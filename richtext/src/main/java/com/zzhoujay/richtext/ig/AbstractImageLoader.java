package com.zzhoujay.richtext.ig;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.widget.TintContextWrapper;
import android.widget.TextView;

import com.zzhoujay.richtext.CacheType;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichTextConfig;
import com.zzhoujay.richtext.callback.ImageLoadNotify;
import com.zzhoujay.richtext.drawable.DrawableWrapper;

import java.lang.ref.WeakReference;

/**
 * Created by zhou on 2016/12/9.
 */
abstract class AbstractImageLoader<T> implements ImageLoader {

    Rect border;
    final ImageHolder holder;
    final RichTextConfig config;
    final WeakReference<DrawableWrapper> drawableWrapperWeakReference;
    final SourceDecode<T> sourceDecode;
    private final WeakReference<TextView> textViewWeakReference;
    private final WeakReference<ImageLoadNotify> notifyWeakReference;

    private WeakReference<ImageWrapper> imageWrapperWeakReference;

    AbstractImageLoader(ImageHolder holder, RichTextConfig config, TextView textView, DrawableWrapper drawableWrapper, ImageLoadNotify iln, SourceDecode<T> sourceDecode, Rect border) {
        this.holder = holder;
        this.config = config;
        this.sourceDecode = sourceDecode;
        this.textViewWeakReference = new WeakReference<>(textView);
        this.drawableWrapperWeakReference = new WeakReference<>(drawableWrapper);
        this.notifyWeakReference = new WeakReference<>(iln);
        this.border = border;
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
        Rect cachedBorder = loadCachedBorder();
        if (cachedBorder != null && config.cacheType > CacheType.NONE) {
            drawableWrapper.setBounds(cachedBorder);
        } else {
            if (!config.autoFix && config.imageFixCallback != null) {
                config.imageFixCallback.onLoading(holder);
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
        if (config.imageFixCallback != null) {
            config.imageFixCallback.onSizeReady(holder, width, height);
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
        drawableWrapper.setDrawable(config.errorImage);
        Rect rect = loadCachedBorder();
        if (rect != null && config.cacheType > CacheType.NONE) {
            drawableWrapper.setBounds(rect);
        } else {
            if (!config.autoFix && config.imageFixCallback != null) {
                config.imageFixCallback.onFailure(holder, e);
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
    public void onResourceReady(ImageWrapper imageWrapper) {
        if (imageWrapper == null) {
            onFailure(new RuntimeException("image decodeAsBitmap onFailure"));
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
        imageWrapperWeakReference = new WeakReference<>(imageWrapper);
        holder.setImageState(ImageHolder.ImageState.READY);
        holder.setSize(imageWrapper.getWidth(), imageWrapper.getHeight());
        drawableWrapper.setDrawable(imageWrapper.getDrawable(textView.getResources()));
        Rect rect = loadCachedBorder();
        if (config.cacheType > CacheType.NONE && rect != null) {
            drawableWrapper.setBounds(rect);
        } else {
            if (!config.autoFix && config.imageFixCallback != null) {
                config.imageFixCallback.onImageReady(holder, imageWrapper.getWidth(), imageWrapper.getHeight());
            }
            if (config.autoFix || holder.isAutoFix() || !holder.isInvalidateSize()) {
                int width = getRealWidth();
                int height = (int) ((float) imageWrapper.getHeight() * width / imageWrapper.getWidth());
                drawableWrapper.setBounds(0, 0, width, height);
            } else {
                drawableWrapper.setBounds(0, 0, (int) holder.getScaleWidth(), (int) holder.getScaleHeight());
            }
        }
        // start gif play
        if (imageWrapper.isGif() && holder.isAutoPlay()) {
            imageWrapper.getAsGif().start(textView);
        }
        // cache image
        if (config.cacheType > CacheType.NONE) {
            BitmapPool pool = BitmapPool.getPool();
            BitmapWrapper bw = new BitmapWrapper(holder.getKey(),
                    (config.cacheType < CacheType.ALL || imageWrapper.isGif()) ? null : imageWrapper.getAsBitmap(),
                    drawableWrapper.getBounds());
            pool.put(holder.getKey(), bw);
        }
        // reset TextView
        resetText();
        done();
    }

    int[] getDimensions(T t, BitmapFactory.Options options) {
        options.inJustDecodeBounds = true;
        sourceDecode.decodeSize(t, options);
        options.inJustDecodeBounds = false;
        return new int[]{options.outWidth, options.outHeight};
    }

    Rect loadCachedBorder() {
        if (border == null && config.cacheType > CacheType.NONE) {
            BitmapWrapper bitmapWrapper = BitmapWrapper.read(BitmapPool.getCacheDir(), holder.getKey(), false);
            if (bitmapWrapper != null) {
                Rect rect = bitmapWrapper.getRect();
                if (rect != null) {
                    border = rect;
                }
            }
        }
        return border;
    }

    @Override
    public void recycle() {
        if (imageWrapperWeakReference != null) {
            imageWrapperWeakReference.get().recycle();
        }
    }

    static int getSampleSize(int inWidth, int inHeight, int outWidth, int outHeight) {
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
