package com.zzhoujay.richtext.spans;

import android.view.View;

/**
 * Created by zhou on 16-8-4.
 * LongClickable
 */
public interface LongClickable {
    /**
     * 长按点击时间
     *
     * @param widget 　view
     * @return true:已处理，false:交由onClick处理
     */
    boolean onLongClick(View widget);
}
