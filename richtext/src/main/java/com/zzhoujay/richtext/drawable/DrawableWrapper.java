package com.zzhoujay.richtext.drawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.zzhoujay.richtext.ImageHolder;

/**
 * Created by zhou on 16-5-28.
 * DrawableWrapper
 */
public class DrawableWrapper extends Drawable {
    private Drawable drawable;

    private float scaleX = 1;
    private float scaleY = 1;
    private float translateX = 0;
    private float translateY = 0;
    private Paint paint;
    private boolean hasCache;
    private DrawableSizeHolder sizeHolder;

    public DrawableWrapper(ImageHolder holder) {
        paint = new Paint();
        paint.setAntiAlias(true);
        sizeHolder = new DrawableSizeHolder(holder);
        this.hasCache = false;

        setUpBorderHolder(sizeHolder.borderHolder);
        setBounds(sizeHolder.border);
    }

    public DrawableWrapper(DrawableSizeHolder sizeHolder) {
        paint = new Paint();
        paint.setAntiAlias(true);
        this.sizeHolder = sizeHolder;
        this.hasCache = true;

        setUpBorderHolder(sizeHolder.borderHolder);
        setBounds(sizeHolder.border);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        if (drawable != null) {
            canvas.clipRect(getBounds());
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                if (bitmap == null || bitmap.isRecycled()) {
                    return;
                }
            }
            drawImage(canvas);
        }
        drawBorder(canvas);
        canvas.restore();
    }

    private void drawBorder(Canvas canvas) {
        if (sizeHolder != null && sizeHolder.borderHolder != null && sizeHolder.borderHolder.isShowBorder() && sizeHolder.border != null) {
            float radius = sizeHolder.borderHolder.getRadius();
            canvas.drawRoundRect(sizeHolder.border, radius, radius, paint);
        }
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
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


    @SuppressWarnings("unused")
    public void setSizeHolder(DrawableSizeHolder sizeHolder) {
        if (!hasCache && sizeHolder != null) {
            this.sizeHolder.set(sizeHolder);
            setUpBorderHolder(sizeHolder.borderHolder);
        }
    }

    public DrawableSizeHolder getSizeHolder() {
        return this.sizeHolder;
    }

    private void setUpBorderHolder(DrawableBorderHolder borderHolder) {
        if (borderHolder != null) {
            paint.setColor(borderHolder.getBorderColor());
            paint.setStrokeWidth(borderHolder.getBorderSize());
            paint.setStyle(Paint.Style.STROKE);
        }
    }

    public void calculate() {
        int imageWidth, imageHeight;
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap == null || bitmap.isRecycled()) {
                return;
            }
            imageWidth = bitmap.getWidth();
            imageHeight = bitmap.getHeight();
        } else if (drawable instanceof GifDrawable) {
            GifDrawable gifDrawable = (GifDrawable) this.drawable;
            imageWidth = gifDrawable.getWidth();
            imageHeight = gifDrawable.getHeight();
        } else {
            Rect bounds = drawable.getBounds();
            imageWidth = bounds.width();
            imageHeight = bounds.height();
        }

        Rect bounds = getBounds();
        int width = bounds.width();
        int height = bounds.height();
        if (width <= 0 || height <= 0) {
            return;
        }

        ImageHolder.ScaleType scaleType = sizeHolder == null ? ImageHolder.ScaleType.none : sizeHolder.scaleType;

        switch (scaleType) {
            case none:
                none(imageWidth, imageHeight, width, height);
                break;
            case center:
                center(imageWidth, imageHeight, width, height);
                break;
            case center_crop:
                centerCrop(imageWidth, imageHeight, width, height);
                break;
            case center_inside:
                centerInside(imageWidth, imageHeight, width, height);
                break;
            case fit_center:
                fitCenter(imageWidth, imageHeight, width, height, 0);
                break;
            case fit_start:
                fitCenter(imageWidth, imageHeight, width, height, -1);
                break;
            case fit_end:
                fitCenter(imageWidth, imageHeight, width, height, 1);
                break;
            case fit_xy:
                fitXY(imageWidth, imageHeight, width, height);
                break;
            case fit_auto:
                fitAuto(imageWidth, imageHeight, width, height);
                break;
        }
    }


    private void drawImage(Canvas canvas) {
        canvas.save();
        canvas.scale(scaleX, scaleY);
        canvas.translate(translateX, translateY);
        drawable.draw(canvas);
        canvas.restore();
    }

    @SuppressWarnings("UnusedParameters")
    private void none(int imageWidth, int imageHeight, int width, int height) {
        translateX = 0;
        translateY = 0;
        scaleX = 1;
        scaleY = 1;
    }


    private void center(int imageWidth, int imageHeight, int width, int height) {
        translateX = (width - imageWidth) / 2f;
        translateY = (height - imageHeight) / 2f;
        scaleX = 1;
        scaleY = 1;
    }

    @SuppressWarnings("UnusedParameters")
    private void fitAuto(int imageWidth, int imageHeight, int width, int height) {
        float sw = (float) width / imageWidth;
        int h = (int) (sw * imageHeight);
        scaleX = sw;
        scaleY = sw;
        translateX = 0;
        translateY = 0;

        setBounds(0, 0, width, h);
        if (hasCache && sizeHolder != null) {
            sizeHolder.border.set(0, 0, width, h);
        }
    }

    private void fitXY(int imageWidth, int imageHeight, int width, int height) {
        scaleX = (float) width / imageWidth;
        scaleY = (float) height / imageHeight;
        translateX = 0;
        translateY = 0;
    }

    private void fitCenter(int imageWidth, int imageHeight, int width, int height, int flag) {
        float sw = (float) width / imageWidth;
        float sh = (float) height / imageHeight;
        float scale = Math.min(sw, sh);
        boolean xy = sw > sh;

        float scaleWidth = imageWidth * scale;
        float scaleHeight = imageHeight * scale;
        float tx = (width - scaleWidth) / 2;
        float ty = (height - scaleHeight) / 2;
        if (flag < 0) {
            if (xy) {
                tx = 0;
            } else {
                ty = 0;
            }
        } else if (flag > 0) {
            if (xy) {
                tx = width - scaleWidth;
            } else {
                ty = height - scaleHeight;
            }
        }

        scaleX = scale;
        scaleY = scale;
        translateX = tx / scale;
        translateY = ty / scale;
    }


    private void centerInside(int imageWidth, int imageHeight, int width, int height) {
        float sw = (float) width / imageWidth;
        float sh = (float) height / imageHeight;
        float scale = 1;
        if (sw < sh) {
            if (sw < 1) {
                scale = sw;
            }
        } else {
            if (sh < 1) {
                scale = sh;
            }
        }
        float scaleWidth = imageWidth * scale;
        float scaleHeight = imageHeight * scale;
        translateX = (width - scaleWidth) / (2 * scale);
        translateY = (height - scaleHeight) / (2 * scale);

        scaleX = scale;
        scaleY = scale;
    }

    private void centerCrop(int imageWidth, int imageHeight, int width, int height) {
        float sw = (float) width / imageWidth;
        float sh = (float) height / imageHeight;
        float scale = 1;

        if (sw > sh) {
            if (sw > 1) {
                scale = sw;
            }
        } else {
            if (sh > 1) {
                scale = sh;
            }
        }

        float scaleWidth = imageWidth * scale;
        float scaleHeight = imageHeight * scale;
        translateX = (width - scaleWidth) / (2 * scale);
        translateY = (height - scaleHeight) / (2 * scale);

        scaleX = scale;
        scaleY = scale;
    }

    private void setBounds(RectF bounds) {
        setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    private void setBounds(float left, float top, float right, float bottom) {
        setBounds((int) left, (int) top, (int) right, (int) bottom);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        if (!hasCache && sizeHolder != null) {
            sizeHolder.border.set(left, top, right, bottom);
        }
    }

    @Override
    public void setBounds(@NonNull Rect bounds) {
        super.setBounds(bounds);
        if (!hasCache && sizeHolder != null) {
            sizeHolder.border.set(bounds);
        }
    }

    public void setScaleType(ImageHolder.ScaleType scaleType) {
        if (!hasCache && sizeHolder != null) {
            sizeHolder.scaleType = scaleType;
        }
    }

    public void setBorderHolder(DrawableBorderHolder borderHolder) {
        if (!hasCache && sizeHolder != null) {
            sizeHolder.borderHolder.set(borderHolder);
        }
    }

    public boolean isHasCache() {
        return hasCache;
    }
}
