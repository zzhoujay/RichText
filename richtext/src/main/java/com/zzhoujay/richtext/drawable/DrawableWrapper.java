package com.zzhoujay.richtext.drawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by zhou on 16-5-28.
 * DrawableWrapper
 */
public class DrawableWrapper extends BitmapDrawable {
    private Drawable drawable;
    private boolean recycle;

    @SuppressWarnings("deprecation")
    public DrawableWrapper() {
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
        if (drawable != null) {
            drawable.setBounds(getBounds());
        }
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        if (drawable != null) {
            drawable.setBounds(left, top, right, bottom);
        }
    }

    @Override
    public void setBounds(Rect bounds) {
        super.setBounds(bounds);
        if (drawable != null) {
            drawable.setBounds(bounds);
        }
    }

    public boolean isRecycle() {
        return recycle;
    }

    public void recycle() {
        this.recycle = true;
    }
}
