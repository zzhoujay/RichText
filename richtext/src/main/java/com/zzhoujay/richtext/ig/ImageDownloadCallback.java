package com.zzhoujay.richtext.ig;

import java.io.InputStream;

/**
 * Created by zhou on 2017/9/11.
 * 图片下载完成回调接口
 */
public interface ImageDownloadCallback {

    void success(InputStream inputStream);

    void failure(Exception e);

}
