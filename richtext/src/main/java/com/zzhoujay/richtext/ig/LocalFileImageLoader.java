package com.zzhoujay.richtext.ig;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.TextView;

import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichTextConfig;
import com.zzhoujay.richtext.callback.ImageLoadNotify;
import com.zzhoujay.richtext.drawable.DrawableWrapper;
import com.zzhoujay.richtext.exceptions.ImageDecodeException;

/**
 * Created by zhou on 2017/2/20.
 * 本地图片加载器
 */

class LocalFileImageLoader extends AbstractImageLoader<String> implements Runnable {

    LocalFileImageLoader(ImageHolder holder, RichTextConfig config, TextView textView, DrawableWrapper drawableWrapper, ImageLoadNotify iln, BitmapWrapper.SizeCacheHolder sizeCacheHolder) {
        super(holder, config, textView, drawableWrapper, iln, SourceDecode.LOCAL_FILE_SOURCE_DECODE, sizeCacheHolder);
    }

    @Override
    public void run() {
        try {
            onLoading();
            BitmapFactory.Options options = new BitmapFactory.Options();
            int[] inDimens = getDimensions(holder.getSource(), options);
            BitmapWrapper.SizeCacheHolder sizeCacheHolder = super.sizeCacheHolder;
            if (sizeCacheHolder == null) {
                sizeCacheHolder = loadSizeCacheHolder();
            }
            if (sizeCacheHolder == null) {
                options.inSampleSize = onSizeReady(inDimens[0], inDimens[1]);
            } else {
                options.inSampleSize = getSampleSize(inDimens[0], inDimens[1], sizeCacheHolder.rect.width(), sizeCacheHolder.rect.height());
            }
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            onResourceReady(sourceDecode.decode(holder, holder.getSource(), options));
        } catch (Exception e) {
            onFailure(new ImageDecodeException(e));
        }
    }
}
