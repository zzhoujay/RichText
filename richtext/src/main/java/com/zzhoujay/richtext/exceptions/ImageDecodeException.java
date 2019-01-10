package com.zzhoujay.richtext.exceptions;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Created by zhou on 2017/4/4.
 * ImageDecodeException
 */

public class ImageDecodeException extends Exception {

    private static final String IMAGE_DECODE_FAILURE = "Image Decode Failure";

    private OutOfMemoryError error;

    public ImageDecodeException() {
        super(IMAGE_DECODE_FAILURE);
    }

    public ImageDecodeException(Throwable cause) {
        super(IMAGE_DECODE_FAILURE, cause);
        if (cause instanceof OutOfMemoryError) {
            error = (OutOfMemoryError) cause;
        }
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.N)
    public ImageDecodeException(Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(IMAGE_DECODE_FAILURE, cause, enableSuppression, writableStackTrace);
        if (cause instanceof OutOfMemoryError) {
            error = (OutOfMemoryError) cause;
        }
    }

    @Override
    public void printStackTrace() {
        if (error != null) {
            error.printStackTrace();
        } else {
            super.printStackTrace();
        }
    }

    @Override
    public void printStackTrace(PrintStream s) {
        if (error != null) {
            error.printStackTrace(s);
        } else {
            super.printStackTrace(s);
        }
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        if (error != null) {
            error.printStackTrace(s);
        } else {
            super.printStackTrace(s);
        }
    }


}
