package com.zzhoujay.richtext;

import android.graphics.Color;
import android.support.annotation.ColorInt;

/**
 * Created by zhou on 2016/11/17.
 * LinkHolder
 */
@SuppressWarnings("unused")
public class LinkHolder {

    private static final int textColor = Color.parseColor("#4078C0");
    private static final int bgColor = Color.parseColor("#00ffffff");

    private final String url;
    private boolean underLine;
    @ColorInt
    private int mNormalBackgroundColor;
    @ColorInt
    private int mPressedBackgroundColor;
    @ColorInt
    private int mNormalTextColor;
    @ColorInt
    private int mPressedTextColor;

    public LinkHolder(String url) {
        this.url = url;
        this.mNormalTextColor = textColor;
        this.mPressedTextColor = textColor;
        this.mNormalBackgroundColor = bgColor;
        this.mPressedBackgroundColor = bgColor;
        underLine = true;
    }

    public boolean isUnderLine() {
        return underLine;
    }

    public void setUnderLine(boolean underLine) {
        this.underLine = underLine;
    }

    public String getUrl() {
        return url;
    }

    @ColorInt
    public int getNormalBackgroundColor() {
        return mNormalBackgroundColor;
    }

    public void setNormalBackgroundColor(@ColorInt int mNormalBackgroundColor) {
        this.mNormalBackgroundColor = mNormalBackgroundColor;
    }

    @ColorInt
    public int getPressedBackgroundColor() {
        return mPressedBackgroundColor;
    }

    public void setPressedBackgroundColor(@ColorInt int mPressedBackgroundColor) {
        this.mPressedBackgroundColor = mPressedBackgroundColor;
    }

    @ColorInt
    public int getNormalTextColor() {
        return mNormalTextColor;
    }


    public void setNormalTextColor(@ColorInt int mNormalTextColor) {
        this.mNormalTextColor = mNormalTextColor;
    }

    @ColorInt
    public int getPressedTextColor() {
        return mPressedTextColor;
    }

    public void setPressedTextColor(@ColorInt int mPressedTextColor) {
        this.mPressedTextColor = mPressedTextColor;
    }
}
