package com.zzhoujay.richtext.drawable;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Created by zhou on 16-5-28.
 */
public class URLDrawable extends BitmapDrawable {
    private Drawable drawable;
    private boolean recycle;

    @SuppressWarnings("deprecation")
    public URLDrawable() {
    }

    @Override
    public void draw(Canvas canvas) {
        if (drawable != null && !recycle) {
            drawable.draw(canvas);
        }
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public boolean isRecycle() {
        return recycle;
    }

    public void recycle() {
        this.recycle = true;
    }
}
