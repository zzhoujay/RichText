package com.zzhoujay.richtext.ig;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.widget.TextView;

import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichTextConfig;
import com.zzhoujay.richtext.callback.ImageLoadNotify;
import com.zzhoujay.richtext.drawable.DrawableWrapper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zhou on 2016/12/8.
 * 默认图片加载器
 */
class CallbackImageLoader extends AbstractImageLoader<InputStream> implements Callback {

    CallbackImageLoader(ImageHolder holder, RichTextConfig config, TextView textView, DrawableWrapper drawableWrapper, ImageLoadNotify iln, Rect border) {
        super(holder, config, textView, drawableWrapper, iln, SourceDecode.REMOTE_SOURCE_DECODE, border);
        onLoading();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try {
            InputStream inputStream = response.body().byteStream();
            BufferedInputStream stream = new BufferedInputStream(inputStream);
            BitmapFactory.Options options = new BitmapFactory.Options();
            int[] inDimens = getDimensions(stream, options);
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
            onResourceReady(sourceDecode.decode(holder, stream, options));
            stream.close();
            inputStream.close();
        } catch (Exception e) {
            onFailure(e);
        }
    }

    @Override
    public void onFailure(Call call, IOException e) {
        onFailure(e);
    }

}

