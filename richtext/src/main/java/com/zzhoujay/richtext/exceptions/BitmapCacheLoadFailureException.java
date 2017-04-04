package com.zzhoujay.richtext.exceptions;

import android.annotation.TargetApi;
import android.os.Build;

/**
 * Created by zhou on 2017/4/4.
 */

public class BitmapCacheLoadFailureException extends Exception {

    private static final String MESSAGE = "Bitmap 缓存加载失败";

    public BitmapCacheLoadFailureException() {
        super(MESSAGE);
    }

    public BitmapCacheLoadFailureException(Throwable cause) {
        super(MESSAGE, cause);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public BitmapCacheLoadFailureException(Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(MESSAGE, cause, enableSuppression, writableStackTrace);
    }
}
