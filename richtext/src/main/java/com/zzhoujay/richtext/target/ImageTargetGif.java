package com.zzhoujay.richtext.target;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.callback.ImageFixCallback;
import com.zzhoujay.richtext.drawable.DrawableWrapper;

import java.lang.ref.SoftReference;

/**
 * Created by zhou on 16-10-23.
 * ImageTarget Gif
 */
public class ImageTargetGif extends ImageTarget<GifDrawable> implements Drawable.Callback {

    private SoftReference<GifDrawable> gifDrawableSoftReference;

    @SuppressWarnings("unused")
    public ImageTargetGif(TextView textView, DrawableWrapper drawableWrapper, ImageHolder holder, boolean autoFix, ImageFixCallback imageFixCallback) {
        super(textView, drawableWrapper, holder, autoFix, imageFixCallback);
    }

    public ImageTargetGif(TextView textView, DrawableWrapper drawableWrapper, ImageHolder holder, boolean autoFix, ImageFixCallback imageFixCallback, ImageLoadNotify imageLoadNotify) {
        super(textView, drawableWrapper, holder, autoFix, imageFixCallback, imageLoadNotify);
    }


    @Override
    public void recycle() {
        Glide.clear(this);
        if (gifDrawableSoftReference != null) {
            GifDrawable gifDrawable = gifDrawableSoftReference.get();
            if (gifDrawable != null) {
                gifDrawable.setCallback(null);
                gifDrawable.stop();
            }
        }
    }

    @Override
    public void onResourceReady(GifDrawable resource, GlideAnimation<? super GifDrawable> glideAnimation) {
        if (!activityIsAlive()) {
            return;
        }
        holder.setImageState(ImageHolder.ImageState.READY);
        gifDrawableSoftReference = new SoftReference<>(resource);
        Bitmap first = resource.getFirstFrame();
        holder.setWidth(first.getWidth());
        holder.setHeight(first.getHeight());
        if (!autoFix) {
            ImageFixCallback imageFixCallback = imageFixCallbackWeakReference.get();
            if (imageFixCallback != null) {
                imageFixCallback.onFix(holder);
            } else {
                checkWidth(holder);
            }
        }
        DrawableWrapper drawableWrapper = urlDrawableWeakReference.get();
        if (drawableWrapper == null) {
            return;
        }
        if (autoFix || holder.isAutoFix()) {
            int width = getRealWidth();
            int height = (int) ((float) first.getHeight() * width / first.getWidth());
            drawableWrapper.setBounds(0, 0, width, height);
            resource.setBounds(0, 0, width, height);
        } else {
            resource.setBounds(0, 0, holder.getWidth(), holder.getHeight());
            drawableWrapper.setBounds(0, 0, holder.getWidth(), holder.getHeight());
        }
        drawableWrapper.setDrawable(resource);
        if (holder.isAutoPlay()) {
            resource.setCallback(this);
            resource.start();
            resource.setLoopCount(GlideDrawable.LOOP_FOREVER);
        }
        resetText();
        loadDone();
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable who) {
        TextView textView = textViewWeakReference.get();
        if (textView != null) {
            textView.invalidate();
        } else {
            recycle();
        }
    }

    @Override
    public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {

    }

    @Override
    public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {

    }
}
