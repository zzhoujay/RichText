package com.zzhoujay.richtext;

import android.support.annotation.IntDef;

/**
 * Created by zhou on 16-10-24.
 * Image Load State
 */
@SuppressWarnings("WeakerAccess")
@IntDef({RichState.ready, RichState.loading, RichState.loaded})
public @interface RichState {
    int ready = 0; // 未开始加载
    int loading = 1; // 加载中
    int loaded = 2; // 加载完毕
}
