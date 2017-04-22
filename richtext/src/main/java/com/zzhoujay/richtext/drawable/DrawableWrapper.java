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
    @ImageHolder.ScaleType
    private int scaleType = ImageHolder.ScaleType.NONE;

    private float scaleX = 1;
    private float scaleY = 1;
    private float translateX = 0;
    private float translateY = 0;
    private ImageHolder.BorderHolder borderHolder;
    private Paint paint;
    private RectF border;

    public DrawableWrapper() {
        paint = new Paint();
        paint.setAntiAlias(true);
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
        if (borderHolder != null && borderHolder.isShowBorder()) {
            float radius = borderHolder.getRadius();
            canvas.drawRoundRect(border, radius, radius, paint);
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


    public void setScaleType(int scaleType) {
        this.scaleType = scaleType;
    }

    @ImageHolder.ScaleType
    public int getScaleType() {
        return scaleType;
    }

    public ImageHolder.BorderHolder getBorderHolder() {
        return borderHolder;
    }

    public void setBorderHolder(ImageHolder.BorderHolder borderHolder) {
        this.borderHolder = borderHolder;
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

        switch (scaleType) {
            case ImageHolder.ScaleType.NONE:
                none(imageWidth, imageHeight, width, height);
                break;
            case ImageHolder.ScaleType.CENTER:
                center(imageWidth, imageHeight, width, height);
                break;
            case ImageHolder.ScaleType.CENTER_CROP:
                centerCrop(imageWidth, imageHeight, width, height);
                break;
            case ImageHolder.ScaleType.CENTER_INSIDE:
                centerInside(imageWidth, imageHeight, width, height);
                break;
            case ImageHolder.ScaleType.FIT_CENTER:
                fitCenter(imageWidth, imageHeight, width, height, 0);
                break;
            case ImageHolder.ScaleType.FIT_START:
                fitCenter(imageWidth, imageHeight, width, height, -1);
                break;
            case ImageHolder.ScaleType.FIT_END:
                fitCenter(imageWidth, imageHeight, width, height, 1);
                break;
            case ImageHolder.ScaleType.FIT_XY:
                fitXY(imageWidth, imageHeight, width, height);
                break;
            case ImageHolder.ScaleType.FIT_AUTO:
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

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        border = new RectF(getBounds());
    }

    @Override
    public void setBounds(@NonNull Rect bounds) {
        super.setBounds(bounds);
        border = new RectF(bounds);
    }
}
