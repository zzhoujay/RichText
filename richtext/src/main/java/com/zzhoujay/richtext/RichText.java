package com.zzhoujay.richtext;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.GifTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zzhoujay.richtext.callback.ImageFixCallback;
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
import com.zzhoujay.richtext.spans.LongCallableURLSpan;
import com.zzhoujay.richtext.spans.LongClickableSpan;

import java.lang.ref.SoftReference;
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
@SuppressLint("NewApi")
public class RichText {

    private static final String TAG_TARGET = "target";

    private static Pattern IMAGE_TAG_PATTERN = Pattern.compile("<img(.*?)>");
    private static Pattern IMAGE_WIDTH_PATTERN = Pattern.compile("width=\"(.*?)\"");
    private static Pattern IMAGE_HEIGHT_PATTERN = Pattern.compile("height=\"(.*?)\"");
    private static Pattern IMAGE_SRC_PATTERN = Pattern.compile("src=\"(.*?)\"");

    public static final int TYPE_HTML = 0;
    public static final int TYPE_MARKDOWN = 1;

    private Drawable placeHolder, errorImage;//占位图，错误图
    @DrawableRes
    private int placeHolderRes = -1, errorImageRes = -1;
    private OnImageClickListener onImageClickListener;//图片点击回调
    private OnImageLongClickListener onImageLongClickListener; // 图片长按回调
    private OnUrlLongClickListener onUrlLongClickListener; // 链接长按回调
    private OnURLClickListener onURLClickListener;//超链接点击回调
    private SoftReference<HashSet<ImageTarget>> targets;
    private HashMap<String, ImageHolder> mImages;
    private ImageFixCallback mImageFixCallback;
    private HashSet<GifDrawable> gifDrawables;

    private boolean autoFix;
    private boolean async;
    private boolean noImage;
    private int clickable;
    String richText;
    @RichType
    private int type;
    private SpannedParser spannedParser;

    private TextView textView;


    private RichText(boolean autoFix, boolean async, String richText, Drawable placeHolder, Drawable errorImage, @RichType int type) {
        this.autoFix = autoFix;
        this.async = async;
        this.richText = richText;

        this.placeHolder = placeHolder;
        this.errorImage = errorImage;
        this.type = type;


        gifDrawables = new HashSet<>();

        noImage = false;
        clickable = 0;

    }

    private RichText() {
        this(true, false, null, new ColorDrawable(Color.LTGRAY), new ColorDrawable(Color.GRAY), TYPE_HTML);
    }

    /**
     * 回收所有图片
     */
    public void recycle() {
        if (gifDrawables == null || gifDrawables.isEmpty()) {
            return;
        }
        for (GifDrawable gifDrawable : gifDrawables) {
            gifDrawable.setCallback(null);
            gifDrawable.recycle();
        }
        gifDrawables.clear();
    }

    /**
     * 给TextView设置富文本
     *
     * @param textView textView
     */
    public void into(TextView textView) {
        this.textView = textView;
        if (type == TYPE_MARKDOWN) {
            spannedParser = new Markdown2SpannedParser(textView);
        } else {
            spannedParser = new Html2SpannedParser(new HtmlTagHandler(textView));
        }
        if (clickable == 0) {
            if (onImageLongClickListener != null || onImageClickListener != null || onUrlLongClickListener != null || onURLClickListener != null) {
                clickable = 1;
            }
        }
        if (clickable > 0) {
            textView.setMovementMethod(new LongClickableLinkMovementMethod());
        }
        textView.post(new Runnable() {
            @Override
            public void run() {
                if (async) {
                    setRichTextInTextViewAsync();
                } else {
                    RichText.this.textView.setText(generateRichText(richText));
                }
            }
        });
    }

