package com.zzhoujay.richtext.drawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by zhou on 16-5-28.
 * DrawableWrapper
 */
public class DrawableWrapper extends Drawable implements Drawable.Callback {
    private Drawable drawable;

    public DrawableWrapper() {
    }

    @Override
    public void draw(Canvas canvas) {
        if (drawable != null) {
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
        if (this.drawable != null) {
            this.drawable.setCallback(null);
        }
        this.drawable = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
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

    @Override
    public void setAlpha(int alpha) {
        if (drawable != null) {
            drawable.setAlpha(alpha);
        }
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        if (drawable != null) {
            drawable.setColorFilter(colorFilter);
        }
    }

    @Override
    public int getOpacity() {
        return drawable == null ? PixelFormat.TRANSPARENT : drawable.getOpacity();
    }

    @Override
    public void invalidateDrawable(Drawable who) {
        Callback callback = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            callback = getCallback();
        }
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        Callback callback = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            callback = getCallback();
        }
        if (callback != null) {
            callback.scheduleDrawable(who, what, when);
        }
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        Callback callback = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            callback = getCallback();
        }
        if (callback != null) {
            callback.unscheduleDrawable(who, what);
        }
    }
}
