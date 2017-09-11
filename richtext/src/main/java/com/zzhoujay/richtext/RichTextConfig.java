package com.zzhoujay.richtext;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
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
import com.zzhoujay.richtext.exceptions.ImageDownloaderNonExistenceException;
import com.zzhoujay.richtext.ig.DefaultImageGetter;
import com.zzhoujay.richtext.ig.ImageDownloader;

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
    public final boolean autoPlay; // Gif图片是否自动播放
    @ImageHolder.ScaleType
    public final int scaleType; // 图片缩放方式
    @CacheType
    public final int cacheType; // 缓存类型
    public final int width; // 图片边框宽度
    public final int height; // 图片边框高度
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
    public final ImageHolder.BorderHolder borderHolder;
    final ImageGetter imageGetter; // 图片加载器，默认为GlideImageGetter
    public final boolean singleLoad;
    public final ImageDownloader imageDownloader;// 图片加载器


    private RichTextConfig(RichTextConfigBuild config) {
        this(config.source, config.richType, config.autoFix, config.resetSize, config.cacheType, config.imageFixCallback,
                config.linkFixCallback, config.noImage, config.clickable, config.onImageClickListener,
                config.onUrlClickListener, config.onImageLongClickListener, config.onUrlLongClickListener,
                config.placeHolder, config.errorImage, config.imageGetter, config.callback, config.autoPlay,
                config.scaleType, config.width, config.height, config.borderHolder, config.singleLoad, config.imageDownloader);
    }

    private RichTextConfig(String source, int richType, boolean autoFix, boolean resetSize, @CacheType int cacheType,
                           ImageFixCallback imageFixCallback, LinkFixCallback linkFixCallback, boolean noImage,
                           int clickable, OnImageClickListener onImageClickListener, OnUrlClickListener onUrlClickListener,
                           OnImageLongClickListener onImageLongClickListener, OnUrlLongClickListener onUrlLongClickListener,
                           Drawable placeHolder, Drawable errorImage, ImageGetter imageGetter, Callback callback,
                           boolean autoPlay, @ImageHolder.ScaleType int scaleType, int width, int height,
                           ImageHolder.BorderHolder borderHolder, boolean singleLoad, ImageDownloader imageDownloader) {
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
        this.scaleType = scaleType;
        this.autoPlay = autoPlay;
        this.width = width;
        this.height = height;
        this.borderHolder = borderHolder;
        this.singleLoad = singleLoad;
        this.imageDownloader = imageDownloader;
        if (clickable == 0) {
            if (onImageLongClickListener != null || onUrlLongClickListener != null ||
                    onImageClickListener != null || onUrlClickListener != null) {
                clickable = 1;
            }
        }
        this.clickable = clickable;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RichTextConfig)) return false;

        RichTextConfig that = (RichTextConfig) o;

        if (richType != that.richType) return false;
        if (autoFix != that.autoFix) return false;
        if (resetSize != that.resetSize) return false;
        if (autoPlay != that.autoPlay) return false;
        if (scaleType != that.scaleType) return false;
        if (cacheType != that.cacheType) return false;
        if (width != that.width) return false;
        if (height != that.height) return false;
        if (noImage != that.noImage) return false;
        if (clickable != that.clickable) return false;
        if (singleLoad != that.singleLoad) return false;
        if (!source.equals(that.source)) return false;
        if (imageFixCallback != null ? !imageFixCallback.equals(that.imageFixCallback) : that.imageFixCallback != null)
            return false;
        if (linkFixCallback != null ? !linkFixCallback.equals(that.linkFixCallback) : that.linkFixCallback != null)
            return false;
        if (onImageClickListener != null ? !onImageClickListener.equals(that.onImageClickListener) : that.onImageClickListener != null)
            return false;
        if (onUrlClickListener != null ? !onUrlClickListener.equals(that.onUrlClickListener) : that.onUrlClickListener != null)
            return false;
        if (onImageLongClickListener != null ? !onImageLongClickListener.equals(that.onImageLongClickListener) : that.onImageLongClickListener != null)
            return false;
        if (onUrlLongClickListener != null ? !onUrlLongClickListener.equals(that.onUrlLongClickListener) : that.onUrlLongClickListener != null)
            return false;
        if (placeHolder != null ? !placeHolder.equals(that.placeHolder) : that.placeHolder != null)
            return false;
        if (errorImage != null ? !errorImage.equals(that.errorImage) : that.errorImage != null)
            return false;
        if (callback != null ? !callback.equals(that.callback) : that.callback != null)
            return false;
        if (borderHolder != null ? !borderHolder.equals(that.borderHolder) : that.borderHolder != null)
            return false;
        if (imageGetter != null ? !imageGetter.equals(that.imageGetter) : that.imageGetter != null)
            return false;
        return imageDownloader != null ? imageDownloader.equals(that.imageDownloader) : that.imageDownloader == null;

    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + richType;
        result = 31 * result + (autoFix ? 1 : 0);
        result = 31 * result + (resetSize ? 1 : 0);
        result = 31 * result + (autoPlay ? 1 : 0);
        result = 31 * result + scaleType;
        result = 31 * result + cacheType;
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + (imageFixCallback != null ? imageFixCallback.hashCode() : 0);
        result = 31 * result + (linkFixCallback != null ? linkFixCallback.hashCode() : 0);
        result = 31 * result + (noImage ? 1 : 0);
        result = 31 * result + clickable;
        result = 31 * result + (onImageClickListener != null ? onImageClickListener.hashCode() : 0);
        result = 31 * result + (onUrlClickListener != null ? onUrlClickListener.hashCode() : 0);
        result = 31 * result + (onImageLongClickListener != null ? onImageLongClickListener.hashCode() : 0);
        result = 31 * result + (onUrlLongClickListener != null ? onUrlLongClickListener.hashCode() : 0);
        result = 31 * result + (placeHolder != null ? placeHolder.hashCode() : 0);
        result = 31 * result + (errorImage != null ? errorImage.hashCode() : 0);
        result = 31 * result + (callback != null ? callback.hashCode() : 0);
        result = 31 * result + (borderHolder != null ? borderHolder.hashCode() : 0);
        result = 31 * result + (imageGetter != null ? imageGetter.hashCode() : 0);
        result = 31 * result + (singleLoad ? 1 : 0);
        result = 31 * result + (imageDownloader != null ? imageDownloader.hashCode() : 0);
        return result;
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
        boolean autoPlay;
        @ImageHolder.ScaleType
        int scaleType;
        int width;
        int height;
        ImageHolder.BorderHolder borderHolder;
        boolean singleLoad;
        ImageDownloader imageDownloader;


        RichTextConfigBuild(String source, int richType) {
            this.source = source;
            this.richType = richType;
            this.autoFix = true;
            this.resetSize = false;
            this.noImage = false;
            this.clickable = 0;
            this.cacheType = CacheType.ALL;
            this.imageGetter = new DefaultImageGetter();
            this.autoPlay = false;
            this.scaleType = ImageHolder.ScaleType.NONE;
            this.width = ImageHolder.WRAP_CONTENT;
            this.height = ImageHolder.WRAP_CONTENT;
            this.borderHolder = new ImageHolder.BorderHolder();
            this.singleLoad = true;
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
         * @param resetSize false：使用标签里的宽高；true：忽略标签里的宽高。默认为false
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
         * 设置Gif图片自动播放
         *
         * @param autoPlay 是否自动播放
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild autoPlay(boolean autoPlay) {
            this.autoPlay = autoPlay;
            return this;
        }

        /**
         * 设置图片缩放方式
         *
         * @param scaleType 缩放方式
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild scaleType(@ImageHolder.ScaleType int scaleType) {
            this.scaleType = scaleType;
            return this;
        }

        /**
         * 设置占位尺寸
         *
         * @param width  宽
         * @param height 高
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * 是否显示边框
         *
         * @param showBorder 显示边框
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild showBorder(boolean showBorder) {
            this.borderHolder.setShowBorder(showBorder);
            return this;
        }

        /**
         * 边框尺寸
         *
         * @param borderSize 边框尺寸
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild borderSize(float borderSize) {
            this.borderHolder.setBorderSize(borderSize);
            return this;
        }

        /**
         * 边框颜色
         *
         * @param color 颜色
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild borderColor(@ColorInt int color) {
            this.borderHolder.setBorderColor(color);
            return this;
        }

        /**
         * 边框边角圆弧弧度
         *
         * @param r 弧度
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild borderRadius(float r) {
            this.borderHolder.setRadius(r);
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
         * 设置是否只允许单个RichText异步解析
         * true：若同时启动了多个RichText，则顺序解析，即上个解析完成才开始后面的加载，类似于AsyncTask的execute
         * false：若同时启动了多个RichText，会并发解析，类似于AsyncTask的executeOnExecutor
         * 仅在API 11及以上生效
         *
         * @param singleLoad 是否只允许单个RichText解析，默认true
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild singleLoad(boolean singleLoad) {
            this.singleLoad = singleLoad;
            return this;
        }

        /**
         * 设置图片下载器
         *
         * @param imageDownloader 设置图片下载器
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild imageDownloader(ImageDownloader imageDownloader) {
            this.imageDownloader = imageDownloader;
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
            // 检查图片下载器是否已设置
            if (imageDownloader == null) {
                // 未设置，判断是否依赖了OkHttpImageDownloader库
                try {
                    //noinspection unchecked
                    Class<ImageDownloader> aClass = (Class<ImageDownloader>) Class.forName("com.zzhoujay.okhttpimagedownloader.OkHttpImageDownloader");
                    imageDownloader = aClass.newInstance();
                    // 自动创建一个OkHttpImageDownloader实例赋值给imageDownloader
                } catch (Exception e) {
                    // 未依赖OkHttpImageDownloader库，直接抛出异常
                    throw new ImageDownloaderNonExistenceException();
                }
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