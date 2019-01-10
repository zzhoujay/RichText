package com.zzhoujay.richtext.ig;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.zzhoujay.richtext.callback.Recyclable;
import com.zzhoujay.richtext.drawable.GifDrawable;
import com.zzhoujay.richtext.exceptions.ImageWrapperMultiSourceException;

/**
 * Created by zhou on 2017/2/21.
 * 抽象的图片类，包含Bitmap静态图的Gif动态图
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
                throw new ImageWrapperMultiSourceException();
            } else {
                height = bitmap.getHeight();
                width = bitmap.getWidth();
            }
        } else {
            if (bitmap != null) {
                throw new ImageWrapperMultiSourceException();
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
            BitmapDrawable bitmapDrawable = new BitmapDrawable(resources, bitmap);
            bitmapDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            return bitmapDrawable;
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
