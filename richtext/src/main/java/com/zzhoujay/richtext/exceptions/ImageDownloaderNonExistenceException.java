package com.zzhoujay.richtext.exceptions;

/**
 * Created by zhou on 2017/9/11.
 * 图片加载器不存在，异常
 */

public class ImageDownloaderNonExistenceException extends RuntimeException {

    public ImageDownloaderNonExistenceException() {
        super("ImageDownloader 为空或未设置");
    }
}