    @Deprecated
    private void setRichTextInTextViewAsync() {
        new AsyncTask<String, Void, Spanned>() {
            @Override
            protected Spanned doInBackground(String... params) {
                return generateRichText(params[0]);
            }

            @Override
            protected void onPostExecute(Spanned spanned) {
                super.onPostExecute(spanned);
                textView.setText(spanned);
            }
        }.execute(richText);
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

    private void checkTag(TextView textView) {
        HashSet<ImageTarget> ts = (HashSet<ImageTarget>) textView.getTag(TAG_TARGET.hashCode());
        if (ts != null) {
            recycleTarget(ts);
        }
        if (targets == null || targets.get() == null) {
            targets = new SoftReference<HashSet<ImageTarget>>(new HashSet<ImageTarget>());
        }
        textView.setTag(TAG_TARGET.hashCode(), targets.get());
    }

    private Spanned generateRichText(String text) {
        recycle();
        if (type != TYPE_MARKDOWN) {
            matchImages(text);
        } else {
            mImages = new HashMap<>();
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

                final int finalI = i;
                ClickableSpan clickableSpan = new LongClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        if (onImageClickListener != null) {
                            onImageClickListener.imageClicked(imageUrls, finalI);
                        }
                    }

                    @Override
                    public boolean onLongClick(View widget) {
                        return onImageLongClickListener != null && onImageLongClickListener.imageLongClicked(imageUrls, finalI);
                    }
                };

                ClickableSpan[] clickableSpans = spannableStringBuilder.getSpans(start, end, ClickableSpan.class);
                if (clickableSpans != null && clickableSpans.length != 0) {
                    for (ClickableSpan cs : clickableSpans) {
                        spannableStringBuilder.removeSpan(cs);
                    }
                }
                spannableStringBuilder.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            // 处理超链接点击事件
            URLSpan[] urlSpans = spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), URLSpan.class);

            for (int i = 0, size = urlSpans == null ? 0 : urlSpans.length; i < size; i++) {
                URLSpan urlSpan = urlSpans[i];

                int start = spannableStringBuilder.getSpanStart(urlSpan);
                int end = spannableStringBuilder.getSpanEnd(urlSpan);

                spannableStringBuilder.removeSpan(urlSpan);
                spannableStringBuilder.setSpan(new LongCallableURLSpan(urlSpan.getURL(), onURLClickListener, onUrlLongClickListener), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spanned;
    }


    private abstract class ImageTarget<Z> extends SimpleTarget<Z> implements View.OnAttachStateChangeListener {

        boolean recycled = false;

        final TextView textView;
        final URLDrawable urlDrawable;
        final ImageHolder holder;

        ImageTarget(TextView textView, URLDrawable urlDrawable, ImageHolder holder) {
            this.textView = textView;
            this.urlDrawable = urlDrawable;
            this.holder = holder;
            textView.addOnAttachStateChangeListener(this);
        }

        @Override
        public void onLoadStarted(Drawable placeholder) {
            super.onLoadStarted(placeholder);
            int width;
            int height;
            if (holder != null && holder.getHeight() > 0 && holder.getWidth() > 0) {
                width = holder.getWidth();
                height = holder.getHeight();
            } else {
                width = getRealWidth();
                height = placeholder.getBounds().height();
                if (height == 0) {
                    height = width / 2;
                }
            }
            placeholder.setBounds(0, 0, width, height);
            urlDrawable.setBounds(0, 0, width, height);
            urlDrawable.setDrawable(placeholder);
            textView.setText(textView.getText());
        }

        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
            super.onLoadFailed(e, errorDrawable);
            int width;
            int height;
            if (holder != null && holder.getHeight() > 0 && holder.getWidth() > 0) {
                checkWidth(holder);
                width = holder.getWidth();
                height = holder.getHeight();
            } else {
                width = getRealWidth();
                height = errorDrawable.getBounds().height();
                if (height == 0) {
                    height = width / 2;
                }
            }
            errorDrawable.setBounds(0, 0, width, height);
            urlDrawable.setBounds(0, 0, width, height);
            urlDrawable.setDrawable(errorDrawable);
            textView.setText(textView.getText());
        }

        public abstract void recycle();

        @Override
        public void onViewAttachedToWindow(View v) {

        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            recycleTarget(targets.get());
            RichText.this.recycle();
            textView.removeOnAttachStateChangeListener(this);
        }
    }

    private class ImageTargetGif extends ImageTarget<GifDrawable> implements Drawable.Callback, View.OnAttachStateChangeListener {

        private SoftReference<GifDrawable> gifDrawableSoftReference;


        private ImageTargetGif(TextView textView, URLDrawable urlDrawable, ImageHolder holder) {
            super(textView, urlDrawable, holder);
        }

        @Override
        public void onResourceReady(GifDrawable resource, GlideAnimation<? super GifDrawable> glideAnimation) {
//            Log.i("RichText","ready gif "+System.identityHashCode(resource)+" url:"+holder.getSrc());
            gifDrawableSoftReference = new SoftReference<GifDrawable>(resource);
            Bitmap first = resource.getFirstFrame();
            if (!autoFix && (holder.getWidth() <= 0 || holder.getHeight() <= 0) && mImageFixCallback != null) {
                holder.setWidth(first.getWidth());
                holder.setHeight(first.getHeight());
                mImageFixCallback.onFix(holder, true);
            }
            if (autoFix || holder.isAutoFix()) {
                int width = getRealWidth();
                int height = (int) ((float) first.getHeight() * width / first.getWidth());
                urlDrawable.setBounds(0, 0, width, height);
                resource.setBounds(0, 0, width, height);
            } else {
                resource.setBounds(0, 0, holder.getWidth(), holder.getHeight());
                urlDrawable.setBounds(0, 0, holder.getWidth(), holder.getHeight());
            }
            urlDrawable.setDrawable(resource);
            gifDrawables.add(resource);
            if (holder.isAutoPlay()) {
                resource.setCallback(this);
                resource.start();
                resource.setLoopCount(GlideDrawable.LOOP_FOREVER);
//                if (holder.isAutoStop() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
//                    textView.addOnAttachStateChangeListener(this);
//                }
            }
            textView.setText(textView.getText());
        }

        @Override
        public void recycle() {
            if (recycled) {
                return;
            }
            Glide.clear(this);
//            Log.i("RichText", "recycle " + holder.getSrc());
            GifDrawable gifDrawable = gifDrawableSoftReference.get();
            if (gifDrawable != null) {
                if (gifDrawables != null) {
                    gifDrawables.remove(gifDrawable);
                }
                gifDrawable.setCallback(null);
                gifDrawable.stop();
                gifDrawable.recycle();
            }
            urlDrawable.recycle();
            textView.removeOnAttachStateChangeListener(this);
            recycled = true;
        }

        @Override
        public void invalidateDrawable(Drawable who) {
            if (textView != null) {
                textView.invalidate();
            } else {
                recycle();
            }
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {

        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {

        }

//        @Override
//        public void onViewAttachedToWindow(View v) {
//        }
//
//        @Override
//        public void onViewDetachedFromWindow(View v) {
////            Log.i("RichText", "onViewDetachedFromWindow " + holder.getSrc());
//            recycleTarget(targets.get());
//            RichText.this.recycle();
//        }
    }

    private class ImageTargetBitmap extends ImageTarget<Bitmap> {

        private SoftReference<Bitmap> bitmapSoftReference;

        ImageTargetBitmap(TextView textView, URLDrawable urlDrawable, ImageHolder holder) {
            super(textView, urlDrawable, holder);
        }

        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//            Log.i("RichText","ready "+System.identityHashCode(resource)+" url:"+holder.getSrc());
            bitmapSoftReference = new SoftReference<Bitmap>(resource);
            Drawable drawable = new BitmapDrawable(textView.getContext().getResources(), resource);
            if (!autoFix && (holder.getWidth() <= 0 || holder.getHeight() <= 0)) {
                holder.setWidth(resource.getWidth());
                holder.setHeight(resource.getHeight());
                if (mImageFixCallback != null) {
                    mImageFixCallback.onFix(holder, true);
                } else {
                    checkWidth(holder);
                }
            }
            if (autoFix || holder.isAutoFix()) {
                int width = getRealWidth();
                int height = (int) ((float) resource.getHeight() * width / resource.getWidth());
                urlDrawable.setBounds(0, 0, width, height);
                drawable.setBounds(0, 0, width, height);
            } else {
                drawable.setBounds(0, 0, holder.getWidth(), holder.getHeight());
                urlDrawable.setBounds(0, 0, holder.getWidth(), holder.getHeight());
            }
            urlDrawable.setDrawable(drawable);
            textView.setText(textView.getText());
        }


        @Override
        public void recycle() {
            Glide.clear(this);
//            if (recycled) {
//                return;
//            }
//            if (bitmapSoftReference.get() != null) {
//                bitmapSoftReference.get().recycle();
//            }
//            Log.i("RichText", "bitmap recycle " + System.identityHashCode(bitmapSoftReference.get()));
//            urlDrawable.recycle();
//            recycled = true;
        }
    }

    /**
     * 检查图片大小是否超过屏幕
     *
     * @param holder ImageHolder
     */
    private void checkWidth(ImageHolder holder) {
        int w = getRealWidth();
        if (holder.getWidth() > w) {
            float r = (float) w / holder.getWidth();
            holder.setHeight((int) (r * holder.getHeight()));
        }
    }

    /**
     * 获取可用宽度
     *
     * @return width
     */
    private int getRealWidth() {
        return textView.getWidth() - textView.getPaddingRight() - textView.getPaddingLeft();
    }


    private final Html.ImageGetter asyncImageGetter = new Html.ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
            if (noImage) {
                return new ColorDrawable(Color.TRANSPARENT);
            }
            final URLDrawable urlDrawable = new URLDrawable();
            ImageHolder imageHolder;
            if (type == TYPE_MARKDOWN) {
                imageHolder = new ImageHolder(source, mImages.size());
            } else {
                imageHolder = mImages.get(source);
            }
            final ImageHolder holder = imageHolder;
            final ImageTarget target;
            final GenericRequestBuilder load;
            if (!autoFix && mImageFixCallback != null && holder != null) {
                mImageFixCallback.onFix(holder, false);
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
            if (holder != null && holder.isGif()) {
                target = new ImageTargetGif(textView, urlDrawable, holder);
                load = dtr.asGif();
            } else {
                target = new ImageTargetBitmap(textView, urlDrawable, holder);
                load = dtr.asBitmap();
            }
            if (targets.get() != null) {
                targets.get().add(target);
            }
//            targets.add(target);
            if (!autoFix && mImageFixCallback != null && holder != null) {
                if (holder.getWidth() > 0 && holder.getHeight() > 0) {
                    load.override(holder.getWidth(), holder.getHeight());
                    if (holder.getScaleType() == ImageHolder.CENTER_CROP) {
                        if (holder.isGif()) {
                            ((GifTypeRequest) load).centerCrop();
                        } else {
                            ((BitmapTypeRequest) load).centerCrop();
                        }
                    } else if (holder.getScaleType() == ImageHolder.FIT_CENTER) {
                        if (holder.isGif()) {
                            ((GifTypeRequest) load).fitCenter();
                        } else {
                            ((BitmapTypeRequest) load).fitCenter();
                        }
                    }
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
            return urlDrawable;
        }
    };

    /**
     * 从文本中拿到<img/>标签,并获取图片url和宽高
     */
    private void matchImages(String text) {
        mImages = new HashMap<>();
        ImageHolder holder;
        Matcher imageMatcher, srcMatcher, widthMatcher, heightMatcher;
        int position = 0;
        imageMatcher = IMAGE_TAG_PATTERN.matcher(text);
        while (imageMatcher.find()) {
            String image = imageMatcher.group().trim();
            srcMatcher = IMAGE_SRC_PATTERN.matcher(image);
            String src = null;
            if (srcMatcher.find()) {
                src = getTextBetweenQuotation(srcMatcher.group().trim().substring(4));
            }
            if (TextUtils.isEmpty(src)) {
                continue;
            }
            holder = new ImageHolder(src, position);
            if (isGif(src)) {
                holder.setImageType(ImageHolder.GIF);
            }
            widthMatcher = IMAGE_WIDTH_PATTERN.matcher(image);
            if (widthMatcher.find()) {
                holder.setWidth(parseStringToInteger(getTextBetweenQuotation(widthMatcher.group().trim().substring(6))));
            }

            heightMatcher = IMAGE_HEIGHT_PATTERN.matcher(image);
            if (heightMatcher.find()) {
                holder.setHeight(parseStringToInteger(getTextBetweenQuotation(heightMatcher.group().trim().substring(6))));
            }

            mImages.put(holder.getSrc(), holder);
            position++;
        }
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

    /**
     * 从双引号之间取出字符串
     */
    @Nullable
    private static String getTextBetweenQuotation(String text) {
        Pattern pattern = Pattern.compile("\"(.*?)\"");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static boolean isGif(String path) {
        int index = path.lastIndexOf('.');
        return index > 0 && "gif".toUpperCase().equals(path.substring(index + 1).toUpperCase());
    }

    /**
     * 手动取消所有任务
     */
    public void cancel() {
        recycleTarget(targets.get());
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
    public static RichText fromHtml(String richText) {
        RichText r = new RichText();
        r.type = RichText.TYPE_HTML;
        r.richText = richText;
        return r;
    }

    /**
     * 构建RichText并设置数据源为Markdown
     *
     * @param markdown markdown源文本
     * @return RichText
     */
    public static RichText fromMarkdown(String markdown) {
        return from(markdown).type(TYPE_MARKDOWN);
    }

    /**
     * 是否异步进行，默认false
     *
     * @param async 是否异步解析
     * @return RichText
     * @deprecated 建议异步自行处理
     */
    @Deprecated
    public RichText async(boolean async) {
        this.async = async;
        return this;
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
     * 手动修复图片宽高
     *
     * @param callback ImageFixCallback回调
     * @return RichText
     */
    public RichText fix(ImageFixCallback callback) {
        this.mImageFixCallback = callback;
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

}
