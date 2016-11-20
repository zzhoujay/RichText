package com.zzhoujay.richtext;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.TintContextWrapper;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.widget.TextView;

import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.GifTypeRequest;
import com.bumptech.glide.Glide;
import com.zzhoujay.richtext.cache.RichCacheManager;
import com.zzhoujay.richtext.callback.ImageFixCallback;
import com.zzhoujay.richtext.callback.LinkFixCallback;
import com.zzhoujay.richtext.callback.OnImageClickListener;
import com.zzhoujay.richtext.callback.OnImageLongClickListener;
import com.zzhoujay.richtext.callback.OnURLClickListener;
import com.zzhoujay.richtext.callback.OnUrlLongClickListener;
import com.zzhoujay.richtext.drawable.URLDrawable;
import com.zzhoujay.richtext.ext.Base64;
import com.zzhoujay.richtext.ext.HtmlTagHandler;
import com.zzhoujay.richtext.ext.LongClickableLinkMovementMethod;
import com.zzhoujay.richtext.parser.Html2SpannedParser;
import com.zzhoujay.richtext.parser.Markdown2SpannedParser;
import com.zzhoujay.richtext.parser.SpannedParser;
import com.zzhoujay.richtext.spans.ClickableImageSpan;
import com.zzhoujay.richtext.spans.LongClickableURLSpan;
import com.zzhoujay.richtext.target.ImageLoadNotify;
import com.zzhoujay.richtext.target.ImageTarget;
import com.zzhoujay.richtext.target.ImageTargetBitmap;
import com.zzhoujay.richtext.target.ImageTargetGif;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhou on 16-5-28.
 * 富文本生成器
 */
@SuppressWarnings("unused")
public class RichText implements ImageLoadNotify {

    private static final String TAG_TARGET = "target";

    private static Matcher IMAGE_TAG_MATCHER = Pattern.compile("<(img|IMG)(.*?)>").matcher("");
    private static Matcher IMAGE_WIDTH_MATCHER = Pattern.compile("(width|WIDTH)=\"(.*?)\"").matcher("");
    private static Matcher IMAGE_HEIGHT_MATCHER = Pattern.compile("(height|HEIGHT)=\"(.*?)\"").matcher("");
    private static Matcher IMAGE_SRC_MATCHER = Pattern.compile("(src|SRC)=\"(.*?)\"").matcher("");


    private Drawable placeHolder, errorImage;//占位图，错误图
    @DrawableRes
    private int placeHolderRes = -1, errorImageRes = -1;
    private OnImageClickListener onImageClickListener;//图片点击回调
    private OnImageLongClickListener onImageLongClickListener; // 图片长按回调
    private OnUrlLongClickListener onUrlLongClickListener; // 链接长按回调
    private OnURLClickListener onURLClickListener;//超链接点击回调
    private SoftReference<HashSet<ImageTarget>> targets;
    private HashMap<String, ImageHolder> imageHolderMap;
    private ImageFixCallback imageFixCallback;
    private LinkFixCallback linkFixCallback;

    private int prepareCount;
    private int loadedCount;
    @RichState
    private int state;
    private boolean resetSize;
    private boolean autoFix;
    private boolean noImage;
    private int clickable;
    private final String sourceText;
    private CharSequence richText;
    @RichType
    private int type;
    private SpannedParser spannedParser;

    private WeakReference<TextView> textViewWeakReference;


    private RichText(boolean autoFix, String sourceText, Drawable placeHolder, Drawable errorImage, @RichType int type) {
        this.autoFix = autoFix;
        this.sourceText = sourceText;
        this.placeHolder = placeHolder;
        this.errorImage = errorImage;
        this.type = type;
        this.clickable = 0;
        this.noImage = false;
        this.state = RichState.ready;
        this.resetSize = false;
    }

    private RichText(String sourceText) {
        this(true, sourceText, new ColorDrawable(Color.LTGRAY), new ColorDrawable(Color.GRAY), RichType.HTML);
    }


