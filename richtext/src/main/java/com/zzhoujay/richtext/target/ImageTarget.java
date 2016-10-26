package com.zzhoujay.richtext.target;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.callback.ImageFixCallback;
import com.zzhoujay.richtext.drawable.URLDrawable;

import java.lang.ref.WeakReference;

/**
 * Created by zhou on 16-10-23.
 * Image target
 */
public abstract class ImageTarget<T> extends SimpleTarget<T> {

    final WeakReference<TextView> textViewWeakReference;
    final WeakReference<URLDrawable> urlDrawableWeakReference;
    protected final ImageHolder holder;
    final boolean autoFix;
    final WeakReference<ImageFixCallback> imageFixCallbackWeakReference;
    private final ImageLoadNotify imageLoadNotify;

    ImageTarget(TextView textView, URLDrawable urlDrawable, ImageHolder holder, boolean autoFix, ImageFixCallback imageFixCallback) {
        this(textView, urlDrawable, holder, autoFix, imageFixCallback, null);
    }

    ImageTarget(TextView textView, URLDrawable urlDrawable, ImageHolder holder, boolean autoFix, ImageFixCallback imageFixCallback, ImageLoadNotify imageLoadNotify) {
        this.textViewWeakReference = new WeakReference<>(textView);
        this.urlDrawableWeakReference = new WeakReference<>(urlDrawable);
        this.holder = holder;
        this.autoFix = autoFix;
        this.imageFixCallbackWeakReference = new WeakReference<>(imageFixCallback);
        this.imageLoadNotify = imageLoadNotify;
    }

    @Override
    public void onLoadStarted(Drawable placeholder) {
        super.onLoadStarted(placeholder);
        int width;
        int height;
        if (holder != null && holder.getHeight() > 0 && holder.getWidth() > 0) {
            width = holder.getWidth();
            height = holder.getHeight();
        } else {
            width = getRealWidth();
            height = placeholder.getBounds().height();
            if (height == 0) {
                height = width / 2;
            }
        }
        placeholder.setBounds(0, 0, width, height);
        URLDrawable urlDrawable = urlDrawableWeakReference.get();
        if (urlDrawable == null) {
            return;
        }
        urlDrawable.setBounds(0, 0, width, height);
        urlDrawable.setDrawable(placeholder);
        resetText();
    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        super.onLoadFailed(e, errorDrawable);
        int width;
        int height;
        if (holder != null && holder.getHeight() > 0 && holder.getWidth() > 0) {
            checkWidth(holder);
            width = holder.getWidth();
            height = holder.getHeight();
        } else {
            width = getRealWidth();
            height = errorDrawable.getBounds().height();
            if (height == 0) {
                height = width / 2;
            }
        }
        errorDrawable.setBounds(0, 0, width, height);
        URLDrawable urlDrawable = urlDrawableWeakReference.get();
        if (urlDrawable == null) {
            return;
        }
        urlDrawable.setBounds(0, 0, width, height);
        urlDrawable.setDrawable(errorDrawable);
        resetText();
        loadDone();
    }

    public abstract void recycle();

    /**
     * 检查图片大小是否超过屏幕
     *
     * @param holder ImageHolder
     */
    void checkWidth(ImageHolder holder) {
        int w = getRealWidth();
        if (holder.getWidth() > w) {
            float r = (float) w / holder.getWidth();
            holder.setHeight((int) (r * holder.getHeight()));
        }
    }

    /**
     * 获取可用宽度
     *
     * @return width
     */
    int getRealWidth() {
        TextView tv = textViewWeakReference.get();
        if (tv == null) {
            return 0;
        }
        return tv.getWidth() - tv.getPaddingRight() - tv.getPaddingLeft();
    }

    void resetText() {
        TextView tv = textViewWeakReference.get();
        if (tv != null) {
            CharSequence cs = tv.getText();
            tv.setText(cs);
        }
    }

    void loadDone() {
        if (imageLoadNotify != null) {
            TextView tv = textViewWeakReference.get();
            CharSequence cs = tv == null ? null : tv.getText();
            imageLoadNotify.done(cs);
        }
    }
}
