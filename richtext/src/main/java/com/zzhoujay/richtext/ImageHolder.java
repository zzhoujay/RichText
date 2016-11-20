package com.zzhoujay.richtext;

import android.support.annotation.IntDef;

/**
 * Created by zhou on 16-5-28.
 * ImageHolder
 */
public class ImageHolder {

    /**
     * ScaleType
     */
    @IntDef({ScaleType.DEFAULT, ScaleType.CENTER_CROP, ScaleType.FIT_CENTER})
    public @interface ScaleType {
        int DEFAULT = 0;
        int CENTER_CROP = 1;
        int FIT_CENTER = 2;
    }

    /**
     * ImageType
     */
    @IntDef({ImageType.JPG, ImageType.GIF})
    public @interface ImageType {
        int JPG = 0;
        int GIF = 1;
    }

    /**
     * ImageState 图片的加载状态
     * INIT: 初始化加载，可以设置图片宽高给Glide
     * LOADING: 加载中，设置placeholder图片的宽高
     * READY: 图片加载成功，设置最终显示的图片的宽高
     * FAILED: 加载失败，设置加载失败的图片的宽高
     */
    @IntDef({ImageState.INIT, ImageState.LOADING, ImageState.READY, ImageState.FAILED, ImageState.SIZE_READY})
    public @interface ImageState {
        int INIT = 0;
        int LOADING = 1;
        int READY = 2;
        int FAILED = 3;
        int SIZE_READY = 4;
    }

    private final String src;
    private final int position;
    private int width = -1, height = -1;
    private int maxWidth, maxHeight;
    private float scale = 1;
    @ScaleType
    private int scaleType = ScaleType.DEFAULT;
    @ImageType
    private int imageType = ImageType.JPG;
    @ImageState
    private int imageState;
    private boolean autoFix;
    private boolean autoPlay;
    private boolean autoStop;
    private boolean show;

    public ImageHolder(String src, int position) {
        this.src = src;
        this.position = position;
        autoPlay = false;
        autoStop = true;
        show = true;
        maxWidth = -1;
        maxHeight = -1;
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

    public String getSrc() {
        return src;
    }

    public boolean isAutoFix() {
        return autoFix;
    }

    public void setAutoFix(boolean autoFix) {
        this.autoFix = autoFix;
    }

    @ImageType
    public int getImageType() {
        return imageType;
    }

    public void setImageType(@ImageType int imageType) {
        this.imageType = imageType;
    }

    @ScaleType
    public int getScaleType() {
        return scaleType;
    }

    public void setScaleType(@ScaleType int scaleType) {
        this.scaleType = scaleType;
    }

    public boolean isGif() {
        return imageType == ImageType.GIF;
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public boolean isAutoStop() {
        return autoStop;
    }

    public void setAutoStop(boolean autoStop) {
        this.autoStop = autoStop;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @ImageState
    public int getImageState() {
        return imageState;
    }

    public void setImageState(@ImageState int imageState) {
        this.imageState = imageState;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    @Override
    public String toString() {
        return "ImageHolder{" +
                "src='" + src + '\'' +
                ", position=" + position +
                ", width=" + width +
                ", height=" + height +
                ", maxWidth=" + maxWidth +
                ", maxHeight=" + maxHeight +
                ", scale=" + scale +
                ", scaleType=" + scaleType +
                ", imageType=" + imageType +
                ", imageState=" + imageState +
                ", autoFix=" + autoFix +
                ", autoPlay=" + autoPlay +
                ", autoStop=" + autoStop +
                ", show=" + show +
                '}';
    }
}
