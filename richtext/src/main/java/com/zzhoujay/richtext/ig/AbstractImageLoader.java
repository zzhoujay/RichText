package com.zzhoujay.richtext.ig;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.widget.TintContextWrapper;
import android.widget.TextView;

import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichTextConfig;
import com.zzhoujay.richtext.callback.ImageLoadNotify;
import com.zzhoujay.richtext.drawable.DrawableWrapper;

import java.lang.ref.WeakReference;

/**
 * Created by zhou on 2016/12/9.
 */
abstract class AbstractImageLoader implements ImageLoader {

    final ImageHolder holder;
    final RichTextConfig config;
    final WeakReference<DrawableWrapper> drawableWrapperWeakReference;
    private final WeakReference<TextView> textViewWeakReference;
    private final WeakReference<ImageLoadNotify> notifyWeakReference;

    AbstractImageLoader(ImageHolder holder, RichTextConfig config, TextView textView, DrawableWrapper drawableWrapper, ImageLoadNotify iln) {
        this.holder = holder;
        this.config = config;
        this.textViewWeakReference = new WeakReference<>(textView);
        this.drawableWrapperWeakReference = new WeakReference<>(drawableWrapper);
        this.notifyWeakReference = new WeakReference<>(iln);
        onLoading();
    }

    @Override
    public void onLoading() {
        if (!activityIsAlive()) {
            return;
        }
        DrawableWrapper drawableWrapper = drawableWrapperWeakReference.get();
        if (drawableWrapper == null) {
            return;
        }
        holder.setImageState(ImageHolder.ImageState.LOADING);
        drawableWrapper.setDrawable(config.placeHolder);
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
                int ow = config.placeHolder.getBounds().width();
                if (ow != 0) {
                    height = config.placeHolder.getBounds().height() * width / ow;
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
    }

    @Override
    public int onSizeReady(int width, int height) {
        holder.setImageState(ImageHolder.ImageState.SIZE_READY);
        holder.setImageSize(width, height);
        if (config.imageFixCallback != null) {
            config.imageFixCallback.onFix(holder);
        }
        int exactSampleSize;
        if (holder.getMaxWidth() > 0 && holder.getMaxHeight() > 0) {
            exactSampleSize = getSampleSize(width, height, holder.getMaxWidth(), holder.getMaxHeight());
        } else {
            exactSampleSize = getSampleSize(width, height, getRealWidth(), Integer.MAX_VALUE);
        }
        int powerOfTwoSampleSize = exactSampleSize == 0 ? 0 : Integer.highestOneBit(exactSampleSize);
        return Math.max(1, powerOfTwoSampleSize);
    }

    @Override
    public void onFailure(Exception e) {
        if (!activityIsAlive()) {
            return;
        }
        DrawableWrapper drawableWrapper = drawableWrapperWeakReference.get();
        if (drawableWrapper == null) {
            return;
        }
        holder.setImageState(ImageHolder.ImageState.FAILED);
        holder.setException(e);
        drawableWrapper.setDrawable(config.errorImage);
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
                int ow = config.errorImage.getBounds().width();
                if (ow != 0) {
                    height = config.errorImage.getBounds().height() * width / ow;
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
        done();
    }

    @Override
    public void onResourceReady(Bitmap bitmap) {
        if (bitmap == null) {
            onFailure(new RuntimeException("bitmap decode onFailure"));
            return;
        }
        DrawableWrapper drawableWrapper = drawableWrapperWeakReference.get();
        if (drawableWrapper == null) {
            return;
        }
        TextView textView = textViewWeakReference.get();
        if (textView == null) {
            return;
        }
        holder.setImageState(ImageHolder.ImageState.READY);
        holder.setImageSize(bitmap.getWidth(), bitmap.getHeight());
        holder.setWidth(bitmap.getWidth());
        holder.setHeight(bitmap.getHeight());
        drawableWrapper.setDrawable(new BitmapDrawable(textView.getResources(), bitmap));
        if (holder.getCachedBound() != null) {
            drawableWrapper.setBounds(holder.getCachedBound());
        } else {
            if (!config.autoFix && config.imageFixCallback != null) {
                config.imageFixCallback.onFix(holder);
            }
            if (config.autoFix || holder.isAutoFix() || !holder.isInvalidateSize()) {
                int width = getRealWidth();
                int height = (int) ((float) bitmap.getHeight() * width / bitmap.getWidth());
                drawableWrapper.setBounds(0, 0, width, height);
            } else {
                drawableWrapper.setBounds(0, 0, (int) holder.getScaleWidth(), (int) holder.getScaleHeight());
            }
        }
        resetText();
        done();
    }

    private int getSampleSize(int inWidth, int inHeight, int outWidth, int outHeight) {
        int maxIntegerFactor = (int) Math.ceil(Math.max(inHeight / (float) outHeight,
                inWidth / (float) outWidth));
        int lesserOrEqualSampleSize = Math.max(1, Integer.highestOneBit(maxIntegerFactor));
        return lesserOrEqualSampleSize << (lesserOrEqualSampleSize < maxIntegerFactor ? 1 : 0);
    }

    private boolean activityIsAlive() {
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

    private void resetText() {
        final TextView tv = textViewWeakReference.get();
        if (tv != null) {
            tv.post(new Runnable() {
                @Override
                public void run() {
                    CharSequence cs = tv.getText();
                    tv.setText(cs);
                }
            });
        }
    }

    private void done() {
        ImageLoadNotify imageLoadNotify = notifyWeakReference.get();
        if (imageLoadNotify != null) {
            imageLoadNotify.done(this);
        }
    }

    private int getRealWidth() {
        TextView tv = textViewWeakReference.get();
        if (tv == null) {
            return 0;
        }
        return tv.getWidth() - tv.getPaddingRight() - tv.getPaddingLeft();
    }
}
