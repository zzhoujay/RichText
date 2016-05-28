package com.zzhoujay.richtext;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by zhou on 16-5-28.
 */
class URLDrawable extends BitmapDrawable {
    private Drawable drawable;

    @SuppressWarnings("deprecation")
    public URLDrawable() {
    }

    @Override
    public void draw(Canvas canvas) {
        if (drawable != null)
            drawable.draw(canvas);
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}