    /**
     * 给TextView设置富文本
     *
     * @param textView textView
     */
    public void into(final TextView textView) {
        this.textViewWeakReference = new WeakReference<>(textView);
        if (type == RichType.MARKDOWN) {
            spannedParser = new Markdown2SpannedParser(textView);
        } else {
            spannedParser = new Html2SpannedParser(new HtmlTagHandler(textView));
        }
        if (clickable == 0) {
            if (onImageLongClickListener != null || onUrlLongClickListener != null || onImageClickListener != null || onURLClickListener != null) {
                clickable = 1;
            }
        }
        if (clickable > 0) {
            textView.setMovementMethod(new LongClickableLinkMovementMethod());
        } else if (clickable == 0) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        textView.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(generateRichText(sourceText));
            }
        });
    }

    private void recycleTarget(HashSet<ImageTarget> ts) {
        if (ts != null) {
            for (ImageTarget it : ts) {
                if (it != null) {
                    it.recycle();
                }
            }
            ts.clear();
        }
    }

    /**
     * 检查TextView tag复用
     *
     * @param textView textView
     */
    @SuppressWarnings("unchecked")
    private void checkTag(TextView textView) {
        HashSet<ImageTarget> ts = (HashSet<ImageTarget>) textView.getTag(TAG_TARGET.hashCode());
        if (ts != null) {
            recycleTarget(ts);
        }
        if (targets == null || targets.get() == null) {
            targets = new SoftReference<>(new HashSet<ImageTarget>());
        }
        textView.setTag(TAG_TARGET.hashCode(), targets.get());
    }

    /**
     * 生成富文本
     *
     * @param text text
     * @return Spanned
     */
    private CharSequence generateRichText(String text) {
        if (state == RichState.loaded && richText != null) {
            return richText;
        } else {
            CharSequence cs = RichCacheManager.getCache().get(text);
            if (cs != null) {
                return cs;
            }
        }
        state = RichState.loading;
        if (type != RichType.MARKDOWN) {
            analyzeImages(text);
        } else {
            imageHolderMap = new HashMap<>();
        }

        TextView textView = textViewWeakReference.get();
        if (textView == null) {
            return null;
        }
        checkTag(textView);

        Spanned spanned = spannedParser.parse(text, asyncImageGetter);
        SpannableStringBuilder spannableStringBuilder;
        if (spanned instanceof SpannableStringBuilder) {
            spannableStringBuilder = (SpannableStringBuilder) spanned;
        } else {
            spannableStringBuilder = new SpannableStringBuilder(spanned);
        }
        if (clickable > 0) {
            // 处理图片得点击事件
            ImageSpan[] imageSpans = spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), ImageSpan.class);
            final List<String> imageUrls = new ArrayList<>();

            for (int i = 0, size = imageSpans.length; i < size; i++) {
                ImageSpan imageSpan = imageSpans[i];
                String imageUrl = imageSpan.getSource();
                int start = spannableStringBuilder.getSpanStart(imageSpan);
                int end = spannableStringBuilder.getSpanEnd(imageSpan);
                imageUrls.add(imageUrl);

                ClickableImageSpan clickableImageSpan = new ClickableImageSpan(imageSpan, imageUrls, i, onImageClickListener, onImageLongClickListener);
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

            // 处理超链接点击事件
            URLSpan[] urlSpans = spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), URLSpan.class);

            for (int i = 0, size = urlSpans == null ? 0 : urlSpans.length; i < size; i++) {
                URLSpan urlSpan = urlSpans[i];

                int start = spannableStringBuilder.getSpanStart(urlSpan);
                int end = spannableStringBuilder.getSpanEnd(urlSpan);

                spannableStringBuilder.removeSpan(urlSpan);
                LinkHolder linkHolder = new LinkHolder(urlSpan.getURL());
                if (linkFixCallback != null) {
                    linkFixCallback.fix(linkHolder);
                }
                spannableStringBuilder.setSpan(new LongClickableURLSpan(urlSpan.getURL(), onURLClickListener, onUrlLongClickListener, linkHolder), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableStringBuilder;
    }

    // 图片异步加载器
    private final Html.ImageGetter asyncImageGetter = new Html.ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
            if (noImage) {
                return new ColorDrawable(Color.TRANSPARENT);
            }
            TextView textView = textViewWeakReference.get();
            if (textView == null) {
                return null;
            }
            // 判断activity是否已结束
            if (!activityIsAlive(textView.getContext())) {
                return null;
            }
            final URLDrawable urlDrawable = new URLDrawable();
            ImageHolder imageHolder;
            if (type == RichType.MARKDOWN) {
                imageHolder = new ImageHolder(source, imageHolderMap.size());
            } else {
                imageHolder = imageHolderMap.get(source);
                if (imageHolder == null) {
                    imageHolder = new ImageHolder(source, 0);
                    imageHolderMap.put(source, imageHolder);
                }
            }
            final ImageHolder holder = imageHolder;
            final ImageTarget target;
            final GenericRequestBuilder load;
            if (isGif(holder.getSrc())) {
                holder.setImageType(ImageHolder.ImageType.GIF);
            } else {
                holder.setImageType(ImageHolder.ImageType.JPG);
            }
            holder.setImageState(ImageHolder.ImageState.INIT);
            if (!autoFix && imageFixCallback != null) {
                imageFixCallback.onFix(holder);
                if (!holder.isShow()) {
                    return new ColorDrawable(Color.TRANSPARENT);
                }
            }
            DrawableTypeRequest dtr;
            byte[] src = Base64.decode(source);
            if (src != null) {
                dtr = Glide.with(textView.getContext()).load(src);
            } else {
                dtr = Glide.with(textView.getContext()).load(source);
            }
            if (holder.isGif()) {
                target = new ImageTargetGif(textView, urlDrawable, holder, autoFix, imageFixCallback, RichText.this);
                load = dtr.asGif();
            } else {
                target = new ImageTargetBitmap(textView, urlDrawable, holder, autoFix, imageFixCallback, RichText.this);
                load = dtr.asBitmap().atMost();
            }
            if (targets.get() != null) {
                targets.get().add(target);
            }
            if (!resetSize && holder.getWidth() > 0 && holder.getHeight() > 0) {
                load.override(holder.getWidth(), holder.getHeight());
            }
            if (holder.getScaleType() == ImageHolder.ScaleType.CENTER_CROP) {
                if (holder.isGif()) {
                    //noinspection ConstantConditions
                    ((GifTypeRequest) load).centerCrop();
                } else {
                    //noinspection ConstantConditions
                    ((BitmapTypeRequest) load).centerCrop();
                }
            } else if (holder.getScaleType() == ImageHolder.ScaleType.FIT_CENTER) {
                if (holder.isGif()) {
                    //noinspection ConstantConditions
                    ((GifTypeRequest) load).fitCenter();
                } else {
                    //noinspection ConstantConditions
                    ((BitmapTypeRequest) load).fitCenter();
                }
            }
            textView.post(new Runnable() {
                @Override
                public void run() {
                    setPlaceHolder(load);
                    setErrorImage(load);
                    load.into(target);
                }
            });
            prepareCount++;
            return urlDrawable;
        }
    };

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
            imageHolderMap.put(holder.getSrc(), holder);
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
     * @param richText 待解析文本
     * @return RichText
     * @see #fromHtml(String)
     */
    public static RichText from(String richText) {
        return fromHtml(richText);
    }

    /**
     * 构建RichText并设置数据源为Html
     *
     * @param richText 待解析文本
     * @return RichText
     */
    @SuppressWarnings("WeakerAccess")
    public static RichText fromHtml(String richText) {
        RichText r = new RichText(richText);
        r.type = RichType.HTML;
        return r;
    }

    /**
     * 构建RichText并设置数据源为Markdown
     *
     * @param markdown markdown源文本
     * @return RichText
     */
    public static RichText fromMarkdown(String markdown) {
        return from(markdown).type(RichType.MARKDOWN);
    }

    /**
     * 回收所有图片和任务
     */
    public void clear() {
        if (targets != null)
            recycleTarget(targets.get());
        TextView textView = textViewWeakReference.get();
        if (textView != null) {
            textView.setText(null);
        }
        RichCacheManager.getCache().clear(sourceText);
    }

    /**
     * 是否图片宽高自动修复自屏宽，默认true
     *
     * @param autoFix autoFix
     * @return RichText
     */
    public RichText autoFix(boolean autoFix) {
        this.autoFix = autoFix;
        return this;
    }

    /**
     * 不使用img标签里的宽高，img标签的宽高存在才有用
     *
     * @param resetSize false：使用标签里的宽高，不会触发SIZE_READY的回调；true：忽略标签里的宽高，触发SIZE_READY的回调获取尺寸大小。默认为false
     * @return RichText
     */
    public RichText resetSize(boolean resetSize) {
        this.resetSize = resetSize;
        return this;
    }

    /**
     * 手动修复图片宽高
     *
     * @param callback ImageFixCallback回调
     * @return RichText
     */
    public RichText fix(ImageFixCallback callback) {
        this.imageFixCallback = callback;
        return this;
    }

    /**
     * 链接修复
     *
     * @param callback LinkFixCallback
     * @return RichText
     */
    public RichText linkFix(LinkFixCallback callback) {
        this.linkFixCallback = callback;
        return this;
    }

    /**
     * 不显示图片
     *
     * @param noImage 默认false
     * @return RichText
     */
    public RichText noImage(boolean noImage) {
        this.noImage = noImage;
        return this;
    }

    /**
     * 是否屏蔽点击，不进行此项设置只会在设置了点击回调才会响应点击事件
     *
     * @param clickable clickable，false:屏蔽点击事件，true不屏蔽不设置点击回调也可以响应响应的链接默认回调
     * @return RichText
     */
    public RichText clickable(boolean clickable) {
        this.clickable = clickable ? 1 : -1;
        return this;
    }

    /**
     * 数据源类型
     *
     * @param type type
     * @return RichText
     * @see RichType
     */
    @SuppressWarnings("WeakerAccess")
    public RichText type(@RichType int type) {
        this.type = type;
        return this;
    }

    /**
     * 图片点击回调
     *
     * @param imageClickListener 回调
     * @return RichText
     */
    public RichText imageClick(OnImageClickListener imageClickListener) {
        this.onImageClickListener = imageClickListener;
        return this;
    }

    /**
     * 链接点击回调
     *
     * @param onURLClickListener 回调
     * @return RichText
     */
    public RichText urlClick(OnURLClickListener onURLClickListener) {
        this.onURLClickListener = onURLClickListener;
        return this;
    }

    /**
     * 图片长按回调
     *
     * @param imageLongClickListener 回调
     * @return RichText
     */
    public RichText imageLongClick(OnImageLongClickListener imageLongClickListener) {
        this.onImageLongClickListener = imageLongClickListener;
        return this;
    }

    /**
     * 链接长按回调
     *
     * @param urlLongClickListener 回调
     * @return RichText
     */
    public RichText urlLongClick(OnUrlLongClickListener urlLongClickListener) {
        this.onUrlLongClickListener = urlLongClickListener;
        return this;
    }

    /**
     * 图片加载过程中的占位图
     *
     * @param placeHolder 占位图
     * @return RichText
     */
    public RichText placeHolder(Drawable placeHolder) {
        this.placeHolder = placeHolder;
        return this;
    }

    /**
     * 图片加载失败的占位图
     *
     * @param errorImage 占位图
     * @return RichText
     */
    public RichText error(Drawable errorImage) {
        this.errorImage = errorImage;
        return this;
    }

    /**
     * 图片加载过程中的占位图
     *
     * @param placeHolder 占位图
     * @return RichText
     */
    public RichText placeHolder(@DrawableRes int placeHolder) {
        this.placeHolderRes = placeHolder;
        return this;
    }

    /**
     * 图片加载失败的占位图
     *
     * @param errorImage 占位图
     * @return RichText
     */
    public RichText error(@DrawableRes int errorImage) {
        this.errorImageRes = errorImage;
        return this;
    }

    private void setPlaceHolder(GenericRequestBuilder load) {
        if (placeHolderRes > 0) {
            load.placeholder(placeHolderRes);
        } else {
            load.placeholder(placeHolder);
        }
    }

    private void setErrorImage(GenericRequestBuilder load) {
        if (errorImageRes > 0) {
            load.error(errorImageRes);
        } else {
            load.error(errorImage);
        }
    }

    @Override
    public void done(CharSequence value) {
        loadedCount++;
        if (loadedCount >= prepareCount) {
            if (value != null) {
                richText = value;
            } else {
                TextView textView = textViewWeakReference.get();
                if (textView == null) {
                    return;
                }
                richText = textView.getText();
            }
            state = RichState.loaded;
            RichCacheManager.getCache().put(sourceText, richText);
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
}
