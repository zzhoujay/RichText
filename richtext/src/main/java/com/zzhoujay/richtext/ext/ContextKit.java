package com.zzhoujay.richtext.ext;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

/**
 * Created by zhou on 2017/11/15.
 * ContextKit
 */
public class ContextKit {

    @SuppressWarnings("WeakerAccess")
    public static Activity getActivity(Context context) {
        if (context == null) {
            return null;
        }
        while (!(context instanceof Activity) && (context instanceof ContextWrapper)) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        if (context instanceof Activity) {
            return ((Activity) context);
        } else {
            return null;
        }
    }

    /**
     * 判断Activity是否已经结束
     *
     * @param context context
     * @return true：已结束
     */
    public static boolean activityIsAlive(Context context) {
        Activity activity = getActivity(context);
        if (activity == null) {
            return false;
        }
        if (activity.isFinishing()) {
            return false;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
                return false;
            }
        }
        return true;
    }

}
