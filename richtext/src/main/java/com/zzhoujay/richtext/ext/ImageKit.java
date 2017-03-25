package com.zzhoujay.richtext.ext;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhou on 2017/3/25.
 */

public class ImageKit {

    private static final String GIF_FILE_HEAD = "47494638";

    public static boolean isGif(InputStream inputStream) {
        if (inputStream.markSupported()) {
            inputStream.mark(10);
            byte[] bs = new byte[4];
            try {
                //noinspection ResultOfMethodCallIgnored
                inputStream.read(bs, 0, bs.length);
                inputStream.reset();
                return GIF_FILE_HEAD.equals(bytesToHexString(bs));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isGif(byte[] bytes) {
        byte[] bs = new byte[4];
        System.arraycopy(bytes, 0, bs, 0, 4);
        return GIF_FILE_HEAD.equals(bytesToHexString(bs));
    }

    public static boolean isGif(String path) {
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            fileInputStream = new FileInputStream(path);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            return isGif(bufferedInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

}
