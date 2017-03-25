package com.zzhoujay.richtext;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.zzhoujay.richtext.callback.Callback;
import com.zzhoujay.richtext.callback.ImageFixCallback;
import com.zzhoujay.richtext.callback.ImageGetter;
import com.zzhoujay.richtext.callback.LinkFixCallback;
import com.zzhoujay.richtext.callback.OnImageClickListener;
import com.zzhoujay.richtext.callback.OnImageLongClickListener;
import com.zzhoujay.richtext.callback.OnUrlClickListener;
import com.zzhoujay.richtext.callback.OnUrlLongClickListener;
import com.zzhoujay.richtext.ig.DefaultImageGetter;

import java.lang.ref.WeakReference;

/**
 * Created by zhou on 2016/12/3.
 * RichText的各种配置
 */
@SuppressWarnings("WeakerAccess")
public final class RichTextConfig {

    public final String source; // 源文本
    @RichType
    public final int richType; // 富文本类型，默认HTML
    public final boolean autoFix; // 图片自动修复，默认true
    public final boolean resetSize; // 是否放弃使用img标签中的尺寸属性，默认false
    @CacheType
    public final int cacheType; // 缓存类型
    public final ImageFixCallback imageFixCallback; // 自定义图片修复接口只有在autoFix为false时有效
    public final LinkFixCallback linkFixCallback; // 链接修复回调
    public final boolean noImage; // 不显示图片，默认false
    public final int clickable; // 是否可点击，默认0：可点击，使用默认的方式处理点击回调；1：可点击，使用设置的回调接口处理；-1：不可点击
    public final OnImageClickListener onImageClickListener; // 图片点击回调接口
    public final OnUrlClickListener onUrlClickListener; // 链接点击回调接口
    public final OnImageLongClickListener onImageLongClickListener; // 图片长按回调接口
    public final OnUrlLongClickListener onUrlLongClickListener; // 链接长按回调接口
    public final Drawable placeHolder; // placeHolder
    public final Drawable errorImage; // errorImage
    public final Callback callback; // 解析完成的回调
    final ImageGetter imageGetter; // 图片加载器，默认为GlideImageGetter


    private RichTextConfig(RichTextConfigBuild config) {
        this(config.source, config.richType, config.autoFix, config.resetSize, config.cacheType, config.imageFixCallback,
                config.linkFixCallback, config.noImage, config.clickable, config.onImageClickListener,
                config.onUrlClickListener, config.onImageLongClickListener, config.onUrlLongClickListener,
                config.placeHolder, config.errorImage, config.imageGetter, config.callback);
    }

    private RichTextConfig(String source, int richType, boolean autoFix, boolean resetSize, int cacheType, ImageFixCallback imageFixCallback, LinkFixCallback linkFixCallback, boolean noImage, int clickable, OnImageClickListener onImageClickListener, OnUrlClickListener onUrlClickListener, OnImageLongClickListener onImageLongClickListener, OnUrlLongClickListener onUrlLongClickListener, Drawable placeHolder, Drawable errorImage, ImageGetter imageGetter, Callback callback) {
        this.source = source;
        this.richType = richType;
        this.autoFix = autoFix;
        this.resetSize = resetSize;
        this.imageFixCallback = imageFixCallback;
        this.linkFixCallback = linkFixCallback;
        this.noImage = noImage;
        this.cacheType = cacheType;
        this.onImageClickListener = onImageClickListener;
        this.onUrlClickListener = onUrlClickListener;
        this.onImageLongClickListener = onImageLongClickListener;
        this.onUrlLongClickListener = onUrlLongClickListener;
        this.placeHolder = placeHolder;
        this.errorImage = errorImage;
        this.imageGetter = imageGetter;
        this.callback = callback;
        if (clickable == 0) {
            if (onImageLongClickListener != null || onUrlLongClickListener != null ||
                    onImageClickListener != null || onUrlClickListener != null) {
                clickable = 1;
            }
        }
        this.clickable = clickable;
    }

    @SuppressWarnings("unused")
    public static final class RichTextConfigBuild {

        final String source;
        @RichType
        int richType;
        boolean autoFix;
        boolean resetSize;
        @CacheType
        int cacheType;
        ImageFixCallback imageFixCallback;
        LinkFixCallback linkFixCallback;
        boolean noImage;
        int clickable;
        OnImageClickListener onImageClickListener;
        OnUrlClickListener onUrlClickListener;
        OnImageLongClickListener onImageLongClickListener;
        OnUrlLongClickListener onUrlLongClickListener;
        Drawable placeHolder;
        Drawable errorImage;
        @DrawableRes
        int placeHolderRes;
        @DrawableRes
        int errorImageRes;
        ImageGetter imageGetter;
        Callback callback;
        WeakReference<Object> tag;

        RichTextConfigBuild(String source, int richType) {
            this.source = source;
            this.richType = richType;
            this.autoFix = true;
            this.resetSize = false;
            this.noImage = false;
            this.clickable = 0;
            this.cacheType = CacheType.ALL;
            this.imageGetter = new DefaultImageGetter();
        }

