package com.zzhoujay.richtext.exceptions;

/**
 * Created by zhou on 2017/11/5.
 * ImageLoadCancelledException
 */

public class ImageLoadCancelledException extends Exception {

    public ImageLoadCancelledException() {
        super("Image load has been cancelled");
    }
}
