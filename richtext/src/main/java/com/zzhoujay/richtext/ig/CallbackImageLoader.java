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
 * Created by zhou on 2016/12/8.
 * 默认图片加载器
 */
class CallbackImageLoader extends AbstractImageLoader<InputStream> implements ImageDownloadCallback {

    CallbackImageLoader(ImageHolder holder, RichTextConfig config, TextView textView, DrawableWrapper drawableWrapper, ImageLoadNotify iln, BitmapWrapper.SizeCacheHolder sizeCacheHolder) {
        super(holder, config, textView, drawableWrapper, iln, SourceDecode.REMOTE_SOURCE_DECODE, sizeCacheHolder);
        onLoading();
    }

    @Override
    public void success(InputStream inputStream) {

        try {
            BufferedInputStream stream = new BufferedInputStream(inputStream);

            doLoadImage(stream);

            stream.close();
        } catch (IOException e) {
            onFailure(new ImageDecodeException(e));
        }
    }

    @Override
    public void failure(Exception e) {
        onFailure(e);
    }

//    @Override
//    public void onResponse(Call call, Response response) throws IOException {
//        try {
//            InputStream inputStream = response.body().byteStream();
//            BufferedInputStream stream = new BufferedInputStream(inputStream);
//
//            doLoadImage(stream);
//
//            stream.close();
//            inputStream.close();
//        } catch (Exception e) {
//            onFailure(new ImageDecodeException(e));
//        }
//    }
//
//    @Override
//    public void onFailure(Call call, IOException e) {
//        onFailure(e);
//    }

}

