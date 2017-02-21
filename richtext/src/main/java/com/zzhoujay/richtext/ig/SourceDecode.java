package com.zzhoujay.richtext.ig;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhou on 2017/2/21.
 */
interface SourceDecode<T> {

    SourceDecode<byte[]> BASE64_SOURCE_DECODE = new SourceDecode<byte[]>() {
        @Override
        public Bitmap decode(byte[] bytes, BitmapFactory.Options options) {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        }
    };

    SourceDecode<String> LOCAL_FILE_SOURCE_DECODE = new SourceDecode<String>() {
        @Override
        public Bitmap decode(String s, BitmapFactory.Options options) {
            return BitmapFactory.decodeFile(s, options);
        }
    };

    SourceDecode<InputStream> REMOTE_SOURCE_DECODE = new SourceDecode<InputStream>() {

        private static final int MARK_POSITION = 1024 * 1024;

        @Override
        public Bitmap decode(InputStream inputStream, BitmapFactory.Options options) {
            BufferedInputStream stream;
            if (inputStream instanceof BufferedInputStream) {
                stream = (BufferedInputStream) inputStream;
            } else {
                stream = new BufferedInputStream(inputStream);
            }
            if (options.inJustDecodeBounds) {
                stream.mark(MARK_POSITION);
            }
            Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
            if (options.inJustDecodeBounds) {
                try {
                    stream.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return bitmap;
        }
    };

    Bitmap decode(T t, BitmapFactory.Options options);

}
