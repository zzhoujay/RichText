package com.zzhoujay.richtext.ig;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.zzhoujay.richtext.CacheType;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichTextConfig;
import com.zzhoujay.richtext.cache.BitmapPool;
import com.zzhoujay.richtext.callback.ImageLoadNotify;
import com.zzhoujay.richtext.drawable.DrawableWrapper;
import com.zzhoujay.richtext.exceptions.ImageDecodeException;
import com.zzhoujay.richtext.ext.ContextKit;

import java.lang.ref.WeakReference;

import static com.zzhoujay.richtext.ext.Debug.log;
import static com.zzhoujay.richtext.ext.Debug.loge;

/**
 * Created by zhou on 2016/12/9.
 * 图片加载器（部分实现版本）
 */
abstract class AbstractImageLoader<T> implements ImageLoader {

    private static final String TAG = "AbstractImageLoader";

    final ImageHolder holder;
    private final RichTextConfig config;
    private final WeakReference<DrawableWrapper> drawableWrapperWeakReference;
    private final SourceDecode<T> sourceDecode;
    private final WeakReference<TextView> textViewWeakReference;
    private final WeakReference<ImageLoadNotify> notifyWeakReference;

    private WeakReference<ImageWrapper> imageWrapperWeakReference;

    AbstractImageLoader(ImageHolder holder, RichTextConfig config, TextView textView, DrawableWrapper drawableWrapper, ImageLoadNotify iln, SourceDecode<T> sourceDecode) {
        this.holder = holder;
        this.config = config;
        this.sourceDecode = sourceDecode;
        this.textViewWeakReference = new WeakReference<>(textView);
        this.drawableWrapperWeakReference = new WeakReference<>(drawableWrapper);
        this.notifyWeakReference = new WeakReference<>(iln);
        onLoading();
    }

    @Override
    public void onLoading() {
        log(TAG, "onLoading > " + holder.getSource());
        if (activityDestroyed()) {
            return;
        }
        DrawableWrapper drawableWrapper = drawableWrapperWeakReference.get();
        if (drawableWrapper == null) {
            return;
        }
        holder.setImageState(ImageHolder.ImageState.LOADING);
        Drawable placeHolder = holder.getPlaceHolder();
        Rect bounds = placeHolder.getBounds();
        drawableWrapper.setDrawable(placeHolder);

        if (config.imageFixCallback != null) {
            config.imageFixCallback.onLoading(holder);
        }

        if (drawableWrapper.isHasCache()) {
            placeHolder.setBounds(drawableWrapper.getBounds());
        } else {
            drawableWrapper.setScaleType(holder.getScaleType());
            drawableWrapper.setBorderHolder(holder.getBorderHolder());
            drawableWrapper.setBounds(0, 0, getHolderWidth(bounds.width()), getHolderHeight(bounds.height()));

            drawableWrapper.calculate();
        }

        resetText();

    }

    @Override
    public int onSizeReady(int width, int height) {
        log(TAG, "onSizeReady > " + "width = " + width + " , height = " + height + " , " + holder.getSource());
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
        loge(TAG, "onFailure > " + holder.getSource(), e);
        if (activityDestroyed()) {
            return;
        }
        DrawableWrapper drawableWrapper = drawableWrapperWeakReference.get();
        if (drawableWrapper == null) {
            return;
        }
        holder.setImageState(ImageHolder.ImageState.FAILED);
        Drawable errorImage = holder.getErrorImage();
        Rect bounds = errorImage.getBounds();
        drawableWrapper.setDrawable(errorImage);

        if (config.imageFixCallback != null) {
            config.imageFixCallback.onFailure(holder, e);
        }

        if (drawableWrapper.isHasCache()) {
            errorImage.setBounds(drawableWrapper.getBounds());
        } else {
            drawableWrapper.setScaleType(holder.getScaleType());
            drawableWrapper.setBounds(0, 0, getHolderWidth(bounds.width()), getHolderHeight(bounds.height()));
            drawableWrapper.setBorderHolder(holder.getBorderHolder());
            drawableWrapper.calculate();
        }

        resetText();
        done();
    }

