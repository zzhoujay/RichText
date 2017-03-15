package com.zzhoujay.richtext.callback;

/**
 * Created by zhou on 2017/3/11.
 */

public interface Callback {

    /**
     * 解析完成回调
     *
     * @param imageLoadDone 图片全部加载成功
     */
    void done(boolean imageLoadDone);

}
