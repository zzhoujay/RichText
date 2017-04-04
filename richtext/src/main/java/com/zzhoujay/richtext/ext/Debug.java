package com.zzhoujay.richtext.ext;

import com.zzhoujay.richtext.RichText;

/**
 * Created by zhou on 2017/4/4.
 */

public class Debug {

    public static void e(Exception e) {
        if (RichText.debugMode) {
            e.printStackTrace();
        }
    }

}
