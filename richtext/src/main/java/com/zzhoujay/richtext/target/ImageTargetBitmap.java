package com.zzhoujay.richtext.target;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.callback.ImageFixCallback;
import com.zzhoujay.richtext.drawable.URLDrawable;

/**
 * Created by zhou on 16-10-23.
 * ImageTarget Bitmap
 */
public class ImageTargetBitmap extends ImageTarget<Bitmap> {


    @SuppressWarnings("unused")
    public ImageTargetBitmap(TextView textView, URLDrawable urlDrawable, ImageHolder holder, boolean autoFix, ImageFixCallback imageFixCallback) {
        super(textView, urlDrawable, holder, autoFix, imageFixCallback);
    }

    public ImageTargetBitmap(TextView textView, URLDrawable urlDrawable, ImageHolder holder, boolean autoFix, ImageFixCallback imageFixCallback, ImageLoadNotify imageLoadNotify) {
        super(textView, urlDrawable, holder, autoFix, imageFixCallback, imageLoadNotify);
    }

    @Override
    public void recycle() {
        Glide.clear(this);
    }

    @Override
    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
        TextView textView = textViewWeakReference.get();
        if (textView == null) {
            return;
        }
        Drawable drawable = new BitmapDrawable(textView.getContext().getResources(), resource);
        if (!autoFix && (holder.getWidth() <= 0 || holder.getHeight() <= 0)) {
            holder.setWidth(resource.getWidth());
            holder.setHeight(resource.getHeight());
            ImageFixCallback imageFixCallback = imageFixCallbackWeakReference.get();
            if (imageFixCallback != null) {
                imageFixCallback.onFix(holder, true);
            } else {
                checkWidth(holder);
            }
        }
        URLDrawable urlDrawable = urlDrawableWeakReference.get();
        if (urlDrawable == null) {
            return;
        }
        if (autoFix || holder.isAutoFix()) {
            int width = getRealWidth();
            int height = (int) ((float) resource.getHeight() * width / resource.getWidth());
            urlDrawable.setBounds(0, 0, width, height);
            drawable.setBounds(0, 0, width, height);
        } else {
            drawable.setBounds(0, 0, holder.getWidth(), holder.getHeight());
            urlDrawable.setBounds(0, 0, holder.getWidth(), holder.getHeight());
        }
        urlDrawable.setDrawable(drawable);
        resetText();
        loadDone();
    }
}
