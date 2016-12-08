package com.zzhoujay.richtext.spans;

import android.annotation.SuppressLint;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

import com.zzhoujay.richtext.LinkHolder;
import com.zzhoujay.richtext.callback.OnUrlClickListener;
import com.zzhoujay.richtext.callback.OnUrlLongClickListener;

/**
 * Created by zhou on 16-5-28.
 * LongClickableURLSpan
 */
@SuppressLint("ParcelCreator")
public class LongClickableURLSpan extends URLSpan implements LongClickableSpan {


    private final OnUrlClickListener onUrlClickListener;
    private final OnUrlLongClickListener onUrlLongClickListener;
    private final LinkHolder linkHolder;

    public LongClickableURLSpan(LinkHolder linkHolder) {
        this(linkHolder, null, null);
    }

    public LongClickableURLSpan(LinkHolder linkHolder, OnUrlClickListener onUrlClickListener, OnUrlLongClickListener onUrlLongClickListener) {
        super(linkHolder.getUrl());
        this.onUrlClickListener = onUrlClickListener;
        this.onUrlLongClickListener = onUrlLongClickListener;
        this.linkHolder = linkHolder;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(linkHolder.getColor());
        ds.setUnderlineText(linkHolder.isUnderLine());
    }

    @Override
    public void onClick(View widget) {
        if (onUrlClickListener != null && onUrlClickListener.urlClicked(getURL())) {
            return;
        }
        super.onClick(widget);
    }

    @Override
    public boolean onLongClick(View widget) {
        return onUrlLongClickListener != null && onUrlLongClickListener.urlLongClick(getURL());
    }

}
