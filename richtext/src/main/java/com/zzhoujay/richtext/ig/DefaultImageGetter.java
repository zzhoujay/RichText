package com.zzhoujay.richtext.ig;

import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.zzhoujay.richtext.CacheType;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.R;
import com.zzhoujay.richtext.RichTextConfig;
import com.zzhoujay.richtext.callback.ImageGetter;
import com.zzhoujay.richtext.callback.ImageLoadNotify;
import com.zzhoujay.richtext.drawable.DrawableWrapper;
import com.zzhoujay.richtext.ext.Base64;
import com.zzhoujay.richtext.ext.TextKit;

import java.util.HashSet;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by zhou on 2016/12/8.
 */
public class DefaultImageGetter implements ImageGetter, ImageLoadNotify {

    private static final int TASK_TAG = R.id.zhou_default_image_tag_id;

    private static OkHttpClient client;
    private static ExecutorService executorService;


    private static OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient();
        }
        return client;
    }

    private static ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
        }
        return executorService;
    }

    private final HashSet<Cancelable> tasks;
    private final WeakHashMap<ImageLoader, Cancelable> taskMap;
    private final Object lock;

    private int loadedCount = 0;
    private ImageLoadNotify notify;

    public DefaultImageGetter() {
        lock = new Object();
        tasks = new HashSet<>();
        taskMap = new WeakHashMap<>();
    }

    private void checkTarget(TextView textView) {
        synchronized (lock) {
            //noinspection unchecked
            HashSet<Cancelable> cs = (HashSet<Cancelable>) textView.getTag(TASK_TAG);
            if (cs != null) {
                if (cs == tasks) {
                    return;
                }
                for (Cancelable c : cs) {
                    c.cancel();
                }
                cs.clear();
            }
            textView.setTag(TASK_TAG, tasks);
        }
    }

    @Override
    public Drawable getDrawable(final ImageHolder holder, final RichTextConfig config, final TextView textView) {
        final DrawableWrapper drawableWrapper = new DrawableWrapper();
        int hit = BitmapPool.getPool().hit(holder.getKey());
        Rect rect = null;
        Cancelable cancelable;
        AbstractImageLoader imageLoader;
        if (config.cacheType >= CacheType.ALL) {
            if (hit >= 3) {
                // 直接从内存中读取
                return loadFromMemory(holder, textView, drawableWrapper);
            } else if (hit == 1) {
                // 从磁盘读取
                return loadFromLocalDisk(holder, config, textView, drawableWrapper);
            }
        } else if (config.cacheType >= CacheType.LAYOUT) {
            if (hit >= 2) {
                // 内存中有尺寸信息
                BitmapWrapper bitmapWrapper = BitmapPool.getPool().get(holder.getKey(), false, false);
                rect = bitmapWrapper.getRect();
            }
        }

        if (rect == null) {
            // 内存里没有缓存尺寸信息
            drawableWrapper.setBounds(0, 0, (int) holder.getScaleWidth(), (int) holder.getScaleHeight());
        } else {
            drawableWrapper.setBounds(rect);
        }


        // 无缓存图片，直接加载
        if (Base64.isBase64(holder.getSource())) {
            // Base64格式图片
            Base64ImageLoader base64ImageLoader = new Base64ImageLoader(holder, config, textView, drawableWrapper, this, rect);
            Future<?> future = getExecutorService().submit(base64ImageLoader);
            cancelable = new FutureCancelableWrapper(future);
            imageLoader = base64ImageLoader;
        } else if (TextKit.isLocalPath(holder.getSource())) {
            // 本地文件
            LocalFileImageLoader localFileImageLoader = new LocalFileImageLoader(holder, config, textView, drawableWrapper, this, rect);
            Future<?> future = getExecutorService().submit(localFileImageLoader);
            cancelable = new FutureCancelableWrapper(future);
            imageLoader = localFileImageLoader;
        } else {
            // 网络图片
            Request builder = new Request.Builder().url(holder.getSource()).get().build();
            Call call = getClient().newCall(builder);
            CallbackImageLoader callback = new CallbackImageLoader(holder, config, textView, drawableWrapper, this, rect);
            cancelable = new CallCancelableWrapper(call);
            imageLoader = callback;
            call.enqueue(callback);
        }

        checkTarget(textView);
        addTask(cancelable, imageLoader);
        return drawableWrapper;
    }

    private Drawable loadFromLocalDisk(ImageHolder holder, RichTextConfig config, TextView textView, DrawableWrapper drawableWrapper) {
        Cancelable cancelable;
        AbstractImageLoader imageLoader;
        LocalDiskCachedImageLoader localDiskCachedImageLoader = new LocalDiskCachedImageLoader(holder, config, textView, drawableWrapper, this);
        Future<?> future = getExecutorService().submit(localDiskCachedImageLoader);
        cancelable = new FutureCancelableWrapper(future);
        imageLoader = localDiskCachedImageLoader;
        checkTarget(textView);
        addTask(cancelable, imageLoader);
        return drawableWrapper;
    }

    private void addTask(Cancelable cancelable, AbstractImageLoader imageLoader) {
        synchronized (lock) {
            tasks.add(cancelable);
            taskMap.put(imageLoader, cancelable);
        }
    }

    @NonNull
    private Drawable loadFromMemory(ImageHolder holder, TextView textView, DrawableWrapper drawableWrapper) {
        BitmapWrapper bitmapWrapper = BitmapPool.getPool().get(holder.getKey(), false, true);
        drawableWrapper.setDrawable(new BitmapDrawable(textView.getResources(), bitmapWrapper.getBitmap()));
        drawableWrapper.setBounds(bitmapWrapper.getRect());
        return drawableWrapper;
    }

    @Override
    public void registerImageLoadNotify(ImageLoadNotify imageLoadNotify) {
        this.notify = imageLoadNotify;
    }

    @Override
    public void recycle() {
        synchronized (lock) {
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
            synchronized (lock) {
                Cancelable cancelable = taskMap.get(imageLoader);
                if (cancelable != null) {
                    tasks.remove(cancelable);
                }
                taskMap.remove(imageLoader);
            }
            loadedCount++;
            if (notify != null) {
                notify.done(loadedCount);
            }
        }
    }


}
