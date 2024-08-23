package com.zzhoujay.glideimagegetter;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.collection.LruCache;
import android.widget.TextView;

import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.GifTypeRequest;
import com.bumptech.glide.Glide;
import com.zzhoujay.richtext.CacheType;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichTextConfig;
import com.zzhoujay.richtext.callback.ImageGetter;
import com.zzhoujay.richtext.callback.ImageLoadNotify;
import com.zzhoujay.richtext.drawable.DrawableWrapper;
import com.zzhoujay.richtext.ext.Base64;

import java.util.HashSet;

/**
 * Created by zhou on 2016/12/3.
 * 使用Glide作为图片加载器
 * <p>
 * 不在建议使用，建议使用DefaultImageGetter基本可以替代此类。
 * 此类后面将不会维护了，请尽快转移！
 */
@Deprecated
public class GlideImageGetter implements ImageGetter, ImageLoadNotify {

    private static final int TARGET_TAG = R.id.zhou_glide_image_tag_id;

    private static final LruCache<String, Rect> imageBoundCache;

    static {
        imageBoundCache = new LruCache<>(20);
    }

    private static void cache(String source, Rect rect) {
        imageBoundCache.put(source, rect);
    }

    private static Rect loadCache(String source) {
        return imageBoundCache.get(source);
    }

    private HashSet<ImageTarget> targets;
    private ImageLoadNotify imageLoadNotify;
    private int loadedCount;

    public GlideImageGetter() {
        targets = new HashSet<>();
    }

    private void checkTag(TextView textView) {
        //noinspection unchecked
        HashSet<ImageTarget> ts = (HashSet<ImageTarget>) textView.getTag(TARGET_TAG);
        if (ts != null) {
            if (ts == targets) {
                return;
            }
            for (ImageTarget target : ts) {
                target.recycle();
            }
            ts.clear();
        }
        textView.setTag(TARGET_TAG, targets);
    }

    @Override
    public Drawable getDrawable(ImageHolder holder, final RichTextConfig config, TextView textView) {
        final ImageTarget target;
        final GenericRequestBuilder load;
        DrawableTypeRequest dtr;
        DrawableWrapper drawableWrapper = new DrawableWrapper();
        byte[] src = Base64.decode(holder.getSource());
        if (src != null) {
            dtr = Glide.with(textView.getContext()).load(src);
        } else {
            dtr = Glide.with(textView.getContext()).load(holder.getSource());
        }
        Rect rect = null;
        if (config.cacheType >= CacheType.LAYOUT) {
            rect = loadCache(holder.getSource());
            if (rect != null) {
                drawableWrapper.setBounds(rect);
            }
        } else {
            drawableWrapper.setBounds(0, 0, holder.getWidth(), holder.getHeight());
        }
        if (holder.isGif()) {
            target = new ImageTargetGif(textView, drawableWrapper, holder, config, this, rect);
            load = dtr.asGif();
        } else {
            target = new ImageTargetBitmap(textView, drawableWrapper, holder, config, this, rect);
            load = dtr.asBitmap().atMost();
        }
        checkTag(textView);
        targets.add(target);
        if (!config.resetSize && holder.isInvalidateSize()) {
            load.override(holder.getWidth(), holder.getHeight());
        }
        if (holder.getScaleType() == ImageHolder.ScaleType.CENTER_CROP) {
            if (holder.isGif()) {
                //noinspection ConstantConditions
                ((GifTypeRequest) load).centerCrop();
            } else {
                //noinspection ConstantConditions
                ((BitmapTypeRequest) load).centerCrop();
            }
        } else if (holder.getScaleType() == ImageHolder.ScaleType.FIT_CENTER) {
            if (holder.isGif()) {
                //noinspection ConstantConditions
                ((GifTypeRequest) load).fitCenter();
            } else {
                //noinspection ConstantConditions
                ((BitmapTypeRequest) load).fitCenter();
            }
        }
        textView.post(new Runnable() {
            @Override
            public void run() {
                load.placeholder(config.placeHolder).error(config.errorImage).into(target);
            }
        });
        drawableWrapper.setCallback(textView);
        return drawableWrapper;
    }

    @Override
    public void registerImageLoadNotify(ImageLoadNotify imageLoadNotify) {
        this.imageLoadNotify = imageLoadNotify;
    }

    @Override
    public void done(Object from) {
        if (from instanceof ImageTarget) {
            ImageTarget imageTarget = (ImageTarget) from;
            DrawableWrapper drawableWrapper = (DrawableWrapper) imageTarget.urlDrawableWeakReference.get();
            if (drawableWrapper != null && imageTarget.config.cacheType >= CacheType.LAYOUT) {
                cache(imageTarget.holder.getSource(), drawableWrapper.getBounds());
            }
            targets.remove(imageTarget);
            loadedCount++;
            if (imageLoadNotify != null) {
                imageLoadNotify.done(loadedCount);
            }
        }
    }

    @Override
    public void recycle() {
        for (ImageTarget target : targets) {
            target.recycle();
        }
        targets.clear();
    }
}
