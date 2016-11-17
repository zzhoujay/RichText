package com.zzhoujay.richtext.spans;

import android.annotation.SuppressLint;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

import com.zzhoujay.richtext.LinkHolder;
import com.zzhoujay.richtext.callback.OnURLClickListener;
import com.zzhoujay.richtext.callback.OnUrlLongClickListener;

/**
 * Created by zhou on 16-5-28.
 * LongClickableURLSpan
 */
@SuppressLint("ParcelCreator")
public class LongClickableURLSpan extends URLSpan implements LongClickableSpan {


    private final OnURLClickListener onURLClickListener;
    private final OnUrlLongClickListener onUrlLongClickListener;
    private final LinkHolder linkHolder;

    public LongClickableURLSpan(String url, OnURLClickListener onURLClickListener, OnUrlLongClickListener onUrlLongClickListener) {
        this(url, onURLClickListener, onUrlLongClickListener, new LinkHolder(url));
    }

    public LongClickableURLSpan(String url, OnURLClickListener onURLClickListener, OnUrlLongClickListener onUrlLongClickListener, LinkHolder linkHolder) {
        super(url);
        this.onURLClickListener = onURLClickListener;
        this.onUrlLongClickListener = onUrlLongClickListener;
        this.linkHolder = linkHolder;
    }

    public LongClickableURLSpan(String url, OnURLClickListener onURLClickListener) {
        this(url, onURLClickListener, null);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(linkHolder.getColor());
        ds.setUnderlineText(linkHolder.isUnderLine());
    }

    @Override
    public void onClick(View widget) {
        if (onURLClickListener != null && onURLClickListener.urlClicked(getURL())) {
            return;
        }
        super.onClick(widget);
    }

    @Override
    public boolean onLongClick(View widget) {
        return onUrlLongClickListener != null && onUrlLongClickListener.urlLongClick(getURL());
    }
}
