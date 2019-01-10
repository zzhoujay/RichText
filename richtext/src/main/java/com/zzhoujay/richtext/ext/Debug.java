package com.zzhoujay.richtext.ext;

import com.zzhoujay.richtext.RichText;

/**
 * Created by zhou on 2017/4/4.
 * Debug Utils
 */

public class Debug {

    public static void e(Throwable e) {
        if (RichText.debugMode) {
            e.printStackTrace();
        }
    }
}
