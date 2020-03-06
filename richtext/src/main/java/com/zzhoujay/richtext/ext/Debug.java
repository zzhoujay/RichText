package com.zzhoujay.richtext.ext;

import android.util.Log;

import com.zzhoujay.richtext.RichText;

/**
 * Created by zhou on 2017/4/4.
 * Debug Utils
 */

public class Debug {

    public static final String PREF = " --> ";
    private static final String TAG = "RichText";

    public static void e(Throwable e) {
        if (RichText.debugMode) {
            e.printStackTrace();
        }
    }

    public static void log(String tag, String message) {
        if (RichText.debugMode) {
            Log.i(TAG, tag + PREF + message);
        }
    }

    public static void log(String tag, String message, Throwable e) {
        if (RichText.debugMode) {
            Log.i(TAG, tag + PREF + message, e);
        }
    }

    public static void loge(String tag, String message, Throwable e) {
        Log.e(TAG, tag + PREF + message, e);
    }

    public static void loge(String tag, String message) {
        Log.e(TAG, tag + PREF + message);
    }
}
