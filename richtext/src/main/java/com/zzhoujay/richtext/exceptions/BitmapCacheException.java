package com.zzhoujay.richtext.exceptions;

/**
 * Created by zhou on 2017/10/7.
 * BitmapCacheException
 */

@SuppressWarnings("unused")
public class BitmapCacheException extends RuntimeException {

    private static final String MESSAGE = "Bitmap缓存过程异常";

    public BitmapCacheException() {
        super(MESSAGE);
    }

    public BitmapCacheException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
