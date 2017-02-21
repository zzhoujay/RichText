package com.zzhoujay.richtext.ig;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichTextConfig;
import com.zzhoujay.richtext.callback.ImageLoadNotify;
import com.zzhoujay.richtext.drawable.DrawableWrapper;

/**
 * Created by zhou on 2016/12/9.
 * Base64格式图片解析器
 */
class Base64ImageLoader extends AbstractImageLoader<byte[]> implements Runnable {

    @NonNull
    private byte[] src;

    Base64ImageLoader(@NonNull byte[] src, ImageHolder holder, RichTextConfig config, TextView textView, DrawableWrapper drawableWrapper, ImageLoadNotify iln) {
        super(holder, config, textView, drawableWrapper, iln, SourceDecode.BASE64_SOURCE_DECODE);
        this.src = src;
    }

    @Override
    public void run() {
        try {
            onLoading();
            BitmapFactory.Options options = new BitmapFactory.Options();
            int[] inDimens = getDimensions(src, options);
            options.inSampleSize = onSizeReady(inDimens[0], inDimens[1]);
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            onResourceReady(sourceDecode.decode(holder, src, options));
        } catch (Exception e) {
            onFailure(e);
        }
    }

}