package com.zzhoujay.richtext.ig;

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
 * Assets目录图片的加载器
 */

class AssetsImageLoader extends AbstractImageLoader<InputStream> implements Runnable {

    private static final String ASSETS_PREFIX = "file:///android_asset/";

    AssetsImageLoader(ImageHolder holder, RichTextConfig config, TextView textView, DrawableWrapper drawableWrapper, ImageLoadNotify iln, BitmapWrapper.SizeCacheHolder sizeCacheHolder) {
        super(holder, config, textView, drawableWrapper, iln, SourceDecode.REMOTE_SOURCE_DECODE, sizeCacheHolder);
    }

    @Override
    public void run() {
        onLoading();
        try {
            String fileName = getAssetFileName(holder.getSource());
            InputStream inputStream = openAssetFile(fileName);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            doLoadImage(bufferedInputStream);
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
