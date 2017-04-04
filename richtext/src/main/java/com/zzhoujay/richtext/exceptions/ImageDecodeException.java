package com.zzhoujay.richtext.exceptions;

import android.annotation.TargetApi;
import android.os.Build;

/**
 * Created by zhou on 2017/4/4.
 */

public class ImageDecodeException extends Exception {

    private static final String IMAGE_DECODE_FAILURE = "Image Decode Failure";

    public ImageDecodeException() {
        super(IMAGE_DECODE_FAILURE);
    }

    public ImageDecodeException(Throwable cause) {
        super(IMAGE_DECODE_FAILURE, cause);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public ImageDecodeException(Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(IMAGE_DECODE_FAILURE, cause, enableSuppression, writableStackTrace);
    }
}
