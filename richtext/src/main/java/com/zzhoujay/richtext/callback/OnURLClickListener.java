package com.zzhoujay.richtext.callback;

/**
 * Created by zhou on 16-5-28.
 */
public interface OnUrlClickListener {

    /**
     * 超链接点击得回调方法
     *
     * @param url 点击得url
     * @return true：已处理，false：未处理（会进行默认处理）
     */
    boolean urlClicked(String url);
}