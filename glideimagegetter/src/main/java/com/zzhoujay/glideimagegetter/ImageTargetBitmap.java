package com.zzhoujay.glideimagegetter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichTextConfig;
import com.zzhoujay.richtext.callback.ImageLoadNotify;
import com.zzhoujay.richtext.drawable.DrawableWrapper;

/**
 * Created by zhou on 16-10-23.
 * ImageTarget Bitmap
 */
class ImageTargetBitmap extends ImageTarget<Bitmap> {


    ImageTargetBitmap(TextView textView, DrawableWrapper drawableWrapper, ImageHolder holder, RichTextConfig config, ImageLoadNotify imageLoadNotify) {
        super(textView, drawableWrapper, holder, config, imageLoadNotify);
    }

    @Override
    public void recycle() {
        Glide.clear(this);
    }

    @Override
    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
        if (!activityIsAlive()) {
            return;
        }
        DrawableWrapper drawableWrapper = urlDrawableWeakReference.get();
        if (drawableWrapper == null) {
            return;
        }
        TextView textView = textViewWeakReference.get();
        holder.setImageState(ImageHolder.ImageState.READY);
        holder.setImageWidth(resource.getWidth());
        holder.setImageHeight(resource.getHeight());
        Drawable drawable = new BitmapDrawable(textView.getContext().getResources(), resource);
        drawableWrapper.setDrawable(drawable);
        if (holder.getCachedBound() != null) {
            drawableWrapper.setBounds(holder.getCachedBound());
        } else {
            if (!config.autoFix && config.imageFixCallback != null) {
                config.imageFixCallback.onFix(holder);
            }
            if (config.autoFix || holder.isAutoFix() || !holder.isInvalidateSize()) {
                int width = getRealWidth();
                int height = (int) ((float) resource.getHeight() * width / resource.getWidth());
                drawableWrapper.setBounds(0, 0, width, height);
            } else {
                drawableWrapper.setBounds(0, 0, (int) holder.getScaleWidth(), (int) holder.getScaleHeight());
            }
        }
        resetText();
        loadDone();
    }
}
