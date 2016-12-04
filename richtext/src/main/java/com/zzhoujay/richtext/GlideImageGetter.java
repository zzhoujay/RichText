package com.zzhoujay.richtext;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.GifTypeRequest;
import com.bumptech.glide.Glide;
import com.zzhoujay.richtext.callback.ImageGetter;
import com.zzhoujay.richtext.drawable.DrawableWrapper;
import com.zzhoujay.richtext.ext.Base64;
import com.zzhoujay.richtext.target.ImageLoadNotify;
import com.zzhoujay.richtext.target.ImageTarget;
import com.zzhoujay.richtext.target.ImageTargetBitmap;
import com.zzhoujay.richtext.target.ImageTargetGif;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by zhou on 2016/12/3.
 * 使用Glide作为图片加载器
 */
public class GlideImageGetter implements ImageGetter, ImageLoadNotify {

    private static final int TARGET_TAG = System.identityHashCode(GlideImageGetter.class);

    private static final HashMap<String, Rect> rectCache;

    static {
        rectCache = new HashMap<>();
    }

    private static void cache(String source, Rect rect) {
        rectCache.put(source, rect);
    }

    private static Rect loadCache(String source) {
        return rectCache.get(source);
    }

    private HashSet<ImageTarget> targets;

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
        }
        textView.setTag(TARGET_TAG, targets);
    }

    @Override
    public Drawable getDrawable(ImageHolder holder, final RichTextConfig config, TextView textView) {
        final DrawableWrapper drawableWrapper = new DrawableWrapper();
        final ImageTarget target;
        final GenericRequestBuilder load;
        DrawableTypeRequest dtr;
        byte[] src = Base64.decode(holder.getSource());
        if (src != null) {
            dtr = Glide.with(textView.getContext()).load(src);
        } else {
            dtr = Glide.with(textView.getContext()).load(holder.getSource());
        }
        if (holder.isGif()) {
            target = new ImageTargetGif(textView, drawableWrapper, holder, config, this);
            load = dtr.asGif();
        } else {
            target = new ImageTargetBitmap(textView, drawableWrapper, holder, config, this);
            load = dtr.asBitmap().atMost();
        }
        checkTag(textView);
        targets.add(target);
        if (!config.resetSize && holder.isInvalidateSize()) {
            load.override((int) holder.getScaleWidth(), (int) holder.getScaleHeight());
        }
        if (config.cache) {
            Rect rect = loadCache(holder.getSource());
            if (rect != null) {
                holder.setCachedBound(rect);
                drawableWrapper.setBounds(rect);
            }
        } else {
            drawableWrapper.setBounds(0, 0, (int) holder.getScaleWidth(), (int) holder.getScaleHeight());
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
                setPlaceHolder(load, config);
                setErrorImage(load, config);
                load.into(target);
            }
        });
        return drawableWrapper;
    }

    private static void setPlaceHolder(GenericRequestBuilder load, RichTextConfig config) {
        if (config.placeHolderRes > 0) {
            load.placeholder(config.placeHolderRes);
        } else {
            load.placeholder(config.placeHolder);
        }
    }

    private static void setErrorImage(GenericRequestBuilder load, RichTextConfig config) {
        if (config.errorImageRes > 0) {
            load.error(config.errorImageRes);
        } else {
            load.error(config.errorImage);
        }
    }

    @Override
    public void done(ImageTarget target, Object... args) {
        if (args.length >= 3) {
            ImageHolder holder = (ImageHolder) args[0];
            RichTextConfig config = (RichTextConfig) args[1];
            Rect rect = (Rect) args[2];
            if (config.cache) {
                holder.setCachedBound(rect);
                cache(holder.getSource(), rect);
            }
        }
        targets.remove(target);
    }

    @Override
    public void recycle() {
        for (ImageTarget target : targets) {
            target.recycle();
        }
        targets.clear();
    }
}
