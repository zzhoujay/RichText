package com.zzhoujay.richtext.ig;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.zzhoujay.richtext.CacheType;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.R;
import com.zzhoujay.richtext.RichTextConfig;
import com.zzhoujay.richtext.cache.BitmapPool;
import com.zzhoujay.richtext.callback.ImageGetter;
import com.zzhoujay.richtext.callback.ImageLoadNotify;
import com.zzhoujay.richtext.drawable.DrawableSizeHolder;
import com.zzhoujay.richtext.drawable.DrawableWrapper;
import com.zzhoujay.richtext.ext.Base64;
import com.zzhoujay.richtext.ext.TextKit;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.zzhoujay.richtext.ext.Debug.log;

/**
 * Created by zhou on 2016/12/8.
 * RichText默认使用的图片加载器
 * 支持本地图片，Gif图片，图片缓存，图片缩放等等功能
 */
public class DefaultImageGetter implements ImageGetter, ImageLoadNotify {

    public static final String TAG = "DefaultImageGetter";

    public static final String GLOBAL_ID = DefaultImageGetter.class.getName();

    private static final int TASK_TAG = R.id.zhou_default_image_tag_id;


    private final HashSet<Cancelable> tasks;
    private final WeakHashMap<ImageLoader, Cancelable> taskMap;

    private int loadedCount = 0;
    private ImageLoadNotify notify;

    public DefaultImageGetter() {
        tasks = new HashSet<>();
        taskMap = new WeakHashMap<>();
    }

    private void checkTarget(TextView textView) {
        synchronized (DefaultImageGetter.class) {
            //noinspection unchecked
            HashSet<Cancelable> cs = (HashSet<Cancelable>) textView.getTag(TASK_TAG);
            if (cs != null) {
                if (cs == tasks) {
                    return;
                }
                //noinspection SpellCheckingInspection
                HashSet<Cancelable> cancelables = new HashSet<>(cs);
                for (Cancelable cancelable : cancelables) {
                    cancelable.cancel();
                }
                cancelables.clear();
                cs.clear();
            }
            textView.setTag(TASK_TAG, tasks);
        }
    }

