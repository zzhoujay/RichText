package com.zzhoujay.richtext.ig;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.widget.TextView;

import com.zzhoujay.richtext.CacheType;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.R;
import com.zzhoujay.richtext.RichTextConfig;
import com.zzhoujay.richtext.callback.ImageGetter;
import com.zzhoujay.richtext.callback.ImageLoadNotify;
import com.zzhoujay.richtext.drawable.DrawableWrapper;
import com.zzhoujay.richtext.ext.Base64;

import java.util.HashSet;
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

    private static final int CALL_TAG = R.id.zhou_default_image_tag_id;
    private static final int FUTURE_TAG = R.id.zhou_default_base64_tag_id;

    private static OkHttpClient client;
    private static ExecutorService executorService;
    private static LruCache<String, Rect> imageBoundCache;
    private static LruCache<String, Bitmap> imageBitmapCache;

    static {
        imageBoundCache = new LruCache<>(20);
        imageBitmapCache = new LruCache<String, Bitmap>(1024 * 1024 * 30) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 && oldValue != null && !oldValue.isRecycled()) {
                    oldValue.recycle();
                }
            }
        };
    }

    private static void cacheBitmap(String source, Bitmap bitmap) {
        imageBitmapCache.put(source, bitmap);
    }

    private static Bitmap loadCacheBitmap(String source) {
        return imageBitmapCache.get(source);
    }

    private static void cacheBound(String source, Rect rect) {
        imageBoundCache.put(source, rect);
    }

    private static Rect loadCacheBound(String source) {
        return imageBoundCache.get(source);
    }

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

    private final HashSet<Call> calls;
    private HashSet<Future> futures;
    private final WeakHashMap<ImageLoader, Call> callMap;
    private WeakHashMap<ImageLoader, Future> futureMap;

    private int loadedCount = 0;
    private ImageLoadNotify notify;

    public DefaultImageGetter() {
        calls = new HashSet<>();
        callMap = new WeakHashMap<>();
    }

    private void checkTarget(TextView textView) {
        //noinspection unchecked
        HashSet<Call> cs = (HashSet<Call>) textView.getTag(CALL_TAG);
        if (cs != null) {
            if (cs == calls) {
                return;
            }
            for (Call c : cs) {
                c.cancel();
            }
            cs.clear();
        }
        textView.setTag(CALL_TAG, calls);
        //noinspection unchecked
        HashSet<Future> fs = (HashSet<Future>) textView.getTag(FUTURE_TAG);
        if (fs != null) {
            if (fs == futures) {
                return;
            }
            for (Future f : fs) {
                if (!f.isCancelled() && !f.isDone()) {
                    f.cancel(true);
                }
            }
            fs.clear();
        }
        if (futures != null) {
            textView.setTag(FUTURE_TAG, futures);
        }
    }

    @Override
    public Drawable getDrawable(final ImageHolder holder, final RichTextConfig config, final TextView textView) {
        final DrawableWrapper drawableWrapper = new DrawableWrapper();
        if (config.cacheType >= CacheType.LAYOUT) {
            Rect rect = loadCacheBound(holder.getSource());
            if (rect != null) {
                holder.setCachedBound(rect);
                drawableWrapper.setBounds(rect);
            }
        } else {
            drawableWrapper.setBounds(0, 0, (int) holder.getScaleWidth(), (int) holder.getScaleHeight());
        }
        if (config.cacheType > CacheType.LAYOUT) {
            Bitmap bitmap = loadCacheBitmap(holder.getSource());
            if (bitmap != null) {
                drawableWrapper.setDrawable(new BitmapDrawable(textView.getResources(), bitmap));
                return drawableWrapper;
            }
        }
        byte[] src = Base64.decode(holder.getSource());
        if (src != null) {
            Base64ImageDecode base64ImageDecode = new Base64ImageDecode(src, holder, config, textView, drawableWrapper, this);
            Future<?> future = getExecutorService().submit(base64ImageDecode);
            getFutures().add(future);
            getFutureMap().put(base64ImageDecode, future);
        } else {
            Request builder = new Request.Builder().url(holder.getSource()).get().build();
            Call call = getClient().newCall(builder);
            checkTarget(textView);
            DefaultCallback callback = new DefaultCallback(holder, config, textView, drawableWrapper, this);
            calls.add(call);
            callMap.put(callback, call);
            call.enqueue(callback);
        }
        return drawableWrapper;
    }

    @Override
    public void registerImageLoadNotify(ImageLoadNotify imageLoadNotify) {
        this.notify = imageLoadNotify;
    }

    @Override
    public void recycle() {
        for (Call call : calls) {
            call.cancel();
        }
        calls.clear();
        callMap.clear();
        if (imageBoundCache.size() > 0) {
            imageBoundCache.evictAll();
        }
        if (imageBitmapCache.size() > 0) {
            imageBitmapCache.evictAll();
        }
        if (futures != null) {
            for (Future future : futures) {
                if (!future.isDone() && !future.isCancelled()) {
                    future.cancel(true);
                }
            }
            futures.clear();
        }
        if (futureMap != null) {
            futureMap.clear();
        }
    }


    @Override
    public void done(Object from) {
        if (from instanceof AbstractImageLoader) {
            AbstractImageLoader imageLoader = ((AbstractImageLoader) from);
            DrawableWrapper drawableWrapper = imageLoader.drawableWrapperWeakReference.get();
            if (drawableWrapper != null) {
                if (imageLoader.config.cacheType > CacheType.NONE) {
                    cacheBound(imageLoader.holder.getSource(), drawableWrapper.getBounds());
                }
                if (imageLoader.config.cacheType > CacheType.LAYOUT) {
                    Drawable drawable = drawableWrapper.getDrawable();
                    if (drawable instanceof BitmapDrawable) {
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                        if (bitmap != null) {
                            cacheBitmap(imageLoader.holder.getSource(), bitmap);
                        }
                    }
                }
            }
            if (imageLoader instanceof DefaultCallback) {
                Call call = callMap.get(imageLoader);
                if (call != null) {
                    calls.remove(call);
                }
                callMap.remove(imageLoader);
            } else if (imageLoader instanceof Base64ImageDecode) {
                if (futureMap != null) {
                    Future future = futureMap.get(imageLoader);
                    if (future != null) {
                        futures.remove(future);
                    }
                    futureMap.remove(imageLoader);
                }
            }
            loadedCount++;
            if (notify != null) {
                notify.done(loadedCount);
            }
        }
    }

    private HashSet<Future> getFutures() {
        if (futures == null) {
            futures = new HashSet<>();
        }
        return futures;
    }

    private WeakHashMap<ImageLoader, Future> getFutureMap() {
        if (futureMap == null) {
            futureMap = new WeakHashMap<>();
        }
        return futureMap;
    }
}
