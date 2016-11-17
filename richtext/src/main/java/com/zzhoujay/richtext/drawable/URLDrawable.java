package com.zzhoujay.richtext.drawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by zhou on 16-5-28.
 * URLDrawable
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
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                if (bitmap == null || bitmap.isRecycled()) {
                    return;
                }
            }
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
