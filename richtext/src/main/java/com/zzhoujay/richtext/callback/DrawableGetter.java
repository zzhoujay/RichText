package com.zzhoujay.richtext.callback;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichTextConfig;

/**
 * Created by zhou on 2017/11/6.
 * DrawableGetter
 */

public interface DrawableGetter {

    /**
     * 获取图片
     *
     * @param holder   ImageHolder
     * @param config   RichTextConfig
     * @param textView TextView
     * @return Drawable
     * @see ImageHolder
     * @see RichTextConfig
     */
    Drawable getDrawable(ImageHolder holder, RichTextConfig config, TextView textView);


}