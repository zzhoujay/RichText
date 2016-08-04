package com.zzhoujay.richtext.callback;

import java.util.List;

/**
 * Created by zhou on 16-8-4.
 * OnImageLongClickListener
 */
public interface OnImageLongClickListener {
    /**
     * 图片长按回调
     *
     * @param imageUrls 　全部图片链接
     * @param position  　当前图片位置
     * @return true:已处理，false:未处理并交由imageClicked处理
     */
    boolean imageLongClicked(List<String> imageUrls, int position);
}
