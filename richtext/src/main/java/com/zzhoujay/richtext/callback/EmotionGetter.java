package com.zzhoujay.richtext.callback;

import android.graphics.drawable.Drawable;

/**
 * Created by lqn on 2019.1.4.
 * 获取表情Drawable接口
 */

public interface EmotionGetter {

    Drawable getDrawable(String emotionKey);

}
