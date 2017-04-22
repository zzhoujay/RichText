package com.zzhoujay.richtext.ig;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.TextView;

import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichTextConfig;
import com.zzhoujay.richtext.callback.ImageLoadNotify;
import com.zzhoujay.richtext.drawable.DrawableWrapper;
import com.zzhoujay.richtext.exceptions.ImageDecodeException;
import com.zzhoujay.richtext.ext.Base64;

/**
 * Created by zhou on 2016/12/9.
 * Base64格式图片解析器
 */
class Base64ImageLoader extends AbstractImageLoader<byte[]> implements Runnable {

    Base64ImageLoader(ImageHolder holder, RichTextConfig config, TextView textView, DrawableWrapper drawableWrapper, ImageLoadNotify iln, BitmapWrapper.SizeCacheHolder border) {
        super(holder, config, textView, drawableWrapper, iln, SourceDecode.BASE64_SOURCE_DECODE, border);
    }

    @Override
    public void run() {
        try {
            onLoading();
            BitmapFactory.Options options = new BitmapFactory.Options();
            byte[] src = Base64.decode(holder.getSource());
            int[] inDimens = getDimensions(src, options);
            BitmapWrapper.SizeCacheHolder border = super.sizeCacheHolder;
            if (border == null) {
                border = loadSizeCacheHolder();
            }
            if (border == null) {
                options.inSampleSize = onSizeReady(inDimens[0], inDimens[1]);
            } else {
                options.inSampleSize = getSampleSize(inDimens[0], inDimens[1], border.rect.width(), border.rect.height());
            }
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            onResourceReady(sourceDecode.decode(holder, src, options));
        } catch (Exception e) {
            onFailure(new ImageDecodeException(e));
        }
    }

}