        /**
         * 绑定到某个tag上，方便下次取用
         *
         * @param tag TAG
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild bind(Object tag) {
            this.tag = new WeakReference<>(tag);
            return this;
        }

        /**
         * 是否图片宽高自动修复自屏宽，默认true
         *
         * @param autoFix autoFix
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild autoFix(boolean autoFix) {
            this.autoFix = autoFix;
            return this;
        }

        /**
         * 不使用img标签里的宽高，img标签的宽高存在才有用
         *
         * @param resetSize false：使用标签里的宽高，不会触发SIZE_READY的回调；true：忽略标签里的宽高，触发SIZE_READY的回调获取尺寸大小。默认为false
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild resetSize(boolean resetSize) {
            this.resetSize = resetSize;
            return this;
        }

        /**
         * 是否开启缓存
         *
         * @param cacheType 默认为NONE
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild cache(@CacheType int cacheType) {
            this.cacheType = cacheType;
            return this;
        }

        /**
         * 手动修复图片宽高
         *
         * @param callback ImageFixCallback回调
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild fix(ImageFixCallback callback) {
            this.imageFixCallback = callback;
            return this;
        }

        /**
         * 链接修复
         *
         * @param callback LinkFixCallback
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild linkFix(LinkFixCallback callback) {
            this.linkFixCallback = callback;
            return this;
        }

        /**
         * 不显示图片
         *
         * @param noImage 默认false
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild noImage(boolean noImage) {
            this.noImage = noImage;
            return this;
        }

        /**
         * 是否屏蔽点击，不进行此项设置只会在设置了点击回调才会响应点击事件
         *
         * @param clickable clickable，false:屏蔽点击事件，true不屏蔽不设置点击回调也可以响应响应的链接默认回调
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild clickable(boolean clickable) {
            this.clickable = clickable ? 1 : -1;
            return this;
        }

        /**
         * 数据源类型
         *
         * @param richType richType
         * @return RichTextConfigBuild
         * @see RichType
         */
        @SuppressWarnings("WeakerAccess")
        public RichTextConfigBuild type(@RichType int richType) {
            this.richType = richType;
            return this;
        }

        /**
         * 图片点击回调
         *
         * @param imageClickListener 回调
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild imageClick(OnImageClickListener imageClickListener) {
            this.onImageClickListener = imageClickListener;
            return this;
        }

        /**
         * 链接点击回调
         *
         * @param onUrlClickListener 回调
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild urlClick(OnUrlClickListener onUrlClickListener) {
            this.onUrlClickListener = onUrlClickListener;
            return this;
        }

        /**
         * 图片长按回调
         *
         * @param imageLongClickListener 回调
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild imageLongClick(OnImageLongClickListener imageLongClickListener) {
            this.onImageLongClickListener = imageLongClickListener;
            return this;
        }

        /**
         * 链接长按回调
         *
         * @param urlLongClickListener 回调
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild urlLongClick(OnUrlLongClickListener urlLongClickListener) {
            this.onUrlLongClickListener = urlLongClickListener;
            return this;
        }

        /**
         * 图片加载过程中的占位图
         *
         * @param placeHolder 占位图
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild placeHolder(Drawable placeHolder) {
            this.placeHolder = placeHolder;
            return this;
        }

        /**
         * 图片加载失败的占位图
         *
         * @param errorImage 占位图
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild error(Drawable errorImage) {
            this.errorImage = errorImage;
            return this;
        }

        /**
         * 图片加载过程中的占位图
         *
         * @param placeHolder 占位图
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild placeHolder(@DrawableRes int placeHolder) {
            this.placeHolderRes = placeHolder;
            return this;
        }

        /**
         * 图片加载失败的占位图
         *
         * @param errorImage 占位图
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild error(@DrawableRes int errorImage) {
            this.errorImageRes = errorImage;
            return this;
        }

        /**
         * 设置imageGetter
         *
         * @param imageGetter ig
         * @return RichTextConfigBuild
         * @see ImageGetter
         */
        public RichTextConfigBuild imageGetter(ImageGetter imageGetter) {
            this.imageGetter = imageGetter;
            return this;
        }

        /**
         * 解析完成的回调（图片已完成加载）
         *
         * @param callback callback
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild done(Callback callback) {
            this.callback = callback;
            return this;
        }

        /**
         * 加载并设置给textView
         *
         * @param textView TextView
         * @return RichTextConfigBuild
         */
        public RichText into(TextView textView) {
            if (placeHolder == null && placeHolderRes != 0) {
                try {
                    placeHolder = ContextCompat.getDrawable(textView.getContext(), placeHolderRes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (placeHolder == null) {
                placeHolder = new ColorDrawable(Color.LTGRAY);
            }
            if (errorImage == null && errorImageRes != 0) {
                try {
                    errorImage = ContextCompat.getDrawable(textView.getContext(), errorImageRes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (errorImage == null) {
                errorImage = new ColorDrawable(Color.DKGRAY);
            }
            RichText richText = new RichText(new RichTextConfig(this), textView);
            if (tag != null) {
                RichText.bind(tag.get(), richText);
            }
            this.tag = null;
            richText.generateAndSet();
            return richText;
        }
    }
}