package com.zzhoujay.richtext;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.widget.TextView;

import com.zzhoujay.richtext.callback.ImageFixCallback;
import com.zzhoujay.richtext.callback.ImageGetter;
import com.zzhoujay.richtext.callback.LinkFixCallback;
import com.zzhoujay.richtext.callback.OnImageClickListener;
import com.zzhoujay.richtext.callback.OnImageLongClickListener;
import com.zzhoujay.richtext.callback.OnURLClickListener;
import com.zzhoujay.richtext.callback.OnUrlLongClickListener;

/**
 * Created by zhou on 2016/12/3.
 */

public final class RichTextConfig {

    final String source;
    @RichType
    final int richType;
    final boolean autoFix;
    final boolean resetSize;
    final ImageFixCallback imageFixCallback;
    final LinkFixCallback linkFixCallback;
    final boolean noImage;
    final int clickable;
    final OnImageClickListener onImageClickListener;
    final OnURLClickListener onURLClickListener;
    final OnImageLongClickListener onImageLongClickListener;
    final OnUrlLongClickListener onUrlLongClickListener;
    final Drawable placeHolder;
    final Drawable errorImage;
    @DrawableRes
    final int placeHolderRes;
    @DrawableRes
    final int errorImageRes;
    final ImageGetter imageGetter;


    public RichTextConfig(RichTextConfigBuild config) {
        this(config.source, config.richType, config.autoFix, config.resetSize, config.imageFixCallback,
                config.linkFixCallback, config.noImage, config.clickable, config.onImageClickListener,
                config.onURLClickListener, config.onImageLongClickListener, config.onUrlLongClickListener,
                config.placeHolder, config.errorImage, config.placeHolderRes, config.errorImageRes,
                config.imageGetter);
    }

    public RichTextConfig(String source, int richType, boolean autoFix, boolean resetSize, ImageFixCallback imageFixCallback, LinkFixCallback linkFixCallback, boolean noImage, int clickable, OnImageClickListener onImageClickListener, OnURLClickListener onURLClickListener, OnImageLongClickListener onImageLongClickListener, OnUrlLongClickListener onUrlLongClickListener, Drawable placeHolder, Drawable errorImage, int placeHolderRes, int errorImageRes, ImageGetter imageGetter) {
        this.source = source;
        this.richType = richType;
        this.autoFix = autoFix;
        this.resetSize = resetSize;
        this.imageFixCallback = imageFixCallback;
        this.linkFixCallback = linkFixCallback;
        this.noImage = noImage;
        this.onImageClickListener = onImageClickListener;
        this.onURLClickListener = onURLClickListener;
        this.onImageLongClickListener = onImageLongClickListener;
        this.onUrlLongClickListener = onUrlLongClickListener;
        this.placeHolder = placeHolder;
        this.errorImage = errorImage;
        this.placeHolderRes = placeHolderRes;
        this.errorImageRes = errorImageRes;
        this.imageGetter = imageGetter;
        if (clickable == 0) {
            if (onImageLongClickListener != null || onUrlLongClickListener != null ||
                    onImageClickListener != null || onURLClickListener != null) {
                clickable = 1;
            }
        }
        this.clickable = clickable;
    }

    public static final class RichTextConfigBuild {

        final String source;
        @RichType
        int richType;
        boolean autoFix;
        boolean resetSize;
        ImageFixCallback imageFixCallback;
        LinkFixCallback linkFixCallback;
        boolean noImage;
        int clickable;
        OnImageClickListener onImageClickListener;
        OnURLClickListener onURLClickListener;
        OnImageLongClickListener onImageLongClickListener;
        OnUrlLongClickListener onUrlLongClickListener;
        Drawable placeHolder;
        Drawable errorImage;
        @DrawableRes
        int placeHolderRes;
        @DrawableRes
        int errorImageRes;
        ImageGetter imageGetter;

        RichTextConfigBuild(String source, int richType) {
            this.source = source;
            this.richType = richType;
            this.autoFix = true;
            this.resetSize = false;
            this.noImage = false;
            this.clickable = 0;
            this.imageGetter = new GlideImageGetter();
        }

