package com.zzhoujay.richtext.ig;

/**
 * Created by zhou on 2017/9/11.
 * 图片下载器
 */

public interface ImageDownloader {

    Cancelable download(String source, ImageDownloadCallback callback);

}
