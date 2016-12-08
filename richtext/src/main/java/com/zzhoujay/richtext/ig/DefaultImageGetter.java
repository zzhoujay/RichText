package com.zzhoujay.richtext.ig;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichTextConfig;
import com.zzhoujay.richtext.callback.ImageGetter;
import com.zzhoujay.richtext.target.ImageLoadNotify;

/**
 * Created by zhou on 2016/12/8.
 */

public class DefaultImageGetter implements ImageGetter {
    @Override
    public Drawable getDrawable(ImageHolder holder, RichTextConfig config, TextView textView) {
        return null;
    }

    @Override
    public void registerImageLoadNotify(ImageLoadNotify imageLoadNotify) {

    }

    @Override
    public void recycle() {

    }
}
