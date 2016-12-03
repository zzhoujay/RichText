package com.zzhoujay.richtext.parser;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;

/**
 * Created by zhou on 16-7-27.
 */
public class Html2SpannedParser implements SpannedParser {

    private Html.TagHandler tagHandler;

    public Html2SpannedParser(Html.TagHandler tagHandler) {
        this.tagHandler = tagHandler;
    }

    @Override
    public Spanned parse(String source, final ImageGetterWrapper imageGetter) {
        return Html.fromHtml(source, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                return imageGetter.getDrawable(source);
            }
        }, tagHandler);
    }
}
