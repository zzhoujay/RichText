package com.zzhoujay.richtext.ig;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.widget.TextView;

import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichTextConfig;
import com.zzhoujay.richtext.callback.ImageLoadNotify;
import com.zzhoujay.richtext.drawable.DrawableWrapper;
import com.zzhoujay.richtext.ext.Base64;

/**
 * Created by zhou on 2016/12/9.
 * Base64格式图片解析器
 */
class Base64ImageLoader extends AbstractImageLoader<byte[]> implements Runnable {

    Base64ImageLoader(ImageHolder holder, RichTextConfig config, TextView textView, DrawableWrapper drawableWrapper, ImageLoadNotify iln, Rect border) {
        super(holder, config, textView, drawableWrapper, iln, SourceDecode.BASE64_SOURCE_DECODE, border);
    }

    @Override
    public void run() {
        try {
            onLoading();
            BitmapFactory.Options options = new BitmapFactory.Options();
            byte[] src = Base64.decode(holder.getSource());
            int[] inDimens = getDimensions(src, options);
            Rect border = super.border;
            if (border == null) {
                border = loadCachedBorder();
            }
            if (border == null) {
                options.inSampleSize = onSizeReady(inDimens[0], inDimens[1]);
            } else {
                options.inSampleSize = getSampleSize(inDimens[0], inDimens[1], border.width(), border.height());
            }
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            onResourceReady(sourceDecode.decode(holder, src, options));
        } catch (Exception e) {
            onFailure(e);
        }
    }

}