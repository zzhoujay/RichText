package com.zzhoujay.richtext;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.zzhoujay.richtext.drawable.DrawableBorderHolder;
import com.zzhoujay.richtext.exceptions.ResetImageSourceException;
import com.zzhoujay.richtext.ext.MD5;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by zhou on 16-5-28.
 * ImageHolder
 */
//@SuppressWarnings("ALL")
@SuppressWarnings("unused")
public class ImageHolder {

    public static final int WRAP_CONTENT = Integer.MIN_VALUE;
    public static final int MATCH_PARENT = Integer.MAX_VALUE;

    /**
     * ScaleType
     */
//    @IntDef({ScaleType.NONE, ScaleType.CENTER, ScaleType.CENTER_CROP, ScaleType.CENTER_INSIDE, ScaleType.FIT_START,
//            ScaleType.FIT_END, ScaleType.FIT_CENTER, ScaleType.FIT_XY, ScaleType.FIT_AUTO})
//    @Retention(RetentionPolicy.SOURCE)
    public enum ScaleType {
        none(0), center(1), center_crop(2), center_inside(3), fit_center(4), fit_start(5), fit_end(6),
        fit_xy(7), fit_auto(8);

        final int value;

        ScaleType(int value) {
            this.value = value;
        }

        public int intValue() {
            return value;
        }

        public static ScaleType valueOf(int value) {
            return values()[value];
        }

    }

