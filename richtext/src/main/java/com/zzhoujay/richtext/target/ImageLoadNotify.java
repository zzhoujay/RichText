package com.zzhoujay.richtext.target;

/**
 * Created by zhou on 16-10-24.
 * Image Load Notify
 */
public interface ImageLoadNotify {
    /**
     * 图片加载完成
     *
     * @param target target
     */
    void done(ImageTarget target);
}