    @Override
    public Drawable getDrawable(final ImageHolder holder, final RichTextConfig config, final TextView textView) {

        checkTarget(textView);

        BitmapPool pool = BitmapPool.getPool();
        String key = holder.getKey();
        DrawableSizeHolder sizeHolder = pool.getSizeHolder(key);
        Bitmap bitmap = pool.getBitmap(key);

        final DrawableWrapper drawableWrapper =
                config.cacheType.intValue() > CacheType.none.intValue() && sizeHolder != null ?
                        new DrawableWrapper(sizeHolder) : new DrawableWrapper(holder);

        boolean hasBitmapLocalCache = pool.hasBitmapLocalCache(key);

        Cancelable cancelable = null;
        AbstractImageLoader imageLoader = null;

        try {

            if (config.cacheType.intValue() > CacheType.layout.intValue()) {

                if (bitmap != null) {
                    // 直接从内存中获取
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(textView.getResources(), bitmap);
                    bitmapDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    drawableWrapper.setDrawable(bitmapDrawable);

                    drawableWrapper.calculate();
                    log(TAG, "cache hit -- memory > " + holder.getSource());
                    return drawableWrapper;
                } else if (hasBitmapLocalCache) {
                    // 从缓存中读取
                    InputStream inputStream = pool.readBitmapFromTemp(key);
                    InputStreamImageLoader inputStreamImageLoader = new InputStreamImageLoader(holder, config, textView, drawableWrapper, this, inputStream);
                    Future<?> future = getExecutorService().submit(inputStreamImageLoader);
                    cancelable = new FutureCancelableWrapper(future);
                    imageLoader = inputStreamImageLoader;
                    log(TAG, "cache hit -- local > " + holder.getSource());
                }
            }
            if (imageLoader == null) {
                log(TAG, "cache hit -- none");
                // 无缓存图片，直接加载
                if (Base64.isBase64(holder.getSource())) {
                    // Base64格式图片
                    Base64ImageLoader base64ImageLoader = new Base64ImageLoader(holder, config, textView, drawableWrapper, this);
                    Future<?> future = getExecutorService().submit(base64ImageLoader);
                    cancelable = new FutureCancelableWrapper(future);
                    imageLoader = base64ImageLoader;
                    log(TAG, "image load -- base64 > " + holder.getSource());
                } else if (TextKit.isAssetPath(holder.getSource())) {
                    // Asset文件
                    AssetsImageLoader assetsImageLoader = new AssetsImageLoader(holder, config, textView, drawableWrapper, this);
                    Future<?> future = getExecutorService().submit(assetsImageLoader);
                    cancelable = new FutureCancelableWrapper(future);
                    imageLoader = assetsImageLoader;
                    log(TAG, "image load -- asset > " + holder.getSource());
                } else if (TextKit.isLocalPath(holder.getSource())) {
                    // 本地文件
                    LocalFileImageLoader localFileImageLoader = new LocalFileImageLoader(holder, config, textView, drawableWrapper, this);
                    Future<?> future = getExecutorService().submit(localFileImageLoader);
                    cancelable = new FutureCancelableWrapper(future);
                    imageLoader = localFileImageLoader;
                    log(TAG, "image load -- local > " + holder.getSource());
                } else {
                    // 网络图片
                    CallbackImageLoader callbackImageLoader = new CallbackImageLoader(holder, config, textView, drawableWrapper, this);
                    cancelable = ImageDownloaderManager.getImageDownloaderManager().addTask(holder, config.imageDownloader, callbackImageLoader);
                    imageLoader = callbackImageLoader;
                    log(TAG, "image load -- remote > " + holder.getSource());
                }
            }
        } catch (Exception e) {
            errorHandle(holder, config, textView, drawableWrapper, e);
        }

        if (cancelable != null) {
            addTask(cancelable, imageLoader);
        }

        return drawableWrapper;
    }

    private void errorHandle(ImageHolder holder, RichTextConfig config, TextView textView, DrawableWrapper drawableWrapper, Exception e) {
        AbstractImageLoader imageLoader = new AbstractImageLoader<Object>(holder, config, textView, drawableWrapper, this, null) {

        };
        imageLoader.onFailure(e);
    }


    private void addTask(Cancelable cancelable, AbstractImageLoader imageLoader) {
        synchronized (DefaultImageGetter.class) {
            tasks.add(cancelable);
            taskMap.put(imageLoader, cancelable);
        }
    }

    @Override
    public void registerImageLoadNotify(ImageLoadNotify imageLoadNotify) {
        this.notify = imageLoadNotify;
    }

    @Override
    public void recycle() {
        synchronized (DefaultImageGetter.class) {
            for (Cancelable cancelable : tasks) {
                cancelable.cancel();
            }
            tasks.clear();
            for (Map.Entry<ImageLoader, Cancelable> imageLoaderCancelableEntry : taskMap.entrySet()) {
                imageLoaderCancelableEntry.getKey().recycle();
            }
            taskMap.clear();
        }
    }


    @Override
    public void done(Object from) {
        if (from instanceof AbstractImageLoader) {
            AbstractImageLoader imageLoader = ((AbstractImageLoader) from);
            synchronized (DefaultImageGetter.class) {
                Cancelable cancelable = taskMap.get(imageLoader);
                if (cancelable != null) {
                    tasks.remove(cancelable);
                }
                taskMap.remove(imageLoader);
                loadedCount++;
                if (notify != null) {
                    notify.done(loadedCount);
                }
            }
        }
    }


    private static ExecutorService getExecutorService() {
        return ExecutorServiceHolder.EXECUTOR_SERVICE;
    }


    private static class ExecutorServiceHolder {

        private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    }

}