    /**
     * ImageState 图片的加载状态
     * INIT: 初始化加载，可以设置图片宽高给Glide
     * LOADING: 加载中，设置placeholder图片的宽高
     * READY: 图片加载成功，设置最终显示的图片的宽高
     * FAILED: 加载失败，设置加载失败的图片的宽高
     */
    @IntDef({ImageState.INIT, ImageState.LOADING, ImageState.READY, ImageState.FAILED, ImageState.SIZE_READY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ImageState {
        int INIT = 0;
        int LOADING = 1;
        int READY = 2;
        int FAILED = 3;
        int SIZE_READY = 4;
    }

    @SuppressWarnings("unused")
    public static class SizeHolder {

        private int width, height;
        private float scale;

        public SizeHolder(int width, int height) {
            this.width = width;
            this.height = height;
            this.scale = 1;
        }

        public void setScale(float scale) {
            this.scale = scale;
        }

        public void setSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return (int) (scale * width);
        }

        public int getHeight() {
            return (int) (scale * height);
        }

        public boolean isInvalidateSize() {
            return scale > 0 && width > 0 && height > 0;
        }

    }


    private String source; // 图片URL
    private String key;
    private final int position; // 图片在在某个富文本中的位置
    private int width, height; // 和scale属性共同决定holder宽高，开发者设置，内部获取值然后进行相应的设置
    private ScaleType scaleType;
    @ImageState
    private int imageState; // 图片加载的状态
    private boolean autoFix;
    private boolean autoPlay;
    private boolean show;
    private boolean isGif;
    private final DrawableBorderHolder borderHolder;
    private Drawable placeHolder, errorImage;
    private final String prefixCode;
    private final int configHashcode;

    public ImageHolder(String source, int position, RichTextConfig config, TextView textView) {
        this.source = source;
        this.position = position;
        this.isGif = false;
        this.configHashcode = config.key();

        prefixCode = config.imageDownloader == null ? "" : config.imageDownloader.getClass().getName();

        generateKey();
        this.autoPlay = config.autoPlay;
        if (config.autoFix) {
            width = MATCH_PARENT;
            height = WRAP_CONTENT;
            scaleType = ScaleType.fit_auto;
        } else {
            scaleType = config.scaleType;
            width = config.width;
            height = config.height;
        }
        this.show = !config.noImage;
        this.borderHolder = new DrawableBorderHolder(config.borderHolder);

        this.placeHolder = config.placeHolderDrawableGetter.getDrawable(this, config, textView);
        this.errorImage = config.errorImageDrawableGetter.getDrawable(this, config, textView);
    }

    private void generateKey() {
        this.key = MD5.generate(prefixCode + configHashcode + source);
    }

    public void setSource(String source) {
        if (imageState != ImageState.INIT) {
            throw new ResetImageSourceException();
        }
        this.source = source;
        generateKey();
    }

    @SuppressWarnings("unused")
    public boolean success() {
        return imageState == ImageState.READY;
    }

    @SuppressWarnings("unused")
    public boolean failed() {
        return imageState == ImageState.FAILED;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public String getKey() {
        return key;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @SuppressWarnings("unused")
    public int getPosition() {
        return position;
    }

    public String getSource() {
        return source;
    }

    public boolean isAutoFix() {
        return autoFix;
    }

    @SuppressWarnings("SameParameterValue")
    public void setAutoFix(boolean autoFix) {
        this.autoFix = autoFix;
        if (autoFix) {
            width = MATCH_PARENT;
            height = WRAP_CONTENT;
            scaleType = ScaleType.fit_auto;
        } else {
            width = WRAP_CONTENT;
            height = WRAP_CONTENT;
            scaleType = ScaleType.none;
        }
//        checkSize();
    }

    public ScaleType getScaleType() {
        return scaleType;
    }

    @SuppressWarnings("unused")
    public void setScaleType(ScaleType scaleType) {
        this.scaleType = scaleType;
    }

    public boolean isGif() {
        return isGif;
    }

    public void setIsGif(boolean isGif) {
        this.isGif = isGif;
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    @ImageState
    public int getImageState() {
        return imageState;
    }

    public void setImageState(@ImageState int imageState) {
        this.imageState = imageState;
    }

    public boolean isInvalidateSize() {
        return width > 0 && height > 0;
    }

    public DrawableBorderHolder getBorderHolder() {
        return borderHolder;
    }

    public void setShowBorder(boolean showBorder) {
        this.borderHolder.setShowBorder(showBorder);
    }

    public void setBorderSize(float borderSize) {
        this.borderHolder.setBorderSize(borderSize);
    }

    public void setBorderColor(@ColorInt int borderColor) {
        this.borderHolder.setBorderColor(borderColor);
    }

    public void setBorderRadius(float radius) {
        this.borderHolder.setRadius(radius);
    }

    public Drawable getPlaceHolder() {
        return placeHolder;
    }

    public Drawable getErrorImage() {
        return errorImage;
    }

    public void setPlaceHolder(Drawable placeHolder) {
        this.placeHolder = placeHolder;
    }

    public void setErrorImage(Drawable errorImage) {
        this.errorImage = errorImage;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageHolder)) return false;

        ImageHolder that = (ImageHolder) o;

        if (position != that.position) return false;
        if (width != that.width) return false;
        if (height != that.height) return false;
        if (scaleType != that.scaleType) return false;
        if (imageState != that.imageState) return false;
        if (autoFix != that.autoFix) return false;
        if (autoPlay != that.autoPlay) return false;
        if (show != that.show) return false;
        if (isGif != that.isGif) return false;
        if (!prefixCode.equals(that.prefixCode)) return false;
        if (!source.equals(that.source)) return false;
        if (!key.equals(that.key)) return false;
        if (!borderHolder.equals(that.borderHolder)) return false;
        if (placeHolder != null ? !placeHolder.equals(that.placeHolder) : that.placeHolder != null)
            return false;
        return errorImage != null ? errorImage.equals(that.errorImage) : that.errorImage == null;
    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + key.hashCode();
        result = 31 * result + position;
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + scaleType.hashCode();
        result = 31 * result + imageState;
        result = 31 * result + (autoFix ? 1 : 0);
        result = 31 * result + (autoPlay ? 1 : 0);
        result = 31 * result + (show ? 1 : 0);
        result = 31 * result + (isGif ? 1 : 0);
        result = 31 * result + (borderHolder != null ? borderHolder.hashCode() : 0);
        result = 31 * result + (placeHolder != null ? placeHolder.hashCode() : 0);
        result = 31 * result + (errorImage != null ? errorImage.hashCode() : 0);
        result = 31 * result + prefixCode.hashCode();
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "ImageHolder{" +
                "source='" + source + '\'' +
                ", key='" + key + '\'' +
                ", position=" + position +
                ", width=" + width +
                ", height=" + height +
                ", scaleType=" + scaleType +
                ", imageState=" + imageState +
                ", autoFix=" + autoFix +
                ", autoPlay=" + autoPlay +
                ", show=" + show +
                ", isGif=" + isGif +
                ", borderHolder=" + borderHolder +
                ", placeHolder=" + placeHolder +
                ", errorImage=" + errorImage +
                ", prefixCode=" + prefixCode +
                '}';
    }
}
