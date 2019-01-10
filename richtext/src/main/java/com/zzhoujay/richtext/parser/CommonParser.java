package com.zzhoujay.richtext.parser;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;

import com.zzhoujay.richtext.LinkHolder;
import com.zzhoujay.richtext.R;
import com.zzhoujay.richtext.callback.EmotionGetter;
import com.zzhoujay.richtext.callback.LinkFixCallback;
import com.zzhoujay.richtext.callback.OnUrlClickListener;
import com.zzhoujay.richtext.spans.URLTouchableSpan;
import com.zzhoujay.richtext.spans.VerticalCenterImageSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zzhoujay.richtext.parser.ParserRegex.AUTHOR_REGEX;
import static com.zzhoujay.richtext.parser.ParserRegex.EMOTION_REGEX;

/**
 * 公用解释器
 */
public class CommonParser {

    /**
     * 解析字符串，只针对表情、作者标识、@链接、普通链接的解析
     *
     * @param context
     * @param text             输入字符串
     * @param textSize         TextView的textSize，用这个参数计算出表情的大小
     * @param parseAuthor      是否需要解析"作者"标签
     * @param emotionGetter    获取表情Drawable的接口
     * @param linkFixCallback  链接文字样式的回调
     * @param urlClickListener 链接文字的点击回调
     * @return 返回解析好的SpannableStringBuilder
     */
    public static SpannableStringBuilder parseString(Context context, String text, float textSize, boolean parseAuthor, EmotionGetter emotionGetter,
                                                     LinkFixCallback linkFixCallback, OnUrlClickListener urlClickListener) {
        Spanned spanned = Html.fromHtml(text);
        SpannableStringBuilder ssb = new SpannableStringBuilder(spanned);
        parseEmotion(ssb, textSize, emotionGetter);
        if (parseAuthor) parseAuthor(ssb, context, textSize);
        parseLink(ssb, linkFixCallback, urlClickListener);
        return ssb;
    }

    /**
     * 解析SpannableStringBuilder，用表情ImageSpan替代表情字符串
     *
     * @param ssb           输入的SpannableStringBuilder
     * @param textSize      TextView的textSize，用这个参数计算出表情的大小
     * @param emotionGetter 获取表情Drawable的接口
     * @return
     */
    public static void parseEmotion(SpannableStringBuilder ssb, float textSize, EmotionGetter emotionGetter) {
        if (emotionGetter == null) return;
        Matcher matcher = Pattern.compile(EMOTION_REGEX).matcher(ssb);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            Drawable drawable = emotionGetter.getDrawable(matcher.group());
            if (drawable != null) {
                int size = (int) (textSize / 12 * 18);
                drawable.setBounds(0, 0, size, size);
                ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                ssb.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    /**
     * 解析"作者"标签
     *
     * @param ssb      输入的SpannableStringBuilder
     * @param context  上下文
     * @param textSize TextView的textSize，用这个参数计算出作者标签的大小
     */
    public static void parseAuthor(SpannableStringBuilder ssb, Context context, float textSize) {
        Matcher matcher = Pattern.compile(AUTHOR_REGEX).matcher(ssb);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.iv_tag_author);
            if (drawable != null) {
                float height = textSize / 12 * 14;
                float width = height / 16 * 28;
                drawable.setBounds(0, 0, (int) width, (int) height);
                VerticalCenterImageSpan span = new VerticalCenterImageSpan(drawable);
                ssb.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    /**
     * 解析链接，设置链接文字的样式、点击回调
     *
     * @param ssb              输入的SpannableStringBuilder
     * @param linkFixCallback  文字样式的回调
     * @param urlClickListener 文字的点击回调
     */
    private static void parseLink(SpannableStringBuilder ssb, LinkFixCallback linkFixCallback, OnUrlClickListener urlClickListener) {
        URLSpan[] urlSpans = ssb.getSpans(0, ssb.length(), URLSpan.class);
        for (int i = 0, size = urlSpans == null ? 0 : urlSpans.length; i < size; i++) {
            resetLinkSpan(ssb, urlSpans[i], linkFixCallback, urlClickListener);
        }
    }

    public static void resetLinkSpan(SpannableStringBuilder ssb, URLSpan urlSpan, LinkFixCallback linkFixCallback, OnUrlClickListener urlClickListener) {
        int start = ssb.getSpanStart(urlSpan);
        int end = ssb.getSpanEnd(urlSpan);
        ssb.removeSpan(urlSpan);
        final LinkHolder linkHolder = new LinkHolder(urlSpan.getURL());
        if (linkFixCallback != null) {
            linkFixCallback.fix(linkHolder);
        }
        URLTouchableSpan urlTouchableSpan = new URLTouchableSpan(linkHolder, urlClickListener);
        ssb.setSpan(urlTouchableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


}
