package zhou.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zzhoujay on 2015/7/21 0021.
 * 富文本显示TextView
 */
public class RichText extends TextView {

    private static Pattern IMAGE_TAG_PATTERN = Pattern.compile("\\<img(.*?)\\>");
    private static Pattern IMAGE_WIDTH_PATTERN = Pattern.compile("width=\"(.*?)\"");
    private static Pattern IMAGE_HEIGHT_PATTERN = Pattern.compile("height=\"(.*?)\"");
    private static Pattern IMAGE_SRC_PATTERN = Pattern.compile("src=\"(.*?)\"");

    private Drawable placeHolder, errorImage;//占位图，错误图
    private OnImageClickListener onImageClickListener;//图片点击回调
    private OnURLClickListener onURLClickListener;//超链接点击回调
    //    private HashSet<Target> targets;
    private HashSet<ImageTarget> targets;
    private HashMap<String, ImageHolder> mImages;
    private ImageFixListener mImageFixListener;
    private int d_w = 200;
    private int d_h = 200;

    public RichText(Context context) {
        this(context, null);
    }

    public RichText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RichText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        targets = new HashSet<>();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.zhou_RichText);
        placeHolder = typedArray.getDrawable(R.styleable.zhou_RichText_zhou_placeHolder);
        errorImage = typedArray.getDrawable(R.styleable.zhou_RichText_zhou_errorImage);

        d_w = typedArray.getDimensionPixelSize(R.styleable.zhou_RichText_zhou_default_width, d_w);
        d_h = typedArray.getDimensionPixelSize(R.styleable.zhou_RichText_zhou_default_height, d_h);

        if (placeHolder == null) {
            placeHolder = new ColorDrawable(Color.GRAY);
        }
        placeHolder.setBounds(0, 0, d_w, d_h);
        if (errorImage == null) {
            errorImage = new ColorDrawable(Color.GRAY);
        }
        errorImage.setBounds(0, 0, d_w, d_h);
        typedArray.recycle();
    }


    /**
     * 设置富文本
     *
     * @param text 富文本
     */
    public void setRichText(String text) {
        targets.clear();
        matchImages(text);

        Spanned spanned = Html.fromHtml(text, asyncImageGetter, null);
        SpannableStringBuilder spannableStringBuilder;
        if (spanned instanceof SpannableStringBuilder) {
            spannableStringBuilder = (SpannableStringBuilder) spanned;
        } else {
            spannableStringBuilder = new SpannableStringBuilder(spanned);
        }

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
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    if (onImageClickListener != null) {
                        onImageClickListener.imageClicked(imageUrls, finalI);
                    }
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
            spannableStringBuilder.setSpan(new CallableURLSpan(urlSpan.getURL(), onURLClickListener), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        super.setText(spanned);
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void addTarget(ImageTarget target) {
        targets.add(target);
    }


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
            widthMatcher = IMAGE_WIDTH_PATTERN.matcher(image);
            if (widthMatcher.find()) {
                holder.width = parseStringToInteger(getTextBetweenQuotation(widthMatcher.group().trim().substring(6)));
            }

            heightMatcher = IMAGE_HEIGHT_PATTERN.matcher(image);
            if (heightMatcher.find()) {
                holder.height = parseStringToInteger(getTextBetweenQuotation(heightMatcher.group().trim().substring(6)));
            }

            mImages.put(holder.src, holder);
            position++;
        }
    }

    private int parseStringToInteger(String integerStr) {
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

    private class ImageTarget implements Target {
        private final URLDrawable urlDrawable;

        public ImageTarget(URLDrawable urlDrawable) {
            this.urlDrawable = urlDrawable;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Drawable drawable = new BitmapDrawable(getContext().getResources(), bitmap);
            drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            urlDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            urlDrawable.setDrawable(drawable);
            RichText.this.setText(getText());
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            urlDrawable.setBounds(errorDrawable.getBounds());
            urlDrawable.setDrawable(errorDrawable);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            urlDrawable.setBounds(placeHolderDrawable.getBounds());
            urlDrawable.setDrawable(placeHolderDrawable);
        }
    }

    /**
     * 异步加载图片（依赖于Picasso）
     */
    private Html.ImageGetter asyncImageGetter = new Html.ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
            final URLDrawable urlDrawable = new URLDrawable();
            ImageTarget target = new ImageTarget(urlDrawable);
            addTarget(target);
            ImageHolder holder = mImages.get(source);
            RequestCreator load = Picasso.with(getContext())
                    .load(source);
            if (mImageFixListener != null && holder != null) {
                mImageFixListener.onFix(holder);
                if (holder.width != -1 && holder.height != -1) {
                    load.resize(holder.width, holder.height);
                }

                if (holder.scaleType == ImageHolder.CENTER_CROP) {
                    load.centerCrop();
                } else if (holder.scaleType == ImageHolder.CENTER_INSIDE) {
                    load.centerInside();
                }
            }
            load.placeholder(placeHolder)
                    .error(errorImage).into(target);
            return urlDrawable;
        }
    };

    private static final class URLDrawable extends BitmapDrawable {
        private Drawable drawable;

        @SuppressWarnings("deprecation")
        public URLDrawable() {
        }

        @Override
        public void draw(Canvas canvas) {
            if (drawable != null)
                drawable.draw(canvas);
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
        }
    }

    private static class CallableURLSpan extends URLSpan {

        private OnURLClickListener onURLClickListener;

        public CallableURLSpan(String url, OnURLClickListener onURLClickListener) {
            super(url);
            this.onURLClickListener = onURLClickListener;
        }

        @SuppressWarnings("unused")
        public CallableURLSpan(Parcel src, OnURLClickListener onURLClickListener) {
            super(src);
            this.onURLClickListener = onURLClickListener;
        }

        @Override
        public void onClick(View widget) {
            if (onURLClickListener != null && onURLClickListener.urlClicked(getURL())) {
                return;
            }
            super.onClick(widget);
        }
    }

    public static class ImageHolder {
        public static final int DEFAULT = 0;
        public static final int CENTER_CROP = 1;
        public static final int CENTER_INSIDE = 2;

        @IntDef({DEFAULT, CENTER_CROP, CENTER_INSIDE})
        public @interface ScaleType {
        }

        private final String src;
        private final int position;
        private int width = -1, height = -1;
        private int scaleType = DEFAULT;

        public ImageHolder(String src, int position) {
            this.src = src;
            this.position = position;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        @ScaleType
        public int getScaleType() {
            return scaleType;
        }

        public void setScaleType(@ScaleType int scaleType) {
            this.scaleType = scaleType;
        }
    }

    @SuppressWarnings("unused")
    public ImageHolder getImageHolder(String url) {
        return mImages.get(url);
    }

    @SuppressWarnings("unused")
    public void setPlaceHolder(Drawable placeHolder) {
        this.placeHolder = placeHolder;
        this.placeHolder.setBounds(0, 0, d_w, d_h);
    }

    @SuppressWarnings("unused")
    public void setErrorImage(Drawable errorImage) {
        this.errorImage = errorImage;
        this.errorImage.setBounds(0, 0, d_w, d_h);
    }

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }

    public void setImageFixListener(ImageFixListener mImageFixListener) {
        this.mImageFixListener = mImageFixListener;
    }

    /**
     * 设置超链接点击回调事件（需在setRichText方法之前调用）
     *
     * @param onURLClickListener 回调
     */
    public void setOnURLClickListener(OnURLClickListener onURLClickListener) {
        this.onURLClickListener = onURLClickListener;
    }

    public interface OnImageClickListener {
        /**
         * 图片被点击后的回调方法
         *
         * @param imageUrls 本篇富文本内容里的全部图片
         * @param position  点击处图片在imageUrls中的位置
         */
        void imageClicked(List<String> imageUrls, int position);
    }

    public interface OnURLClickListener {

        /**
         * 超链接点击得回调方法
         *
         * @param url 点击得url
         * @return true：已处理，false：未处理（会进行默认处理）
         */
        boolean urlClicked(String url);
    }

    public interface ImageFixListener {
        /**
         * 修复图片尺寸的方法
         *
         * @param holder ImageHolder对象
         */
        void onFix(ImageHolder holder);
    }
}
