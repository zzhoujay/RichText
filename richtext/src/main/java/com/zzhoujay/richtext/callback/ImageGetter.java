package com.zzhoujay.richtext.callback;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichTextConfig;

/**
 * Created by zhou on 2016/12/3.
 */

public interface ImageGetter {

    Drawable getDrawable(ImageHolder holder, RichTextConfig config, TextView textView);

}
