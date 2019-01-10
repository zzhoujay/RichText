package com.zzhoujay.richtext.ig;

import com.zzhoujay.richtext.callback.BitmapStream;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhou on 2017/9/11.
 * 图片下载器
 */

public interface ImageDownloader {


    /**
     * 下载图片并返回流，无需异步
     *
     * @param source 图片URL
     * @return 下载到的图片的输入流
     * @throws IOException 抛出的IOException将会被处理
     */
    BitmapStream download(String source) throws IOException;

}
