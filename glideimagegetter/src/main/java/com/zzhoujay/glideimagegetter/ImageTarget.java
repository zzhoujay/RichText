package com.zzhoujay.glideimagegetter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.TintContextWrapper;
import android.widget.TextView;

import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichTextConfig;
import com.zzhoujay.richtext.callback.ImageLoadNotify;
import com.zzhoujay.richtext.callback.Recyclable;
import com.zzhoujay.richtext.drawable.DrawableWrapper;

import java.lang.ref.WeakReference;

/**
 * Created by zhou on 16-10-23.
 * Image target
 */
abstract class ImageTarget<T> extends BaseTarget<T> implements Recyclable {

    final WeakReference<TextView> textViewWeakReference;
    final WeakReference<DrawableWrapper> urlDrawableWeakReference;
    final ImageHolder holder;
    private final WeakReference<ImageLoadNotify> imageLoadNotifyWeakReference;
    final RichTextConfig config;

    ImageTarget(TextView textView, DrawableWrapper drawableWrapper, ImageHolder holder, RichTextConfig config, ImageLoadNotify imageLoadNotify) {
        this.textViewWeakReference = new WeakReference<>(textView);
        this.urlDrawableWeakReference = new WeakReference<>(drawableWrapper);
        this.holder = holder;
        this.config = config;
        this.imageLoadNotifyWeakReference = new WeakReference<>(imageLoadNotify);
    }

    @Override
    public void onLoadStarted(Drawable placeholder) {
        super.onLoadStarted(placeholder);
        if (placeholder == null || !activityIsAlive()) {
            return;
        }
        DrawableWrapper drawableWrapper = urlDrawableWeakReference.get();
        if (drawableWrapper == null) {
            return;
        }
        holder.setImageState(ImageHolder.ImageState.LOADING);
        drawableWrapper.setDrawable(placeholder);
        if (holder.getCachedBound() != null) {
            drawableWrapper.setBounds(holder.getCachedBound());
        } else {
            if (!config.autoFix && config.imageFixCallback != null) {
                config.imageFixCallback.onFix(holder);
            }
            int width;
            int height = 0;
            if (config.autoFix || holder.isAutoFix() || !holder.isInvalidateSize()) {
                width = getRealWidth();
                int ow = placeholder.getBounds().width();
                if (ow != 0) {
                    height = placeholder.getBounds().height() * width / ow;
                }
                if (height == 0) {
                    height = width / 2;
                }
            } else {
                width = (int) holder.getScaleWidth();
                height = (int) holder.getScaleHeight();
            }
            drawableWrapper.setBounds(0, 0, width, height);
        }
        resetText();
    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        super.onLoadFailed(e, errorDrawable);
        if (errorDrawable == null || !activityIsAlive()) {
            return;
        }
        DrawableWrapper drawableWrapper = urlDrawableWeakReference.get();
        if (drawableWrapper == null) {
            return;
        }
        holder.setImageState(ImageHolder.ImageState.FAILED);
        holder.setException(e);
        drawableWrapper.setDrawable(errorDrawable);
        if (holder.getCachedBound() != null) {
            drawableWrapper.setBounds(holder.getCachedBound());
        } else {
            if (!config.autoFix && config.imageFixCallback != null) {
                config.imageFixCallback.onFix(holder);
            }
            int width;
            int height = 0;
            if (config.autoFix || holder.isAutoFix() || !holder.isInvalidateSize()) {
                width = getRealWidth();
                int ow = errorDrawable.getBounds().width();
                if (ow != 0) {
                    height = errorDrawable.getBounds().height() * width / ow;
                }
                if (height == 0) {
                    height = width / 2;
                }
            } else {
                width = (int) holder.getScaleWidth();
                height = (int) holder.getScaleHeight();
            }
            drawableWrapper.setBounds(0, 0, width, height);
        }
        resetText();
        loadDone();
    }

    @Override
    public void getSize(SizeReadyCallback cb) {
        int maxWidth = getRealWidth(), maxHeight = Integer.MAX_VALUE;
        if (config.imageFixCallback != null) {
            holder.setImageState(ImageHolder.ImageState.SIZE_READY);
            config.imageFixCallback.onFix(holder);
            if (holder.getMaxWidth() > 0 && holder.getMaxHeight() > 0) {
                maxWidth = holder.getMaxWidth();
                maxHeight = holder.getMaxHeight();
            }
        }
        cb.onSizeReady(maxWidth, maxHeight);
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

    int getReadHeight() {
        TextView tv = textViewWeakReference.get();
        if (tv == null) {
            return 0;
        }
        return tv.getHeight() - tv.getPaddingTop() - tv.getPaddingBottom();
    }

    void resetText() {
        TextView tv = textViewWeakReference.get();
        if (tv != null) {
            CharSequence cs = tv.getText();
            tv.setText(cs);
        }
    }

    void loadDone() {
        ImageLoadNotify notify = imageLoadNotifyWeakReference.get();
        if (notify != null) {
            notify.done(this);
        }
    }

    /**
     * 判断Activity是否已经结束
     *
     * @return true：已结束
     */
    boolean activityIsAlive() {
        TextView textView = textViewWeakReference.get();
        if (textView == null) {
            return false;
        }
        Context context = textView.getContext();
        if (context == null) {
            return false;
        }
        if (context instanceof TintContextWrapper) {
            context = ((TintContextWrapper) context).getBaseContext();
        }
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return false;
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && ((Activity) context).isDestroyed()) {
                    return false;
                }
            }
        }
        return true;
    }
}
