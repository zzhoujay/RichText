package com.zzhoujay.richtext.exceptions;

/**
 * Created by zhou on 2017/10/2.
 * BitmapInputStreamNullPointException
 */

public class BitmapInputStreamNullPointException extends RuntimeException {


    private static final String MESSAGE = "Bitmap InputStream cannot be null";

    public BitmapInputStreamNullPointException() {
        super(MESSAGE);
    }
}
