package com.zzhoujay.richtext.ig;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
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

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by zhou on 2016/12/8.
 * RichText默认使用的图片加载器
 * 支持本地图片，Gif图片，图片缓存，图片缩放等等功能
 */
public class DefaultImageGetter implements ImageGetter, ImageLoadNotify {

    private static final int TASK_TAG = R.id.zhou_default_image_tag_id;


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
        BitmapWrapper.SizeCacheHolder sizeCacheHolder = null;
        Cancelable cancelable = null;
        AbstractImageLoader imageLoader = null;
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
                sizeCacheHolder = bitmapWrapper.getSizeCacheHolder();
            }
        }

        if (sizeCacheHolder == null) {
            // 内存里没有缓存尺寸信息
            drawableWrapper.setBounds(0, 0, holder.getWidth(), holder.getHeight());
            drawableWrapper.setScaleType(ImageHolder.ScaleType.NONE);
        } else {
            drawableWrapper.setBounds(sizeCacheHolder.rect);
            drawableWrapper.setScaleType(sizeCacheHolder.scaleType);
        }

        try {

            // 无缓存图片，直接加载
            if (Base64.isBase64(holder.getSource())) {
                // Base64格式图片
                Base64ImageLoader base64ImageLoader = new Base64ImageLoader(holder, config, textView, drawableWrapper, this, sizeCacheHolder);
                Future<?> future = getExecutorService().submit(base64ImageLoader);
                cancelable = new FutureCancelableWrapper(future);
                imageLoader = base64ImageLoader;
            } else if (TextKit.isLocalPath(holder.getSource())) {
                // 本地文件
                LocalFileImageLoader localFileImageLoader = new LocalFileImageLoader(holder, config, textView, drawableWrapper, this, sizeCacheHolder);
                Future<?> future = getExecutorService().submit(localFileImageLoader);
                cancelable = new FutureCancelableWrapper(future);
                imageLoader = localFileImageLoader;
            } else {
                // 网络图片
                Request builder = new Request.Builder().url(holder.getSource()).get().build();
                Call call = getClient().newCall(builder);
                CallbackImageLoader callback = new CallbackImageLoader(holder, config, textView, drawableWrapper, this, sizeCacheHolder);
                cancelable = new CallCancelableWrapper(call);
                imageLoader = callback;
                call.enqueue(callback);
            }

        } catch (Exception e) {
            errorHandle(holder, config, textView, drawableWrapper, sizeCacheHolder, e);
        }


        checkTarget(textView);

        if (cancelable != null) {
            addTask(cancelable, imageLoader);
        }

        return drawableWrapper;
    }

    private void errorHandle(ImageHolder holder, RichTextConfig config, TextView textView, DrawableWrapper drawableWrapper,
                             BitmapWrapper.SizeCacheHolder sizeCacheHolder, Exception e) {
        AbstractImageLoader imageLoader = new AbstractImageLoader<Object>(holder, config, textView, drawableWrapper, this, null, sizeCacheHolder) {

        };
        imageLoader.onFailure(e);
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
        Bitmap bitmap = bitmapWrapper.getBitmap();
        BitmapDrawable bitmapDrawable = new BitmapDrawable(textView.getResources(), bitmap);
        bitmapDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        drawableWrapper.setDrawable(bitmapDrawable);
        BitmapWrapper.SizeCacheHolder sizeCacheHolder = bitmapWrapper.getSizeCacheHolder();
        drawableWrapper.setBounds(sizeCacheHolder.rect);
        drawableWrapper.setScaleType(sizeCacheHolder.scaleType);
        drawableWrapper.calculate();
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

    private static OkHttpClient getClient() {
        return OkHttpClientHolder.CLIENT;
    }

    private static ExecutorService getExecutorService() {
        return ExecutorServiceHolder.EXECUTOR_SERVICE;
    }

    private static class OkHttpClientHolder {
        private static final OkHttpClient CLIENT;
        private static SSLContext sslContext = null;

        private static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            @SuppressLint("BadHostnameVerifier")
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        static {
            // 设置https为全部信任
            X509TrustManager xtm = new X509TrustManager() {
                @SuppressLint("TrustAllX509TrustManager")
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };


            try {
                sslContext = SSLContext.getInstance("SSL");

                sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
            }

            CLIENT = new OkHttpClient().newBuilder()
                    .sslSocketFactory(sslContext.getSocketFactory(), xtm)
                    .hostnameVerifier(DO_NOT_VERIFY)
                    .build();
        }

    }

    private static class ExecutorServiceHolder {

        private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    }

}
