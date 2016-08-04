package com.zzhoujay.richtext.spans;

import android.text.style.URLSpan;
import android.view.View;

import com.zzhoujay.richtext.callback.OnURLClickListener;
import com.zzhoujay.richtext.callback.OnUrlLongClickListener;

/**
 * Created by zhou on 16-5-28.
 */
public class LongCallableURLSpan extends URLSpan implements LongClickable {

    private OnURLClickListener onURLClickListener;
    private OnUrlLongClickListener onUrlLongClickListener;

    public LongCallableURLSpan(String url, OnURLClickListener onURLClickListener, OnUrlLongClickListener onUrlLongClickListener) {
        super(url);
        this.onURLClickListener = onURLClickListener;
        this.onUrlLongClickListener = onUrlLongClickListener;
    }

    public LongCallableURLSpan(String url, OnURLClickListener onURLClickListener) {
        this(url, onURLClickListener, null);
    }


    @Override
    public void onClick(View widget) {
        if (onURLClickListener != null && onURLClickListener.urlClicked(getURL())) {
            return;
        }
        super.onClick(widget);
    }

    public boolean onLongClick(View widget) {
        return onUrlLongClickListener != null && onUrlLongClickListener.urlLongClick(getURL());
    }
}
