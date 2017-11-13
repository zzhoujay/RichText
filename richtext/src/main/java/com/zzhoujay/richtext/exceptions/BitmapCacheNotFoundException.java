package com.zzhoujay.richtext.exceptions;

import android.annotation.TargetApi;
import android.os.Build;

/**
 * Created by zhou on 2017/4/4.
 */

public class BitmapCacheNotfoudException extends Exception {

    private static final String MESSAGE = "Bitmap 缓存不存在";

    public BitmapCacheNotfoudException() {
        super(MESSAGE);
    }

    public BitmapCacheNotfoudException(Throwable cause) {
        super(MESSAGE, cause);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public BitmapCacheNotfoudException(Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(MESSAGE, cause, enableSuppression, writableStackTrace);
    }
}
