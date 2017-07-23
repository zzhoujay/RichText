package com.zzhoujay.richtext.ig;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.TextView;

import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichTextConfig;
import com.zzhoujay.richtext.callback.ImageLoadNotify;
import com.zzhoujay.richtext.drawable.DrawableWrapper;
import com.zzhoujay.richtext.exceptions.ImageDecodeException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhou on 2017/7/23.
 */

class AssetsImageLoader extends AbstractImageLoader<InputStream> implements Runnable {

    private static final String ASSETS_PREFIX = "file:///android_asset/";

    AssetsImageLoader(ImageHolder holder, RichTextConfig config, TextView textView, DrawableWrapper drawableWrapper, ImageLoadNotify iln, BitmapWrapper.SizeCacheHolder sizeCacheHolder) {
        super(holder, config, textView, drawableWrapper, iln, SourceDecode.REMOTE_SOURCE_DECODE, sizeCacheHolder);
    }

    @Override
    public void run() {
        onLoading();
        String fileName = getAssetFileName(holder.getSource());
        try {
            InputStream inputStream = openAssetFile(fileName);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            BitmapFactory.Options options = new BitmapFactory.Options();
            int[] inDimens = getDimensions(bufferedInputStream, options);
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
            onResourceReady(sourceDecode.decode(holder, bufferedInputStream, options));
            bufferedInputStream.close();
            inputStream.close();
        } catch (IOException e) {
            onFailure(new ImageDecodeException(e));
        }
    }

    private static String getAssetFileName(String path) {
        if (path != null && path.startsWith(ASSETS_PREFIX)) {
            return path.replace(ASSETS_PREFIX, "");
        }
        return null;
    }

}
