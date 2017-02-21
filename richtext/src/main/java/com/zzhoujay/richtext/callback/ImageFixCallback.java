package com.zzhoujay.richtext.callback;

import com.zzhoujay.richtext.ImageHolder;

/**
 * Created by zhou on 16-5-28.
 * ImageFixCallback
 */
public interface ImageFixCallback {

    /**
     * 加载开始前
     *
     * @param holder ImageHolder
     */
    void onInit(ImageHolder holder);

    /**
     * 正在加载，此时设置holder的宽高将会运用到placeholder
     *
     * @param holder ImageHolder
     */
    void onLoading(ImageHolder holder);

    /**
     * 图片下载完成（未加载到内存）并且尺寸已获取，此时给holder设置最大宽高为图片加载到内存后的最大宽高，用与压缩图片
     *
     * @param holder ImageHolder
     * @param width  图片原始宽度
     * @param height 图片原始高度
     */
    void onSizeReady(ImageHolder holder, int width, int height);

    /**
     * 图片已加载到内存，此时给holder设置宽高将是最后图片显示的大小
     *
     * @param holder ImageHolder
     * @param width  图片加载到内存后的宽度
     * @param height 图片加载到内存后的高度
     */
    void onImageReady(ImageHolder holder, int width, int height);

    /**
     * 图片加载失败，此时设置holder的宽高将会运用到errorImage
     *
     * @param holder ImageHolder
     * @param e      Exception
     */
    void onFailure(ImageHolder holder, Exception e);

}
