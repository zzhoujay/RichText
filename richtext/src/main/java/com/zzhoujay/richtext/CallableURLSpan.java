package com.zzhoujay.richtext;

import android.os.Parcel;
import android.text.style.URLSpan;
import android.view.View;

/**
 * Created by zhou on 16-5-28.
 */
class CallableURLSpan extends URLSpan {

    private OnURLClickListener onURLClickListener;

    public CallableURLSpan(String url, OnURLClickListener onURLClickListener) {
        super(url);
        this.onURLClickListener = onURLClickListener;
    }

    @SuppressWarnings("unused")
    public CallableURLSpan(Parcel src, OnURLClickListener onURLClickListener) {
        super(src);
        this.onURLClickListener = onURLClickListener;
    }

    @Override
    public void onClick(View widget) {
        if (onURLClickListener != null && onURLClickListener.urlClicked(getURL())) {
            return;
        }
        super.onClick(widget);
    }
}
