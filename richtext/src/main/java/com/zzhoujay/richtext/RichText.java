package com.zzhoujay.richtext;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.TintContextWrapper;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.widget.TextView;

import com.bumptech.glide.GenericRequestBuilder;
import com.zzhoujay.richtext.ext.HtmlTagHandler;
import com.zzhoujay.richtext.ext.LongClickableLinkMovementMethod;
import com.zzhoujay.richtext.parser.Html2SpannedParser;
import com.zzhoujay.richtext.parser.ImageGetterWrapper;
import com.zzhoujay.richtext.parser.Markdown2SpannedParser;
import com.zzhoujay.richtext.parser.SpannedParser;
import com.zzhoujay.richtext.spans.ClickableImageSpan;
import com.zzhoujay.richtext.spans.LongClickableURLSpan;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhou on 16-5-28.
 * 富文本生成器
 */
@SuppressWarnings("unused")
public class RichText implements ImageGetterWrapper {

    private static final String TAG_TARGET = "target";

    private static Matcher IMAGE_TAG_MATCHER = Pattern.compile("<(img|IMG)(.*?)>").matcher("");
    private static Matcher IMAGE_WIDTH_MATCHER = Pattern.compile("(width|WIDTH)=\"(.*?)\"").matcher("");
    private static Matcher IMAGE_HEIGHT_MATCHER = Pattern.compile("(height|HEIGHT)=\"(.*?)\"").matcher("");
    private static Matcher IMAGE_SRC_MATCHER = Pattern.compile("(src|SRC)=\"(.*?)\"").matcher("");

    private HashMap<String, ImageHolder> imageHolderMap;

    private int prepareCount;
    private int loadedCount;
    @RichState
    private int state;

    private final SpannedParser spannedParser;

    private final SoftReference<TextView> textViewSoftReference;
    private final RichTextConfig config;

    RichText(RichTextConfig config, TextView textView) {
        this.config = config;
        this.textViewSoftReference = new SoftReference<>(textView);
        if (config.richType == RichType.MARKDOWN) {
            spannedParser = new Markdown2SpannedParser(textView);
        } else {
            spannedParser = new Html2SpannedParser(new HtmlTagHandler(textView));
        }
        if (config.clickable > 0) {
            textView.setMovementMethod(new LongClickableLinkMovementMethod());
        } else if (config.clickable == 0) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public static RichTextConfig.RichTextConfigBuild from(String source) {
        return fromHtml(source);
    }

    public static RichTextConfig.RichTextConfigBuild fromHtml(String source) {
        return from(source, RichType.HTML);
    }

    public static RichTextConfig.RichTextConfigBuild fromMarkdown(String source) {
        return from(source, RichType.MARKDOWN);
    }

    public static RichTextConfig.RichTextConfigBuild from(String source, @RichType int richType) {
        return new RichTextConfig.RichTextConfigBuild(source, richType);
    }

    void generateAndSet() {
        final TextView textView = textViewSoftReference.get();
        if (textView != null) {
            textView.post(new Runnable() {
                @Override
                public void run() {
                    textView.setText(generateRichText());
                }
            });
        }
    }

    /**
     * 生成富文本
     *
     * @return Spanned
     */
    private CharSequence generateRichText() {
        String source = config.source;
        TextView textView = textViewSoftReference.get();
        if (textView == null) {
            return null;
        }
        state = RichState.loading;
        if (config.richType != RichType.MARKDOWN) {
            analyzeImages(source);
        } else {
            imageHolderMap = new HashMap<>();
        }

        Spanned spanned = spannedParser.parse(source, this);
        SpannableStringBuilder spannableStringBuilder;
        if (spanned instanceof SpannableStringBuilder) {
            spannableStringBuilder = (SpannableStringBuilder) spanned;
        } else {
            spannableStringBuilder = new SpannableStringBuilder(spanned);
        }
        if (config.clickable > 0) {
            // 处理图片得点击事件
            ImageSpan[] imageSpans = spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), ImageSpan.class);
            final List<String> imageUrls = new ArrayList<>();

            for (int i = 0, size = imageSpans.length; i < size; i++) {
                ImageSpan imageSpan = imageSpans[i];
                String imageUrl = imageSpan.getSource();
                int start = spannableStringBuilder.getSpanStart(imageSpan);
                int end = spannableStringBuilder.getSpanEnd(imageSpan);
                imageUrls.add(imageUrl);

                ClickableImageSpan clickableImageSpan = new ClickableImageSpan(imageSpan, imageUrls, i, config.onImageClickListener, config.onImageLongClickListener);
                // 去除其他的ClickableSpan
                ClickableSpan[] clickableSpans = spannableStringBuilder.getSpans(start, end, ClickableSpan.class);
                if (clickableSpans != null && clickableSpans.length != 0) {
                    for (ClickableSpan cs : clickableSpans) {
                        spannableStringBuilder.removeSpan(cs);
                    }
                }
                spannableStringBuilder.removeSpan(imageSpan);
                spannableStringBuilder.setSpan(clickableImageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        }
        if (config.clickable >= 0) {
            // 处理超链接点击事件
            URLSpan[] urlSpans = spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), URLSpan.class);

            for (int i = 0, size = urlSpans == null ? 0 : urlSpans.length; i < size; i++) {
                URLSpan urlSpan = urlSpans[i];

                int start = spannableStringBuilder.getSpanStart(urlSpan);
                int end = spannableStringBuilder.getSpanEnd(urlSpan);

                spannableStringBuilder.removeSpan(urlSpan);
                LinkHolder linkHolder = new LinkHolder(urlSpan.getURL());
                if (config.linkFixCallback != null) {
                    config.linkFixCallback.fix(linkHolder);
                }
                LongClickableURLSpan longClickableURLSpan = new LongClickableURLSpan(urlSpan.getURL(),
                        config.onUrlClickListener, config.onUrlLongClickListener, linkHolder);
                spannableStringBuilder.setSpan(longClickableURLSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else {
            // 移除URLSpan
            URLSpan[] urlSpans = spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), URLSpan.class);
            for (int i = 0, size = urlSpans == null ? 0 : urlSpans.length; i < size; i++) {
                spannableStringBuilder.removeSpan(urlSpans[i]);
            }
        }
        return spannableStringBuilder;
    }

