package com.zzhoujay.richtext.ig;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.zzhoujay.richtext.callback.Recyclable;
import com.zzhoujay.richtext.drawable.GifDrawable;

/**
 * Created by zhou on 2017/2/21.
 */

class ImageWrapper implements Recyclable {

    private final GifDrawable gifDrawable;
    private final Bitmap bitmap;
    private final int height;
    private final int width;

    private ImageWrapper(GifDrawable gifDrawable, Bitmap bitmap) {
        this.gifDrawable = gifDrawable;
        this.bitmap = bitmap;
        if (gifDrawable == null) {
            if (bitmap == null) {
                throw new IllegalArgumentException("gifDrawable和bitmap有且只有一个为null");
            } else {
                height = bitmap.getHeight();
                width = bitmap.getWidth();
            }
        } else {
            if (bitmap != null) {
                throw new IllegalArgumentException("gifDrawable和bitmap有且只有一个为null");
            } else {
                height = gifDrawable.getHeight();
                width = gifDrawable.getWidth();
            }
        }
    }

    boolean isGif() {
        return gifDrawable != null;
    }

    GifDrawable getAsGif() {
        return gifDrawable;
    }

    Bitmap getAsBitmap() {
        return bitmap;
    }


    int getHeight() {
        return height;
    }

    int getWidth() {
        return width;
    }

    Drawable getDrawable(Resources resources) {
        if (gifDrawable == null) {
            return new BitmapDrawable(resources, bitmap);
        } else {
            return gifDrawable;
        }
    }

    @Override
    public void recycle() {
        if (gifDrawable != null) {
            gifDrawable.stop();
        }
    }

    static ImageWrapper createAsGif(GifDrawable gifDrawable) {
        return new ImageWrapper(gifDrawable, null);
    }

    static ImageWrapper createAsBitmap(Bitmap bitmap) {
        return new ImageWrapper(null, bitmap);
    }

}
