package com.zzhoujay.richtext.exceptions;

/**
 * Created by zhou on 2017/11/5.
 * HttpResponseCodeException
 */

public class HttpResponseCodeException extends RuntimeException {

    public HttpResponseCodeException(int code) {
        super("Http Response Code is :" + code);
    }
}
