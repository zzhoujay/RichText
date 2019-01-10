package com.zzhoujay.richtext.parser;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by zhou on 16-7-27.
 * Html2SpannedParser
 */
public class Html2SpannedParser implements SpannedParser {

    private static final String TAG = "Html2SpannedParser";

    private static final String Z_HTML_CLASS_NAME = "com.zzhoujay.html.Html";
    private static final Method Z_FROM_HTML_METHOD;

    static {
        Method fromHtml = null;
        try {
            fromHtml = Class.forName(Z_HTML_CLASS_NAME).getMethod("fromHtml", String.class, Html.ImageGetter.class, Html.TagHandler.class);
        } catch (Exception ignore) {
        }
        Z_FROM_HTML_METHOD = fromHtml;
    }

    private Html.TagHandler tagHandler;

    public Html2SpannedParser(Html.TagHandler tagHandler) {
        this.tagHandler = tagHandler;
    }

    @Override
    public Spanned parse(String source) {
        if (Z_FROM_HTML_METHOD != null) {
            try {
                return (Spanned) Z_FROM_HTML_METHOD.invoke(null, source, null, tagHandler);
            } catch (Exception e) {
                Log.d(TAG, "Z_FROM_HTML_METHOD invoke failure", e);
            }
        }
        return Html.fromHtml(source, null, tagHandler);
    }
}
