package com.zzhoujay.richtext.spans;

import android.annotation.SuppressLint;
import android.view.View;

import com.qmuiteam.qmui.span.QMUITouchableSpan;
import com.zzhoujay.richtext.LinkHolder;
import com.zzhoujay.richtext.callback.OnUrlClickListener;
import com.zzhoujay.richtext.callback.OnUrlLongClickListener;

/**
 * Created by lqn on 2019.1.9
 * 继承QMUITouchableSpan的可点击Span，会保存url，点击时返回该url
 */
@SuppressLint("ParcelCreator")
public class URLTouchableSpan extends QMUITouchableSpan {


    private final OnUrlClickListener mUrlClickListener;
    private final OnUrlLongClickListener mUrlLongClickListener;
    private final LinkHolder mLinkHolder;
    private long pressTime = -1;    //记录按下时间

    @SuppressWarnings("unused")
    public URLTouchableSpan(LinkHolder linkHolder) {
        this(linkHolder, null, null);
    }

    public URLTouchableSpan(LinkHolder linkHolder, OnUrlClickListener urlClickListener, OnUrlLongClickListener urlLongClickListener) {
        super(linkHolder.getNormalTextColor(), linkHolder.getPressedTextColor(),
                linkHolder.getNormalBackgroundColor(), linkHolder.getPressedBackgroundColor());
        this.mLinkHolder = linkHolder;
        this.mUrlClickListener = urlClickListener;
        this.mUrlLongClickListener = urlLongClickListener;
    }

    @Override
    public void setPressed(boolean isSelected) {
        super.setPressed(isSelected);
        if (isSelected) {
            pressTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onSpanClick(View widget) {
        if (mUrlLongClickListener == null) {
            if (mUrlClickListener != null) {
                mUrlClickListener.urlClicked(mLinkHolder.getUrl());
            }
        } else {
            if (pressTime != -1 && System.currentTimeMillis() - pressTime > 500) {
                mUrlLongClickListener.urlLongClick(mLinkHolder.getUrl());
            } else {
                if (mUrlClickListener != null) {
                    mUrlClickListener.urlClicked(mLinkHolder.getUrl());
                }
            }
        }
    }

    public LinkHolder getLinkHolder() {
        return mLinkHolder;
    }

}
