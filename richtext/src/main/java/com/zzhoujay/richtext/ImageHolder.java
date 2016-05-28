package com.zzhoujay.richtext;

import android.support.annotation.IntDef;

/**
 * Created by zhou on 16-5-28.
 */
public class ImageHolder {
    public static final int DEFAULT = 0;
    public static final int CENTER_CROP = 1;
    public static final int FIT_CENTER = 2;

    public static final int JPG = 0;
    public static final int GIF = 1;

    @IntDef({DEFAULT, CENTER_CROP, FIT_CENTER})
    protected @interface ScaleType {
    }

    @IntDef({JPG, GIF})
    protected @interface ImageType {

    }

    private final String src;
    private final int position;
    private int width = -1, height = -1;
    private int scaleType = DEFAULT;
    private int imageType;
    private boolean autoFix;

    public ImageHolder(String src, int position) {
        this.src = src;
        this.position = position;
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

}