        /**
         * 是否图片宽高自动修复自屏宽，默认true
         *
         * @param autoFix autoFix
         * @return RichText
         */
        public RichTextConfigBuild autoFix(boolean autoFix) {
            this.autoFix = autoFix;
            return this;
        }

        /**
         * 不使用img标签里的宽高，img标签的宽高存在才有用
         *
         * @param resetSize false：使用标签里的宽高，不会触发SIZE_READY的回调；true：忽略标签里的宽高，触发SIZE_READY的回调获取尺寸大小。默认为false
         * @return RichText
         */
        public RichTextConfigBuild resetSize(boolean resetSize) {
            this.resetSize = resetSize;
            return this;
        }

        /**
         * 手动修复图片宽高
         *
         * @param callback ImageFixCallback回调
         * @return RichText
         */
        public RichTextConfigBuild fix(ImageFixCallback callback) {
            this.imageFixCallback = callback;
            return this;
        }

        /**
         * 链接修复
         *
         * @param callback LinkFixCallback
         * @return RichText
         */
        public RichTextConfigBuild linkFix(LinkFixCallback callback) {
            this.linkFixCallback = callback;
            return this;
        }

        /**
         * 不显示图片
         *
         * @param noImage 默认false
         * @return RichText
         */
        public RichTextConfigBuild noImage(boolean noImage) {
            this.noImage = noImage;
            return this;
        }

        /**
         * 是否屏蔽点击，不进行此项设置只会在设置了点击回调才会响应点击事件
         *
         * @param clickable clickable，false:屏蔽点击事件，true不屏蔽不设置点击回调也可以响应响应的链接默认回调
         * @return RichText
         */
        public RichTextConfigBuild clickable(boolean clickable) {
            this.clickable = clickable ? 1 : -1;
            return this;
        }

        /**
         * 数据源类型
         *
         * @param richType richType
         * @return RichText
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
         * @return RichText
         */
        public RichTextConfigBuild imageClick(OnImageClickListener imageClickListener) {
            this.onImageClickListener = imageClickListener;
            return this;
        }

        /**
         * 链接点击回调
         *
         * @param onURLClickListener 回调
         * @return RichText
         */
        public RichTextConfigBuild urlClick(OnURLClickListener onURLClickListener) {
            this.onURLClickListener = onURLClickListener;
            return this;
        }

        /**
         * 图片长按回调
         *
         * @param imageLongClickListener 回调
         * @return RichText
         */
        public RichTextConfigBuild imageLongClick(OnImageLongClickListener imageLongClickListener) {
            this.onImageLongClickListener = imageLongClickListener;
            return this;
        }

        /**
         * 链接长按回调
         *
         * @param urlLongClickListener 回调
         * @return RichText
         */
        public RichTextConfigBuild urlLongClick(OnUrlLongClickListener urlLongClickListener) {
            this.onUrlLongClickListener = urlLongClickListener;
            return this;
        }

        /**
         * 图片加载过程中的占位图
         *
         * @param placeHolder 占位图
         * @return RichText
         */
        public RichTextConfigBuild placeHolder(Drawable placeHolder) {
            this.placeHolder = placeHolder;
            return this;
        }

        /**
         * 图片加载失败的占位图
         *
         * @param errorImage 占位图
         * @return RichText
         */
        public RichTextConfigBuild error(Drawable errorImage) {
            this.errorImage = errorImage;
            return this;
        }

        /**
         * 图片加载过程中的占位图
         *
         * @param placeHolder 占位图
         * @return RichText
         */
        public RichTextConfigBuild placeHolder(@DrawableRes int placeHolder) {
            this.placeHolderRes = placeHolder;
            return this;
        }

        /**
         * 图片加载失败的占位图
         *
         * @param errorImage 占位图
         * @return RichText
         */
        public RichTextConfigBuild error(@DrawableRes int errorImage) {
            this.errorImageRes = errorImage;
            return this;
        }

        public RichTextConfigBuild imageGetter(ImageGetter imageGetter) {
            this.imageGetter = imageGetter;
            return this;
        }

        public RichText into(TextView textView) {
            RichText richText = new RichText(new RichTextConfig(this), textView);
            richText.generateAndSet();
            return richText;
        }
    }
}