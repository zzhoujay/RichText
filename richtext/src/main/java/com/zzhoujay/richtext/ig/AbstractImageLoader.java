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
import com.zzhoujay.richtext.exceptions.ImageDecodeException;

import java.lang.ref.WeakReference;

/**
 * Created by zhou on 2016/12/9.
 * 图片加载器（部分实现版本）
 */
abstract class AbstractImageLoader<T> implements ImageLoader {

    BitmapWrapper.SizeCacheHolder sizeCacheHolder;
    final ImageHolder holder;
    private final RichTextConfig config;
    private final WeakReference<DrawableWrapper> drawableWrapperWeakReference;
    final SourceDecode<T> sourceDecode;
    private final WeakReference<TextView> textViewWeakReference;
    private final WeakReference<ImageLoadNotify> notifyWeakReference;

    private WeakReference<ImageWrapper> imageWrapperWeakReference;

    AbstractImageLoader(ImageHolder holder, RichTextConfig config, TextView textView, DrawableWrapper drawableWrapper, ImageLoadNotify iln, SourceDecode<T> sourceDecode, BitmapWrapper.SizeCacheHolder sizeCacheHolder) {
        this.holder = holder;
        this.config = config;
        this.sourceDecode = sourceDecode;
        this.textViewWeakReference = new WeakReference<>(textView);
        this.drawableWrapperWeakReference = new WeakReference<>(drawableWrapper);
        this.notifyWeakReference = new WeakReference<>(iln);
        this.sizeCacheHolder = sizeCacheHolder;
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
        if (!useCache(drawableWrapper)) {
            if (config.imageFixCallback != null) {
                config.imageFixCallback.onLoading(holder);
            }
            int imageWidth = 0, imageHeight = 0;
            boolean fill = false;
            if (config.placeHolder != null) {
                Rect bounds = config.placeHolder.getBounds();
                imageWidth = bounds.width();
                imageHeight = bounds.height();
                fill = imageWidth <= 0 || imageHeight <= 0;
            }
            int width = getHolderWidth(imageWidth);
            int height = getHolderHeight(imageHeight);
            drawableWrapper.setScaleType(holder.getScaleType());
            drawableWrapper.setBounds(0, 0, width, height);
            drawableWrapper.setBorderHolder(holder.getBorderHolder());
            if (fill) {
                config.placeHolder.setBounds(0, 0, width, height);
            }
        }
        drawableWrapper.calculate();
        resetText();
    }

    @Override
    public int onSizeReady(int width, int height) {
        holder.setImageState(ImageHolder.ImageState.SIZE_READY);
        ImageHolder.SizeHolder sizeHolder = new ImageHolder.SizeHolder(width, height);
        if (config.imageFixCallback != null) {
            config.imageFixCallback.onSizeReady(holder, width, height, sizeHolder);
        }
        int exactSampleSize;
        if (sizeHolder.isInvalidateSize()) {
            exactSampleSize = getSampleSize(width, height, sizeHolder.getWidth(), sizeHolder.getHeight());
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
        if (!useCache(drawableWrapper)) {
            if (config.imageFixCallback != null) {
                config.imageFixCallback.onFailure(holder, e);
            }
            int imageWidth = 0, imageHeight = 0;
            boolean fill = false;
            if (config.errorImage != null) {
                Rect bounds = config.errorImage.getBounds();
                imageWidth = bounds.width();
                imageHeight = bounds.height();
                fill = imageWidth <= 0 || imageHeight <= 0;
            }
            int width = getHolderWidth(imageWidth);
            int height = getHolderHeight(imageHeight);
            drawableWrapper.setScaleType(holder.getScaleType());
            drawableWrapper.setBounds(0, 0, width, height);
            drawableWrapper.setBorderHolder(holder.getBorderHolder());
            if (fill) {
                config.errorImage.setBounds(0, 0, width, height);
            }
        }
        drawableWrapper.calculate();
        resetText();
        done();
    }

    @Override
    public void onResourceReady(ImageWrapper imageWrapper) {
        if (imageWrapper == null) {
            onFailure(new ImageDecodeException());
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
        drawableWrapper.setScaleType(holder.getScaleType());
        drawableWrapper.setDrawable(imageWrapper.getDrawable(textView.getResources()));
        if (!useCache(drawableWrapper)) {
            int imageWidth = imageWrapper.getWidth(), imageHeight = imageWrapper.getHeight();
            if (config.imageFixCallback != null) {
                config.imageFixCallback.onImageReady(this.holder, imageWidth, imageHeight);
            }
            int width = getHolderWidth(imageWidth);
            int height = getHolderHeight(imageHeight);
            drawableWrapper.setScaleType(this.holder.getScaleType());
            drawableWrapper.setBounds(0, 0, width, height);
            drawableWrapper.setBorderHolder(holder.getBorderHolder());
        }
        drawableWrapper.calculate();
        // start gif play
        if (imageWrapper.isGif() && this.holder.isAutoPlay()) {
            imageWrapper.getAsGif().start(textView);
        }
        // cache image
        if (config.cacheType > CacheType.NONE) {
            BitmapPool pool = BitmapPool.getPool();
            BitmapWrapper bw = new BitmapWrapper(this.holder.getKey(),
                    (config.cacheType < CacheType.ALL || imageWrapper.isGif()) ? null : imageWrapper.getAsBitmap(),
                    drawableWrapper.getBounds(), drawableWrapper.getScaleType(), drawableWrapper.getBorderHolder());
            pool.put(this.holder.getKey(), bw);
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

    BitmapWrapper.SizeCacheHolder loadSizeCacheHolder() {
        if (sizeCacheHolder == null && config.cacheType > CacheType.NONE) {
            BitmapWrapper bitmapWrapper = BitmapPool.getPool().read(holder.getKey(), false);
            if (bitmapWrapper != null) {
                BitmapWrapper.SizeCacheHolder holder = bitmapWrapper.getSizeCacheHolder();
                if (holder != null) {
                    this.sizeCacheHolder = holder;
                }
            }
        }
        return sizeCacheHolder;
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

    private int getRealHeight() {
        TextView tv = textViewWeakReference.get();
        if (tv == null) {
            return 0;
        }
        return tv.getHeight() - tv.getPaddingTop() - tv.getPaddingBottom();
    }

    private boolean useCache(DrawableWrapper drawableWrapper) {
        if (config.cacheType > CacheType.NONE) {
            BitmapWrapper.SizeCacheHolder sizeCacheHolder = loadSizeCacheHolder();
            if (sizeCacheHolder != null) {
                drawableWrapper.setBounds(sizeCacheHolder.rect);
                drawableWrapper.setScaleType(sizeCacheHolder.scaleType);
                drawableWrapper.setBorderHolder(sizeCacheHolder.borderHolder);
                return true;
            }
        }
        return false;
    }

    private int getHolderWidth(int width) {
        int w = holder.getWidth();
        if (w == ImageHolder.MATCH_PARENT) {
            w = getRealWidth();
        } else if (w == ImageHolder.WRAP_CONTENT) {
            w = width;
        }
        if (w <= 0) {
            return getRealWidth();
        }
        return w;
    }

    private int getHolderHeight(int height) {
        int h = holder.getHeight();
        if (h == ImageHolder.MATCH_PARENT) {
            h = getRealHeight();
        } else if (h == ImageHolder.WRAP_CONTENT) {
            h = height;
        }
        if (h <= 0) {
            return getRealWidth() / 2;
        }
        return h;
    }
}
