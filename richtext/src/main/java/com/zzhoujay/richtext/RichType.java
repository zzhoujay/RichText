package com.zzhoujay.richtext;

import android.support.annotation.IntDef;

/**
 * Created by zhou on 16-7-27.
 * 富文本类型
 */
@IntDef({RichType.HTML, RichType.MARKDOWN})
public @interface RichType {
    int HTML = 0;
    int MARKDOWN = 1;
}
