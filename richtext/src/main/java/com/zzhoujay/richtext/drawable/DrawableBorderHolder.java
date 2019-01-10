package com.zzhoujay.richtext.drawable;

import android.graphics.Color;
import android.support.annotation.ColorInt;

/**
 * Created by zhou on 2017/10/4.
 * Drawable border
 */
public class DrawableBorderHolder {


    private boolean showBorder;
    private float borderSize;
    @ColorInt
    private int borderColor;
    private float radius;

    DrawableBorderHolder(boolean showBorder, float borderSize, @ColorInt int borderColor, float radius) {
        this.showBorder = showBorder;
        this.borderSize = borderSize;
        this.borderColor = borderColor;
        this.radius = radius;
    }

    public DrawableBorderHolder() {
        this(false, 5, Color.BLACK, 0);
    }

    public DrawableBorderHolder(DrawableBorderHolder borderHolder) {
        this(borderHolder.showBorder, borderHolder.borderSize, borderHolder.borderColor, borderHolder.radius);
    }

    boolean isShowBorder() {
        return showBorder;
    }

    public void setShowBorder(boolean showBorder) {
        this.showBorder = showBorder;
    }

    float getBorderSize() {
        return borderSize;
    }

    public void setBorderSize(float borderSize) {
        this.borderSize = borderSize;
    }

    @ColorInt
    int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(@ColorInt int borderColor) {
        this.borderColor = borderColor;
    }

    float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }


    void set(DrawableBorderHolder borderHolder) {
        this.showBorder = borderHolder.showBorder;
        this.borderSize = borderHolder.borderSize;
        this.borderColor = borderHolder.borderColor;
        this.radius = borderHolder.radius;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DrawableBorderHolder)) return false;

        DrawableBorderHolder that = (DrawableBorderHolder) o;

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
