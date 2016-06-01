package com.zzhoujay.richtext;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

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
public class RichText {

    private static Pattern IMAGE_TAG_PATTERN = Pattern.compile("\\<img(.*?)\\>");
    private static Pattern IMAGE_WIDTH_PATTERN = Pattern.compile("width=\"(.*?)\"");
    private static Pattern IMAGE_HEIGHT_PATTERN = Pattern.compile("height=\"(.*?)\"");
    private static Pattern IMAGE_SRC_PATTERN = Pattern.compile("src=\"(.*?)\"");

    private Drawable placeHolder, errorImage;//占位图，错误图
    private OnImageClickListener onImageClickListener;//图片点击回调
    private OnURLClickListener onURLClickListener;//超链接点击回调
    //    private HashSet<Target> targets;
    private HashSet<ImageTargetBitmap> targets;
    private HashMap<String, ImageHolder> mImages;
    private ImageFixCallback mImageFixCallback;

    private boolean autoFix;
    private boolean async;
    private String richText;

    private TextView textView;


    private RichText(boolean autoFix, boolean async, String richText, Drawable placeHolder, Drawable errorImage) {
        this.autoFix = autoFix;
        this.async = async;
        this.richText = richText;

        this.placeHolder = placeHolder;
        this.errorImage = errorImage;

        targets = new HashSet<>();

    }

    public RichText() {
        this(true, false, null, new ColorDrawable(Color.LTGRAY), new ColorDrawable(Color.GRAY));
    }

    private void recycle() {
        targets.clear();
    }

    public void into(TextView textView) {
        this.textView = textView;
        textView.setMovementMethod(LinkMovementMethod.getInstance());
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


    private Spanned generateRichText(String text) {
        recycle();
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
        return spanned;
    }

    private class ImageTargetBitmap extends SimpleTarget<Bitmap> {
        private final URLDrawable urlDrawable;
        private final ImageHolder holder;

        public ImageTargetBitmap(URLDrawable urlDrawable, ImageHolder holder) {
            this.urlDrawable = urlDrawable;
            this.holder = holder;
        }

        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            Drawable drawable = new BitmapDrawable(textView.getContext().getResources(), resource);
            if (!autoFix && (holder.getWidth() <= 0 || holder.getHeight() <= 0) && mImageFixCallback != null) {
                holder.setWidth(resource.getWidth());
                holder.setHeight(resource.getHeight());
                mImageFixCallback.onFix(holder, true);
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
        public void onLoadStarted(Drawable placeholder) {
            super.onLoadStarted(placeholder);
            if (placeholder != null) {
                int width = getRealWidth();
                int height = placeholder.getBounds().height();
                if (height == 0) {
                    height = width / 2;
                    placeholder.setBounds(0, 0, width, height);
                }
                urlDrawable.setBounds(0, 0, width, height);
                urlDrawable.setDrawable(placeholder);
            }
        }

        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
            super.onLoadFailed(e, errorDrawable);
            if (errorDrawable != null) {
                int width = getRealWidth();
                int height = errorDrawable.getBounds().height();
                if (height == 0) {
                    height = width / 2;
                    errorDrawable.setBounds(0, 0, width, height);
                }
                urlDrawable.setBounds(0, 0, width, height);
                urlDrawable.setDrawable(errorDrawable);
            }
        }
    }

    private int getRealWidth() {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
        return textView.getWidth() - textView.getPaddingRight() - textView.getPaddingLeft() - layoutParams.leftMargin - layoutParams.rightMargin;
    }


    private final Html.ImageGetter asyncImageGetter = new Html.ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
            final URLDrawable urlDrawable = new URLDrawable();
            ImageHolder holder = mImages.get(source);
            final ImageTargetBitmap target = new ImageTargetBitmap(urlDrawable, holder);
            final BitmapTypeRequest load = Glide.with(textView.getContext()).load(source).asBitmap();

            targets.add(target);
            if (!autoFix && mImageFixCallback != null && holder != null) {
                mImageFixCallback.onFix(holder, false);
                if (holder.getWidth() > 0 && holder.getHeight() > 0) {
                    load.override(holder.getWidth(), holder.getHeight());
                    if (holder.getScaleType() == ImageHolder.CENTER_CROP) {
                        load.centerCrop();
                    } else if (holder.getScaleType() == ImageHolder.FIT_CENTER) {
                        load.fitCenter();
                    }
                }
            }
            textView.post(new Runnable() {
                @Override
                public void run() {
                    load.placeholder(placeHolder).error(errorImage).into(target);
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

    public static RichText from(String richText) {
        RichText r = new RichText();
        r.richText = richText;
        return r;
    }

    public RichText async(boolean async) {
        this.async = async;
        return this;
    }

    public RichText autoFix(boolean autoFix) {
        this.autoFix = autoFix;
        return this;
    }

    public RichText fix(ImageFixCallback callback) {
        this.mImageFixCallback = callback;
        return this;
    }

    public RichText imageClick(OnImageClickListener imageClickListener) {
        this.onImageClickListener = imageClickListener;
        return this;
    }

    public RichText urlClick(OnURLClickListener onURLClickListener) {
        this.onURLClickListener = onURLClickListener;
        return this;
    }

    public RichText placeHolder(Drawable placeHolder) {
        this.placeHolder = placeHolder;
        return this;
    }

    public RichText error(Drawable errorImage) {
        this.errorImage = errorImage;
        return this;
    }

}
