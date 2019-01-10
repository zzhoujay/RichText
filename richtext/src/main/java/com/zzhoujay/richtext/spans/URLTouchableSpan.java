package com.zzhoujay.richtext.spans;

import android.annotation.SuppressLint;
import android.view.View;

import com.qmuiteam.qmui.span.QMUITouchableSpan;
import com.zzhoujay.richtext.LinkHolder;
import com.zzhoujay.richtext.callback.OnUrlClickListener;

/**
 * Created by lqn on 2019.1.9
 * 继承QMUITouchableSpan的可点击Span，会保存url，点击时返回该url
 */
@SuppressLint("ParcelCreator")
public class URLTouchableSpan extends QMUITouchableSpan {


    private final OnUrlClickListener mUrlClickListener;
    private final LinkHolder mLinkHolder;

    @SuppressWarnings("unused")
    public URLTouchableSpan(LinkHolder linkHolder) {
        this(linkHolder, null);
    }

    public URLTouchableSpan(LinkHolder linkHolder, OnUrlClickListener urlClickListener) {
        super(linkHolder.getNormalTextColor(), linkHolder.getPressedTextColor(),
                linkHolder.getNormalBackgroundColor(), linkHolder.getPressedBackgroundColor());
        this.mLinkHolder = linkHolder;
        this.mUrlClickListener = urlClickListener;
    }

    @Override
    public void onSpanClick(View widget) {
        if (mUrlClickListener != null) {
            mUrlClickListener.urlClicked(mLinkHolder.getUrl());
        }
    }

    public LinkHolder getLinkHolder() {
        return mLinkHolder;
    }

}