    /**
     * 从文本中拿到<img/>标签,并获取图片url和宽高
     */
    private synchronized void analyzeImages(String text) {
        imageHolderMap = new HashMap<>();
        ImageHolder holder;
        int position = 0;
        IMAGE_TAG_MATCHER.reset(text);
        while (IMAGE_TAG_MATCHER.find()) {
            String image = IMAGE_TAG_MATCHER.group(2).trim();
            IMAGE_SRC_MATCHER.reset(image);
            String src = null;
            if (IMAGE_SRC_MATCHER.find()) {
                src = IMAGE_SRC_MATCHER.group(2).trim();
            }
            if (TextUtils.isEmpty(src)) {
                continue;
            }
            holder = new ImageHolder(src, position);
            IMAGE_WIDTH_MATCHER.reset(image);
            if (IMAGE_WIDTH_MATCHER.find()) {
                holder.setWidth(parseStringToInteger(IMAGE_WIDTH_MATCHER.group(2).trim()));
            }
            IMAGE_HEIGHT_MATCHER.reset(image);
            if (IMAGE_HEIGHT_MATCHER.find()) {
                holder.setHeight(parseStringToInteger(IMAGE_HEIGHT_MATCHER.group(2).trim()));
            }
            imageHolderMap.put(holder.getSource(), holder);
            position++;
        }
    }

    /**
     * 判断Activity是否已经结束
     *
     * @param context context
     * @return true：已结束
     */
    private static boolean activityIsAlive(Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof TintContextWrapper) {
            context = ((TintContextWrapper) context).getBaseContext();
        }
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return false;
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && ((Activity) context).isDestroyed()) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int parseStringToInteger(String integerStr) {
        int result = -1;
        if (!TextUtils.isEmpty(integerStr)) {
            try {
                result = Integer.parseInt(integerStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static boolean isGif(String path) {
        int index = path.lastIndexOf('.');
        return index > 0 && "gif".toUpperCase().equals(path.substring(index + 1).toUpperCase());
    }

    /**
     * 回收所有图片和任务
     */
    public void clear() {
        TextView textView = textViewSoftReference.get();
        if (textView != null) {
            textView.setText(null);
        }
        config.imageGetter.recycle();
    }


    private void setPlaceHolder(GenericRequestBuilder load) {
        if (config.placeHolderRes > 0) {
            load.placeholder(config.placeHolderRes);
        } else {
            load.placeholder(config.placeHolder);
        }
    }

    private void setErrorImage(GenericRequestBuilder load) {
        if (config.errorImageRes > 0) {
            load.error(config.errorImageRes);
        } else {
            load.error(config.errorImage);
        }
    }

    /**
     * 获取解析的状态
     *
     * @return state
     */
    public int getState() {
        return state;
    }

    @Override
    public Drawable getDrawable(String source) {
        if (config.imageGetter == null) {
            return null;
        }
        if (config.noImage) {
            return null;
        }
        TextView textView = textViewSoftReference.get();
        if (textView == null) {
            return null;
        }
        // 判断activity是否已结束
        if (!activityIsAlive(textView.getContext())) {
            return null;
        }
        ImageHolder holder;
        if (config.richType == RichType.MARKDOWN) {
            holder = new ImageHolder(source, imageHolderMap.size());
        } else {
            holder = imageHolderMap.get(source);
            if (holder == null) {
                holder = new ImageHolder(source, 0);
                imageHolderMap.put(source, holder);
            }
        }
        if (isGif(holder.getSource())) {
            holder.setImageType(ImageHolder.ImageType.GIF);
        } else {
            holder.setImageType(ImageHolder.ImageType.JPG);
        }
        holder.setImageState(ImageHolder.ImageState.INIT);
        if (!config.autoFix && config.imageFixCallback != null) {
            config.imageFixCallback.onFix(holder);
            if (!holder.isShow()) {
                return null;
            }
        }
        return config.imageGetter.getDrawable(holder, config, textView);
    }
}
