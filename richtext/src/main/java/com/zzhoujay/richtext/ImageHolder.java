package com.zzhoujay.richtext;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;

import com.zzhoujay.richtext.exceptions.ResetImageSourceException;
import com.zzhoujay.richtext.ext.MD5;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by zhou on 16-5-28.
 * ImageHolder
 */
@SuppressWarnings("ALL")
public class ImageHolder {

    public static final int WRAP_CONTENT = Integer.MIN_VALUE;
    public static final int MATCH_PARENT = Integer.MAX_VALUE;

    /**
     * ScaleType
     */
    @IntDef({ScaleType.NONE, ScaleType.CENTER, ScaleType.CENTER_CROP, ScaleType.CENTER_INSIDE, ScaleType.FIT_START,
            ScaleType.FIT_END, ScaleType.FIT_CENTER, ScaleType.FIT_XY, ScaleType.FIT_AUTO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScaleType {
        int NONE = -1;
        int CENTER = 0;
        int CENTER_CROP = 1;
        int CENTER_INSIDE = 2;
        int FIT_CENTER = 3;
        int FIT_START = 4;
        int FIT_END = 5;
        int FIT_XY = 6;
        int FIT_AUTO = 7;
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

    public static class BorderHolder {

        private boolean showBorder;
        private float borderSize;
        @ColorInt
        private int borderColor;
        private float radius;

        public BorderHolder(boolean showBorder, float borderSize, @ColorInt int borderColor, float radius) {
            this.showBorder = showBorder;
            this.borderSize = borderSize;
            this.borderColor = borderColor;
            this.radius = radius;
        }

        public BorderHolder() {
            this(false, 5, Color.BLACK, 0);
        }

        public BorderHolder(BorderHolder borderHolder) {
            this(borderHolder.showBorder, borderHolder.borderSize, borderHolder.borderColor, borderHolder.radius);
        }

        public boolean isShowBorder() {
            return showBorder;
        }

        public void setShowBorder(boolean showBorder) {
            this.showBorder = showBorder;
        }

        public float getBorderSize() {
            return borderSize;
        }

        public void setBorderSize(float borderSize) {
            this.borderSize = borderSize;
        }

        @ColorInt
        public int getBorderColor() {
            return borderColor;
        }

        public void setBorderColor(@ColorInt int borderColor) {
            this.borderColor = borderColor;
        }

        public float getRadius() {
            return radius;
        }

        public void setRadius(float radius) {
            this.radius = radius;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BorderHolder)) return false;

            BorderHolder that = (BorderHolder) o;

            if (showBorder != that.showBorder) return false;
            if (Float.compare(that.borderSize, borderSize) != 0) return false;
            if (borderColor != that.borderColor) return false;
            if (Float.compare(that.radius, radius) != 0) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = (showBorder ? 1 : 0);
            result = 31 * result + (borderSize != +0.0f ? Float.floatToIntBits(borderSize) : 0);
            result = 31 * result + borderColor;
            result = 31 * result + (radius != +0.0f ? Float.floatToIntBits(radius) : 0);
            return result;
        }
    }

    private String source; // 图片URL
    private String key;
    private final int position; // 图片在在某个富文本中的位置
    private int width, height; // 和scale属性共同决定holder宽高，开发者设置，内部获取值然后进行相应的设置
    @ScaleType
    private int scaleType;
    @ImageState
    private int imageState; // 图片加载的状态
    private boolean autoFix;
    private boolean autoPlay;
    private boolean show;
    private boolean isGif;
    private BorderHolder borderHolder;
    private int configHashCode = 0;

    public ImageHolder(String source, int position, RichTextConfig config) {
        this(source, position);
        this.autoPlay = config.autoPlay;
        if (config.autoFix) {
            width = MATCH_PARENT;
            height = WRAP_CONTENT;
            scaleType = ScaleType.FIT_AUTO;
        } else {
            scaleType = config.scaleType;
            width = config.width;
            height = config.height;
        }
        this.show = !config.noImage;
        setShowBorder(config.borderHolder.showBorder);
        setBorderColor(config.borderHolder.borderColor);
        setBorderSize(config.borderHolder.borderSize);
        setBorderRadius(config.borderHolder.radius);
        configHashCode = config.hashCode();
        generateKey();
    }

    private ImageHolder(String source, int position) {
        this.source = source;
        this.position = position;
        width = WRAP_CONTENT;
        height = WRAP_CONTENT;
        scaleType = ScaleType.NONE;
        autoPlay = false;
        show = true;
        this.isGif = false;
        this.borderHolder = new BorderHolder();
        generateKey();
    }

    private void generateKey() {
        this.key = MD5.generate(configHashCode + source);
    }

    public void setSource(String source) {
        if (imageState != ImageState.INIT) {
            throw new ResetImageSourceException();
        }
        this.source = source;
        generateKey();
    }

    public boolean success() {
        return imageState == ImageState.READY;
    }

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

    public int getPosition() {
        return position;
    }

    public String getSource() {
        return source;
    }

    public boolean isAutoFix() {
        return autoFix;
    }

    public void setAutoFix(boolean autoFix) {
        this.autoFix = autoFix;
        if (autoFix) {
            width = MATCH_PARENT;
            height = WRAP_CONTENT;
            scaleType = ScaleType.FIT_AUTO;
        }
    }

    @ScaleType
    public int getScaleType() {
        return scaleType;
    }

    public void setScaleType(@ScaleType int scaleType) {
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

    public BorderHolder getBorderHolder() {
        return borderHolder;
    }

    public void setShowBorder(boolean showBorder) {
        this.borderHolder.showBorder = showBorder;
    }

    public void setBorderSize(float borderSize) {
        this.borderHolder.borderSize = borderSize;
    }

    public void setBorderColor(@ColorInt int borderColor) {
        this.borderHolder.borderColor = borderColor;
    }

    public void setBorderRadius(float radius) {
        this.borderHolder.radius = radius;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageHolder)) return false;

        ImageHolder that = (ImageHolder) o;

        if (width != that.width) return false;
        if (height != that.height) return false;
        if (scaleType != that.scaleType) return false;
        if (autoFix != that.autoFix) return false;
        if (autoPlay != that.autoPlay) return false;
        if (show != that.show) return false;
        if (isGif != that.isGif) return false;
        if (!source.equals(that.source)) return false;
        if (!borderHolder.equals(that.borderHolder)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + scaleType;
        result = 31 * result + (autoFix ? 1 : 0);
        result = 31 * result + (autoPlay ? 1 : 0);
        result = 31 * result + (show ? 1 : 0);
        result = 31 * result + (isGif ? 1 : 0);
        result = 31 * result + borderHolder.hashCode();
        return result;
    }

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
                ", configHashCode=" + configHashCode +
                '}';
    }
}
