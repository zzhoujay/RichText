package com.zzhoujay.richtext.parser;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;

import com.zzhoujay.richtext.LinkHolder;
import com.zzhoujay.richtext.RichTextConfig;
import com.zzhoujay.richtext.callback.OnImageClickListener;
import com.zzhoujay.richtext.callback.OnImageLongClickListener;
import com.zzhoujay.richtext.spans.ClickableImageSpan;
import com.zzhoujay.richtext.spans.LongClickableURLSpan;

import java.util.ArrayList;

/**
 * Created by zhou on 2016/12/5.
 */

public class CachedSpannedParser {

    public static class Cached {
    }

    public int parse(SpannableStringBuilder ssb, ImageGetterWrapper imageGetter, RichTextConfig config) {
        boolean cached = isCached(ssb);
        handleClick(ssb, config, cached);
        return handleImage(ssb, imageGetter, config, cached);
    }

    private void handleClick(SpannableStringBuilder ssb, RichTextConfig config, boolean cached) {
        if (cached) {
            LongClickableURLSpan[] lcus = ssb.getSpans(0, ssb.length(), LongClickableURLSpan.class);
            if (lcus != null && lcus.length > 0) {
                for (LongClickableURLSpan lcu : lcus) {
                    resetLinkSpan(ssb, config, lcu);
                }
            }
        } else {
            if (config.clickable >= 0) {
                // 处理超链接点击事件
                URLSpan[] urlSpans = ssb.getSpans(0, ssb.length(), URLSpan.class);
                for (int i = 0, size = urlSpans == null ? 0 : urlSpans.length; i < size; i++) {
                    resetLinkSpan(ssb, config, urlSpans[i]);
                }
            } else {
                // 移除URLSpan
                URLSpan[] urlSpans = ssb.getSpans(0, ssb.length(), URLSpan.class);
                for (int i = 0, size = urlSpans == null ? 0 : urlSpans.length; i < size; i++) {
                    ssb.removeSpan(urlSpans[i]);
                }
            }
        }
    }

    private void resetLinkSpan(SpannableStringBuilder ssb, RichTextConfig config, URLSpan urlSpan) {
        int start = ssb.getSpanStart(urlSpan);
        int end = ssb.getSpanEnd(urlSpan);
        ssb.removeSpan(urlSpan);
        LinkHolder linkHolder = new LinkHolder(urlSpan.getURL());
        if (config.linkFixCallback != null) {
            config.linkFixCallback.fix(linkHolder);
        }
        LongClickableURLSpan longClickableURLSpan = new LongClickableURLSpan(linkHolder, config.onUrlClickListener, config.onUrlLongClickListener);
        ssb.setSpan(longClickableURLSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private int handleImage(SpannableStringBuilder ssb, ImageGetterWrapper imageGetterWrapper, RichTextConfig config, boolean cached) {
        if (cached) {
            ClickableImageSpan[] cis = ssb.getSpans(0, ssb.length(), ClickableImageSpan.class);
            if (cis != null && cis.length > 0) {
                for (ClickableImageSpan ci : cis) {
                    int start = ssb.getSpanStart(ci);
                    int end = ssb.getSpanEnd(ci);
                    ssb.removeSpan(ci);
                    OnImageClickListener onImageClickListener = null;
                    OnImageLongClickListener onImageLongClickListener = null;
                    if (config.clickable > 0) {
                        onImageClickListener = config.onImageClickListener;
                        onImageLongClickListener = config.onImageLongClickListener;
                    }
                    Drawable drawable = imageGetterWrapper.getDrawable(ci.getSource());
                    if (drawable == null) {
                        drawable = new ColorDrawable(Color.TRANSPARENT);
                    }
                    ClickableImageSpan nci = new ClickableImageSpan(drawable, ci, onImageClickListener, onImageLongClickListener);
                    ssb.setSpan(nci, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                return cis.length;
            }
        } else if (!config.noImage) {
            ImageSpan[] iss = ssb.getSpans(0, ssb.length(), ImageSpan.class);
            if (iss != null && iss.length > 0) {
                ArrayList<String> imageUrls = new ArrayList<>(iss.length);
                for (int i = 0; i < iss.length; i++) {
                    ImageSpan imageSpan = iss[i];
                    String imageUrl = imageSpan.getSource();
                    imageUrls.add(imageUrl);

                    int start = ssb.getSpanStart(imageSpan);
                    int end = ssb.getSpanEnd(imageSpan);
                    ClickableSpan[] clickableSpans = ssb.getSpans(start, end, ClickableSpan.class);
                    if (clickableSpans != null && clickableSpans.length != 0) {
                        for (ClickableSpan cs : clickableSpans) {
                            ssb.removeSpan(cs);
                        }
                    }
                    OnImageClickListener onImageClickListener = null;
                    OnImageLongClickListener onImageLongClickListener = null;
                    if (config.clickable > 0) {
                        onImageClickListener = config.onImageClickListener;
                        onImageLongClickListener = config.onImageLongClickListener;
                    }
                    Drawable drawable = imageGetterWrapper.getDrawable(imageUrl);
                    if (drawable == null) {
                        drawable = new ColorDrawable(Color.TRANSPARENT);
                    }
                    ClickableImageSpan cacheImageSpan = new ClickableImageSpan(drawable, imageUrls, i, onImageClickListener, onImageLongClickListener);
                    ssb.removeSpan(imageSpan);
                    ssb.setSpan(cacheImageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                return iss.length;
            }
        }
        return 0;
    }

    private boolean isCached(SpannableStringBuilder ssb) {
        Cached[] cs = ssb.getSpans(0, ssb.length(), Cached.class);
        return cs != null && cs.length > 0;
    }

    private void clearCachedMark(SpannableStringBuilder ssb) {
        Cached[] cs = ssb.getSpans(0, ssb.length(), Cached.class);
        if (cs != null && cs.length > 0) {
            for (Cached c : cs) {
                ssb.removeSpan(c);
            }
        }
    }
}