    @Override
    public void onResourceReady(ImageWrapper imageWrapper) {
        log(TAG, "onResourceReady > " + holder.getSource());
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

        Drawable drawable = imageWrapper.getDrawable(textView.getResources());
        drawableWrapper.setDrawable(drawable);

        int imageWidth = imageWrapper.getWidth(), imageHeight = imageWrapper.getHeight();
        if (config.imageFixCallback != null) {
            config.imageFixCallback.onImageReady(this.holder, imageWidth, imageHeight);
        }

        if (drawableWrapper.isHasCache()) {
            drawable.setBounds(drawableWrapper.getBounds());
        } else {
            drawableWrapper.setScaleType(this.holder.getScaleType());
            drawableWrapper.setBounds(0, 0, getHolderWidth(imageWidth), getHolderHeight(imageHeight));
            drawableWrapper.setBorderHolder(holder.getBorderHolder());

            drawableWrapper.calculate();
        }

        // start gif play
        if (imageWrapper.isGif() && this.holder.isAutoPlay()) {
            imageWrapper.getAsGif().start(textView);
        }
        // cache size
        BitmapPool pool = BitmapPool.getPool();
        String key = holder.getKey();
        if (config.cacheType.intValue() > CacheType.none.intValue() && !drawableWrapper.isHasCache()) {
            pool.cacheSize(key, drawableWrapper.getSizeHolder());
        }

        // cache image

        if (config.cacheType.intValue() > CacheType.layout.intValue() && !imageWrapper.isGif()) {
            pool.cacheBitmap(key, imageWrapper.getAsBitmap());
        }

        // reset TextView
        resetText();
        done();
    }

    private int[] getDimensions(T t, BitmapFactory.Options options) {
        options.inJustDecodeBounds = true;
        sourceDecode.decodeSize(t, options);
        options.inJustDecodeBounds = false;
        return new int[]{options.outWidth, options.outHeight};
    }

    @Override
    public void recycle() {
        if (imageWrapperWeakReference != null) {
            ImageWrapper imageWrapper = imageWrapperWeakReference.get();
            if (imageWrapper != null) {
                imageWrapper.recycle();
            }
        }
    }

    private static int getSampleSize(int inWidth, int inHeight, int outWidth, int outHeight) {
        int maxIntegerFactor = (int) Math.ceil(Math.max(inHeight / (float) outHeight,
                inWidth / (float) outWidth));
        int lesserOrEqualSampleSize = Math.max(1, Integer.highestOneBit(maxIntegerFactor));
        return lesserOrEqualSampleSize << (lesserOrEqualSampleSize < maxIntegerFactor ? 1 : 0);
    }

    // 执行图片加载，并在加载成功后调用onResourceReady
    void doLoadImage(T t) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        int[] inDimens = getDimensions(t, options);
        options.inSampleSize = onSizeReady(inDimens[0], inDimens[1]);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        onResourceReady(sourceDecode.decode(holder, t, options));
    }

    private boolean activityDestroyed() {
        TextView textView = textViewWeakReference.get();
        if (textView == null) {
            loge(TAG, "textView is recycle");
            return true;
        }
        Context context = textView.getContext();
        boolean isAlive = ContextKit.activityIsAlive(context);
        if (!isAlive) {
            loge(TAG, "activity is destroy");
        }
        return !isAlive;
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

    private int getHolderWidth(int width) {
        int w = holder.getWidth();
        if (w == ImageHolder.MATCH_PARENT) {
            w = getRealWidth();
        } else if (w == ImageHolder.WRAP_CONTENT) {
            w = width;
        }
//        if (w <= 0) {
//            return getRealWidth();
//        }
        return w;
    }

    private int getHolderHeight(int height) {
        int h = holder.getHeight();
        if (h == ImageHolder.MATCH_PARENT) {
            h = getRealHeight();
        } else if (h == ImageHolder.WRAP_CONTENT) {
            h = height;
        }
//        if (h <= 0) {
//            return getRealWidth() / 2;
//        }
        return h;
    }
}
