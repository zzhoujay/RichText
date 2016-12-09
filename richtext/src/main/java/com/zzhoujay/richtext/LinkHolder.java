package com.zzhoujay.richtext;

import android.graphics.Color;
import android.support.annotation.ColorInt;

/**
 * Created by zhou on 2016/11/17.
 * LinkHolder
 */
@SuppressWarnings("unused")
public class LinkHolder {

    private static final int link_color = Color.parseColor("#4078C0");

    private final String url;
    @ColorInt
    private int color;
    private boolean underLine;

    public LinkHolder(String url) {
        this.url = url;
        this.color = link_color;
        underLine = true;
    }

    @ColorInt
    public int getColor() {
        return color;
    }

    public void setColor(@ColorInt int color) {
        this.color = color;
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
}
