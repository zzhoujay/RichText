package com.zzhoujay.richtext.ext;

import android.text.TextUtils;

/**
 * Created by zhou on 2017/2/21.
 */

public class TextKit {


    public static boolean isLocalPath(String path) {
        if (!TextUtils.isEmpty(path)) {
            return path.startsWith("/");
        }
        return false;
    }
}
