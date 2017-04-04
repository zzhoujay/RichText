package com.zzhoujay.richtext.exceptions;

/**
 * Created by zhou on 2017/4/4.
 */

public class ImageWrapperMultiSourceException extends IllegalArgumentException {

    private static final String MESSAGE = "GifDrawable和Bitmap有且只有一个为null";

    public ImageWrapperMultiSourceException() {
        super(MESSAGE);
    }

    public ImageWrapperMultiSourceException(Throwable cause) {
        super(MESSAGE, cause);
    }

}
