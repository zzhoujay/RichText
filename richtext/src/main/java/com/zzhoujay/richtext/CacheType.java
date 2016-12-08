package com.zzhoujay.richtext;

import android.support.annotation.IntDef;

/**
 * Created by zhou on 2016/12/5.
 */
@IntDef({CacheType.NONE, CacheType.LAYOUT, CacheType.ALL})
public @interface CacheType {
    int NONE = 0;
    int LAYOUT = 1;
    @Deprecated
    int ALL = 2;
}
