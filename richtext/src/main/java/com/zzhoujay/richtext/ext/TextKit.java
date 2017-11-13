package com.zzhoujay.richtext.ext;

import android.text.TextUtils;

/**
 * Created by zhou on 2017/2/21.
 * TextKit
 */

public class TextKit {

    private static final String ASSETS_PREFIX = "file:///android_asset/";
    private static final String LOCAL_FILE_PREFIX = "/";

    public static boolean isLocalPath(String path) {
        return !TextUtils.isEmpty(path) && path.startsWith(LOCAL_FILE_PREFIX);
    }

    public static boolean isAssetPath(String path) {
        return !TextUtils.isEmpty(path) && path.startsWith(ASSETS_PREFIX);
    }
}
