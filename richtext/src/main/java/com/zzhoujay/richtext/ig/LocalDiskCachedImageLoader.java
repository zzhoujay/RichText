package com.zzhoujay.richtext.ig;

import android.widget.TextView;

import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichTextConfig;
import com.zzhoujay.richtext.callback.ImageLoadNotify;
import com.zzhoujay.richtext.drawable.DrawableWrapper;

/**
 * Created by zhou on 2017/3/25.
 */
class LocalDiskCachedImageLoader extends AbstractImageLoader implements Runnable {

    LocalDiskCachedImageLoader(ImageHolder holder, RichTextConfig config, TextView textView, DrawableWrapper drawableWrapper, ImageLoadNotify iln) {
        //noinspection unchecked
        super(holder, config, textView, drawableWrapper, iln, null, null);
    }


    @Override
    public void run() {
        int exist = BitmapWrapper.exist(BitmapPool.getCacheDir(), holder.getKey());
        if (exist < 1) {
            onFailure(new RuntimeException("bitmap 未缓存"));
        } else {
            BitmapWrapper bitmapWrapper = BitmapWrapper.read(BitmapPool.getCacheDir(), holder.getKey(), true);
            if (bitmapWrapper == null) {
                onFailure(new RuntimeException("bitmap 加载失败"));
            } else {
                border = bitmapWrapper.getRect();
                onResourceReady(ImageWrapper.createAsBitmap(bitmapWrapper.getBitmap()));
            }
        }
    }
}
