package com.zzhoujay.richtext.ig;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.TextView;

import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichTextConfig;
import com.zzhoujay.richtext.callback.ImageLoadNotify;
import com.zzhoujay.richtext.drawable.DrawableWrapper;

/**
 * Created by zhou on 2017/2/20.
 */

class LocalFileImageLoader extends AbstractImageLoader<String> implements Runnable {
    LocalFileImageLoader(ImageHolder holder, RichTextConfig config, TextView textView, DrawableWrapper drawableWrapper, ImageLoadNotify iln) {
        super(holder, config, textView, drawableWrapper, iln, SourceDecode.LOCAL_FILE_SOURCE_DECODE);
    }

    @Override
    public void run() {
        try {
            onLoading();
            BitmapFactory.Options options = new BitmapFactory.Options();
            int[] inDimens = getDimensions(holder.getSource(), options);
            options.inSampleSize = onSizeReady(inDimens[0], inDimens[1]);
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            onResourceReady(sourceDecode.decode(holder.getSource(), options));
        } catch (Exception e) {
            onFailure(e);
        }
    }
}
