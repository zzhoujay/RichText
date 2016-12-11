package com.zzhoujay.richtext.ig;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
class DefaultCallback extends AbstractImageLoader implements Callback {

    private static final int MARK_POSITION = 1024 * 1024;

    DefaultCallback(ImageHolder holder, RichTextConfig config, TextView textView, DrawableWrapper drawableWrapper, ImageLoadNotify iln) {
        super(holder, config, textView, drawableWrapper, iln);
        onLoading();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try {
            InputStream inputStream = response.body().byteStream();
            BufferedInputStream stream = new BufferedInputStream(inputStream);
            BitmapFactory.Options options = new BitmapFactory.Options();
            int[] inDimens = getDimensions(stream, options);
            options.inSampleSize = onSizeReady(inDimens[0], inDimens[1]);
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            onResourceReady(decodeStream(stream, options));
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

    private int[] getDimensions(InputStream inputStream, BitmapFactory.Options options) {
        options.inJustDecodeBounds = true;
        decodeStream(inputStream, options);
        options.inJustDecodeBounds = false;
        return new int[]{options.outWidth, options.outHeight};
    }

    private Bitmap decodeStream(InputStream inputStream, BitmapFactory.Options options) {
        if (options.inJustDecodeBounds) {
            inputStream.mark(MARK_POSITION);
        }
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        if (options.inJustDecodeBounds) {
            try {
                inputStream.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

}

class CallWrapper implements Cancelable {
    private Call call;

    CallWrapper(Call call) {
        this.call = call;
    }

    @Override
    public void cancel() {
        if (call != null && !call.isCanceled()) {
            call.cancel();
            call = null;
        }
    }
}