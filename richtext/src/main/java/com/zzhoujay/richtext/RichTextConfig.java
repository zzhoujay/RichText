package com.zzhoujay.richtext;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.v4.util.Pair;
import android.widget.TextView;

import com.zzhoujay.richtext.callback.Callback;
import com.zzhoujay.richtext.callback.DrawableGetter;
import com.zzhoujay.richtext.callback.ImageFixCallback;
import com.zzhoujay.richtext.callback.ImageGetter;
import com.zzhoujay.richtext.callback.LinkFixCallback;
import com.zzhoujay.richtext.callback.OnImageClickListener;
import com.zzhoujay.richtext.callback.OnImageLongClickListener;
import com.zzhoujay.richtext.callback.OnUrlClickListener;
import com.zzhoujay.richtext.callback.OnUrlLongClickListener;
import com.zzhoujay.richtext.drawable.DrawableBorderHolder;
import com.zzhoujay.richtext.ig.DefaultImageDownloader;
import com.zzhoujay.richtext.ig.DefaultImageGetter;
import com.zzhoujay.richtext.ig.ImageDownloader;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by zhou on 2016/12/3.
 * RichText的各种配置
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class RichTextConfig {

    public static final String OK_HTTP_GLOBAL_ID = "com.zzhoujay.okhttpimagedownloader.OkHttpImageDownloader";
    public final String source; // 源文本
    public final RichType richType; // 富文本类型，默认HTML
    public final boolean autoFix; // 图片自动修复，默认true
    public final boolean resetSize; // 是否放弃使用img标签中的尺寸属性，默认false
    public final boolean autoPlay; // Gif图片是否自动播放
    public final ImageHolder.ScaleType scaleType; // 图片缩放方式
    public final CacheType cacheType; // 缓存类型
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
    public final Callback callback; // 解析完成的回调
    public final DrawableBorderHolder borderHolder;
    final ImageGetter imageGetter; // 图片加载器，默认为GlideImageGetter
    public final boolean singleLoad;
    public final boolean syncParse;
    public final ImageDownloader imageDownloader;// 图片加载器
    public final DrawableGetter placeHolderDrawableGetter, errorImageDrawableGetter;


    private WeakReference<RichText> richTextInstanceWeakReference;

    private final HashMap<String, Object> argsPool;

    void setRichTextInstance(RichText richTextInstance) {
        if (this.richTextInstanceWeakReference == null)
            this.richTextInstanceWeakReference = new WeakReference<>(richTextInstance);
    }

    public RichText getRichTextInstance() {
        return richTextInstanceWeakReference == null ? null : richTextInstanceWeakReference.get();
    }

    public void setArgs(String key, Object args) {
        argsPool.put(key, args);
    }

    public Object getArgs(String key) {
        return argsPool.get(key);
    }


    private RichTextConfig(RichTextConfigBuild config) {
        this(config.source, config.richType, config.autoFix, config.resetSize, config.cacheType, config.imageFixCallback,
                config.linkFixCallback, config.noImage, config.clickable, config.onImageClickListener,
                config.onUrlClickListener, config.onImageLongClickListener, config.onUrlLongClickListener,
                config.imageGetter, config.callback, config.autoPlay, config.scaleType, config.width,
                config.height, config.borderHolder, config.singleLoad, config.syncParse, config.imageDownloader, config.placeHolderDrawableGetter,
                config.errorImageDrawableGetter);
    }

    private RichTextConfig(String source, RichType richType, boolean autoFix, boolean resetSize, CacheType cacheType,
                           ImageFixCallback imageFixCallback, LinkFixCallback linkFixCallback, boolean noImage,
                           int clickable, OnImageClickListener onImageClickListener, OnUrlClickListener onUrlClickListener,
                           OnImageLongClickListener onImageLongClickListener, OnUrlLongClickListener onUrlLongClickListener,
                           ImageGetter imageGetter, Callback callback, boolean autoPlay, ImageHolder.ScaleType scaleType,
                           int width, int height, DrawableBorderHolder borderHolder, boolean singleLoad, boolean syncParse,
                           ImageDownloader imageDownloader, DrawableGetter placeHolderDrawableGetter, DrawableGetter errorImageDrawableGetter) {
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
        this.imageGetter = imageGetter;
        this.callback = callback;
        this.scaleType = scaleType;
        this.autoPlay = autoPlay;
        this.width = width;
        this.height = height;
        this.borderHolder = borderHolder;
        this.singleLoad = singleLoad;
        this.syncParse = syncParse;
        this.imageDownloader = imageDownloader;
        this.placeHolderDrawableGetter = placeHolderDrawableGetter;
        this.errorImageDrawableGetter = errorImageDrawableGetter;
        if (clickable == 0) {
            if (onImageLongClickListener != null || onUrlLongClickListener != null ||
                    onImageClickListener != null || onUrlClickListener != null) {
                clickable = 1;
            }
        }
        this.clickable = clickable;

        argsPool = new HashMap<>();
    }

    public int key() {
        int result = source.hashCode();
        result = 31 * result + richType.hashCode();
        result = 31 * result + (autoFix ? 1 : 0);
        result = 31 * result + (resetSize ? 1 : 0);
        result = 31 * result + (autoPlay ? 1 : 0);
        result = 31 * result + scaleType.hashCode();
        result = 31 * result + cacheType.hashCode();
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + (noImage ? 1 : 0);
        result = 31 * result + clickable;
        result = 31 * result + borderHolder.hashCode();
        return result;
    }

    @SuppressWarnings({"unused", "SameParameterValue"})
    public static final class RichTextConfigBuild {

        final String source;
        RichType richType;
        boolean autoFix;
        boolean resetSize;
        CacheType cacheType;
        ImageFixCallback imageFixCallback;
        LinkFixCallback linkFixCallback;
        boolean noImage;
        int clickable;
        OnImageClickListener onImageClickListener;
        OnUrlClickListener onUrlClickListener;
        OnImageLongClickListener onImageLongClickListener;
        OnUrlLongClickListener onUrlLongClickListener;
        ImageGetter imageGetter;
        Callback callback;
        WeakReference<Object> tag;
        boolean autoPlay;
        ImageHolder.ScaleType scaleType;
        int width;
        int height;
        DrawableBorderHolder borderHolder;
        boolean singleLoad;
        boolean syncParse;
        ImageDownloader imageDownloader;
        DrawableGetter placeHolderDrawableGetter, errorImageDrawableGetter;

        RichTextConfigBuild(String source, RichType richType) {
            this.source = source;
            this.richType = richType;
            this.autoFix = true;
            this.resetSize = false;
            this.noImage = false;
            this.clickable = 0;
            this.cacheType = CacheType.all;
            this.autoPlay = false;
            this.scaleType = ImageHolder.ScaleType.none;
            this.width = ImageHolder.WRAP_CONTENT;
            this.height = ImageHolder.WRAP_CONTENT;
            this.borderHolder = new DrawableBorderHolder();
            this.singleLoad = true;
            this.placeHolderDrawableGetter = PLACE_HOLDER_DRAWABLE_GETTER;
            this.errorImageDrawableGetter = ERROR_IMAGE_DRAWABLE_GETTER;
            this.syncParse = false;
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
        public RichTextConfigBuild cache(CacheType cacheType) {
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
        public RichTextConfigBuild type(RichType richType) {
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
        public RichTextConfigBuild scaleType(ImageHolder.ScaleType scaleType) {
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
         * 设置placeHolder创建器
         *
         * @param drawableGetter placeHolderDrawableGetter
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild placeHolder(DrawableGetter drawableGetter) {
            this.placeHolderDrawableGetter = drawableGetter;
            return this;
        }

        /**
         * 设置errorImage创建器
         *
         * @param drawableGetter errorImageDrawableGetter
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild errorImage(DrawableGetter drawableGetter) {
            this.errorImageDrawableGetter = drawableGetter;
            return this;
        }

        /**
         * 设置imageGetter
         *
         * @param imageGetter ig，如果未null则使用DefaultImageGetter
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
         * 是否同步解析，默认false
         *
         * @param syncParse true：同步解析，false：异步解析
         * @return RichTextConfigBuild
         */
        public RichTextConfigBuild sync(boolean syncParse) {
            this.syncParse = syncParse;
            return this;
        }

        private static final int SET_BOUNDS = 0x09;

        private static final Handler HANDLER = new Handler(Looper.getMainLooper()) {
            @Override
            public void dispatchMessage(Message msg) {
                if (msg.what == SET_BOUNDS) {
                    //noinspection unchecked
                    Pair<Drawable, TextView> pair = (Pair<Drawable, TextView>) msg.obj;
                    Drawable drawable = pair.first;
                    TextView textView = pair.second;
                    int width = textView.getWidth() - textView.getPaddingLeft() - textView.getPaddingRight();
                    drawable.setBounds(0, 0, width, width / 2);
                }
            }
        };

        private static final DrawableGetter PLACE_HOLDER_DRAWABLE_GETTER = new DrawableGetter() {

            @Override
            public Drawable getDrawable(ImageHolder holder, RichTextConfig config, TextView textView) {
                ColorDrawable drawable = new ColorDrawable(Color.LTGRAY);
                int width = textView.getWidth() ;
                drawable.setBounds(0, 0, width, width / 2);
                HANDLER.obtainMessage(SET_BOUNDS, Pair.create(drawable, textView)).sendToTarget();
                return drawable;
            }
        };

        private static final DrawableGetter ERROR_IMAGE_DRAWABLE_GETTER = new DrawableGetter() {

            @Override
            public Drawable getDrawable(ImageHolder holder, RichTextConfig config, TextView textView) {
                ColorDrawable drawable = new ColorDrawable(Color.DKGRAY);
                int width = textView.getWidth();
                drawable.setBounds(0, 0, width, width / 2);
                HANDLER.obtainMessage(SET_BOUNDS, Pair.create(drawable, textView)).sendToTarget();
                return drawable;
            }
        };

        /**
         * 加载并设置给textView
         *
         * @param textView TextView
         * @return RichTextConfigBuild
         */
        public RichText into(TextView textView) {
            // 检查图片下载器是否已设置
            if (imageGetter == null) {
                // 未设置，使用DefaultImageGetter
//                DefaultImageGetter defaultImageGetter = (DefaultImageGetter) RichText.getArgs(DefaultImageGetter.GLOBAL_ID);
//                if (defaultImageGetter == null) {
//                    defaultImageGetter = new DefaultImageGetter();
//                    RichText.putArgs(DefaultImageGetter.GLOBAL_ID, defaultImageGetter);
//                }
//                imageGetter = defaultImageGetter;
                imageGetter = new DefaultImageGetter();
            }
            if (imageGetter instanceof DefaultImageGetter && imageDownloader == null) {
                try {
                    // 检查是否依赖了okhttp模块
                    //noinspection unchecked
                    Class<ImageDownloader> aClass = (Class<ImageDownloader>) Class.forName(OK_HTTP_GLOBAL_ID);
                    ImageDownloader downloader = (ImageDownloader) RichText.getArgs(OK_HTTP_GLOBAL_ID);
                    if (downloader == null) {
                        downloader = aClass.newInstance();
                        RichText.putArgs(OK_HTTP_GLOBAL_ID, downloader);
                    }
                    imageDownloader = downloader;
                } catch (Exception e) {
                    // 未依赖okhttp模块，使用DefaultImageDownloader
                    DefaultImageDownloader defaultImageDownloader = (DefaultImageDownloader) RichText.getArgs(DefaultImageDownloader.GLOBAL_ID);
                    if (defaultImageDownloader == null) {
                        defaultImageDownloader = new DefaultImageDownloader();
                        RichText.putArgs(DefaultImageDownloader.GLOBAL_ID, defaultImageDownloader);
                    }
                    imageDownloader = defaultImageDownloader;
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