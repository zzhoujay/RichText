package com.zzhoujay.richtext.callback;

import java.util.List;

/**
 * Created by zhou on 16-5-28.
 */
public interface OnImageClickListener {
    /**
     * 图片被点击后的回调方法
     *
     * @param imageUrls 本篇富文本内容里的全部图片
     * @param position  点击处图片在imageUrls中的位置
     */
    void imageClicked(List<String> imageUrls, int position);
}