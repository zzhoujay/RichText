package com.zzhoujay.richtext.target;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.TintContextWrapper;
import android.widget.TextView;

import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.callback.ImageFixCallback;
import com.zzhoujay.richtext.callback.Recyclable;
import com.zzhoujay.richtext.drawable.DrawableWrapper;

import java.lang.ref.WeakReference;

/**
 * Created by zhou on 16-10-23.
 * Image target
 */
public abstract class ImageTarget<T> extends BaseTarget<T> implements Recyclable {

    final WeakReference<TextView> textViewWeakReference;
    final WeakReference<DrawableWrapper> urlDrawableWeakReference;
    protected final ImageHolder holder;
    final boolean autoFix;
    final WeakReference<ImageFixCallback> imageFixCallbackWeakReference;
    private final WeakReference<ImageLoadNotify> imageLoadNotifyWeakReference;

    ImageTarget(TextView textView, DrawableWrapper drawableWrapper, ImageHolder holder, boolean autoFix, ImageFixCallback imageFixCallback) {
        this(textView, drawableWrapper, holder, autoFix, imageFixCallback, null);
    }

    ImageTarget(TextView textView, DrawableWrapper drawableWrapper, ImageHolder holder, boolean autoFix, ImageFixCallback imageFixCallback, ImageLoadNotify imageLoadNotify) {
        this.textViewWeakReference = new WeakReference<>(textView);
        this.urlDrawableWeakReference = new WeakReference<>(drawableWrapper);
        this.holder = holder;
        this.autoFix = autoFix;
        this.imageFixCallbackWeakReference = new WeakReference<>(imageFixCallback);
        this.imageLoadNotifyWeakReference = new WeakReference<>(imageLoadNotify);
    }

    @Override
    public void onLoadStarted(Drawable placeholder) {
        super.onLoadStarted(placeholder);
        if (placeholder == null || !activityIsAlive()) {
            return;
        }
        holder.setImageState(ImageHolder.ImageState.LOADING);
        ImageFixCallback imageFixCallback = imageFixCallbackWeakReference.get();
        if (!autoFix && imageFixCallback != null) {
            imageFixCallback.onFix(holder);
        }
        int width;
        int height;
        if (holder.getHeight() > 0 && holder.getWidth() > 0) {
            width = (int) (holder.getWidth() * holder.getScale());
            height = (int) (holder.getHeight() * holder.getScale());
        } else {
            width = getRealWidth();
            height = placeholder.getBounds().height();
            if (height == 0) {
                height = width / 2;
            }
        }
        placeholder.setBounds(0, 0, width, height);
        DrawableWrapper drawableWrapper = urlDrawableWeakReference.get();
        if (drawableWrapper == null) {
            return;
        }
        drawableWrapper.setBounds(0, 0, width, height);
        drawableWrapper.setDrawable(placeholder);
        resetText();
    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        super.onLoadFailed(e, errorDrawable);
        if (errorDrawable == null || !activityIsAlive()) {
            return;
        }
        holder.setImageState(ImageHolder.ImageState.FAILED);
        holder.setException(e);
        ImageFixCallback imageFixCallback = imageFixCallbackWeakReference.get();
        if (!autoFix && imageFixCallback != null) {
            imageFixCallback.onFix(holder);
        }
        int width;
        int height;
        if (holder.getHeight() > 0 && holder.getWidth() > 0) {
            checkWidth(holder);
            width = (int) (holder.getWidth() * holder.getScale());
            height = (int) (holder.getHeight() * holder.getScale());
        } else {
            width = getRealWidth();
            height = errorDrawable.getBounds().height();
            if (height == 0) {
                height = width / 2;
            }
        }
        errorDrawable.setBounds(0, 0, width, height);
        DrawableWrapper drawableWrapper = urlDrawableWeakReference.get();
        if (drawableWrapper == null) {
            return;
        }
        drawableWrapper.setBounds(0, 0, width, height);
        drawableWrapper.setDrawable(errorDrawable);
        resetText();
        loadDone();
    }

    @Override
    public void getSize(SizeReadyCallback cb) {
        ImageFixCallback imageFixCallback = imageFixCallbackWeakReference.get();
        int maxWidth = getRealWidth(), maxHeight = Integer.MAX_VALUE;
        if (imageFixCallback != null) {
            holder.setImageState(ImageHolder.ImageState.SIZE_READY);
            imageFixCallback.onFix(holder);
            if (holder.getMaxWidth() > 0 && holder.getMaxHeight() > 0) {
                maxWidth = holder.getMaxWidth();
                maxHeight = holder.getMaxHeight();
            }
        }
        cb.onSizeReady(maxWidth, maxHeight);
    }

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
        if (imageLoadNotifyWeakReference != null) {
            ImageLoadNotify notify = imageLoadNotifyWeakReference.get();
            if (notify != null) {
                notify.done(this);
            }
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
