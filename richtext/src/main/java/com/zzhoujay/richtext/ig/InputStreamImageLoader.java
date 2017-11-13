package com.zzhoujay.richtext.ig;

import android.widget.TextView;

import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichTextConfig;
import com.zzhoujay.richtext.callback.ImageLoadNotify;
import com.zzhoujay.richtext.drawable.DrawableWrapper;
import com.zzhoujay.richtext.exceptions.BitmapInputStreamNullPointException;
import com.zzhoujay.richtext.exceptions.ImageDecodeException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhou on 2017/10/2.
 * 处理流
 */

class InputStreamImageLoader extends AbstractImageLoader<InputStream> implements Runnable {

    private InputStream inputStream;

    InputStreamImageLoader(ImageHolder holder, RichTextConfig config, TextView textView, DrawableWrapper drawableWrapper, ImageLoadNotify iln, InputStream inputStream) {
        super(holder, config, textView, drawableWrapper, iln, SourceDecode.INPUT_STREAM_DECODE);
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        if (inputStream == null) {
            onFailure(new BitmapInputStreamNullPointException());
            return;
        }

        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            doLoadImage(bufferedInputStream);
            bufferedInputStream.close();
            inputStream.close();
        } catch (IOException e) {
            onFailure(e);
        } catch (OutOfMemoryError error) {
            onFailure(new ImageDecodeException(error));
        }

    }
}
