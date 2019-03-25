package com.zzhoujay.richtext;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.zzhoujay.richtext.cache.BitmapPool;
import com.zzhoujay.richtext.callback.ImageLoadNotify;
import com.zzhoujay.richtext.ext.ContextKit;
import com.zzhoujay.richtext.ext.Debug;
import com.zzhoujay.richtext.ext.HtmlTagHandler;
import com.zzhoujay.richtext.ext.LongClickableLinkMovementMethod;
import com.zzhoujay.richtext.parser.CachedSpannedParser;
import com.zzhoujay.richtext.parser.Html2SpannedParser;
import com.zzhoujay.richtext.parser.ImageGetterWrapper;
import com.zzhoujay.richtext.parser.Markdown2SpannedParser;
import com.zzhoujay.richtext.parser.SpannedParser;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhou on 16-5-28.
 * 富文本生成器
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class RichText implements ImageGetterWrapper, ImageLoadNotify {

    private static final String TAG = "RichText";

    public static boolean debugMode = true;


    static void bind(Object tag, RichText richText) {
        RichTextPool.getPool().bind(tag, richText);
    }

    /**
     * 清除tag绑定的所有RichText，并清除所有的图片缓存
     *
     * @param tag TAG
     */
    public static void clear(Object tag) {
        RichTextPool.getPool().clear(tag);
    }


    /**
     * 设置缓存目录，若不设置将不会对图片进行本地缓存
     *
     * @param cacheDir 缓存目录，请确保对该目录有读写权限
     */
    public static void initCacheDir(File cacheDir) {
        BitmapPool.setCacheDir(cacheDir);
    }

    /**
     * 清除缓存
     */
    public static void recycle() {
        BitmapPool.getPool().clear();
        RichTextPool.getPool().recycle();
    }

    /**
     * 设置缓存目录，若不设置将不会对图片进行本地缓存
     *
     * @param context Context
     */
    public static void initCacheDir(Context context) {
        File cacheDir;
        cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = context.getCacheDir();
        }
        initCacheDir(cacheDir);
    }

    static void putArgs(String key, Object args) {
        synchronized (GLOBAL_ARGS) {
            GLOBAL_ARGS.put(key, args);
        }
    }

    static Object getArgs(String key) {
        synchronized (GLOBAL_ARGS) {
            return GLOBAL_ARGS.get(key);
        }
    }

    private static final String TAG_TARGET = "target";

    private static Pattern IMAGE_TAG_PATTERN = Pattern.compile("<(img|IMG)(.*?)>");
    private static Pattern IMAGE_WIDTH_PATTERN = Pattern.compile("(width|WIDTH)=\"(.*?)\"");
    private static Pattern IMAGE_HEIGHT_PATTERN = Pattern.compile("(height|HEIGHT)=\"(.*?)\"");
    private static Pattern IMAGE_SRC_PATTERN = Pattern.compile("(src|SRC)=\"(.*?)\"");


    private static final HashMap<String, Object> GLOBAL_ARGS = new HashMap<>();

    private HashMap<String, ImageHolder> imageHolderMap;

    private RichState state = RichState.ready;

    private final SpannedParser spannedParser;
    private final CachedSpannedParser cachedSpannedParser;
    private final WeakReference<TextView> textViewWeakReference;
    private final RichTextConfig config;
    private int count;
    private int loadingCount;

    RichText(RichTextConfig config, TextView textView) {
        this.config = config;
        this.textViewWeakReference = new WeakReference<>(textView);
        if (config.richType == RichType.markdown) {
            spannedParser = new Markdown2SpannedParser(textView);
        } else {
            spannedParser = new Html2SpannedParser(new HtmlTagHandler(textView));
        }
        if (config.clickable > 0) {
            textView.setMovementMethod(new LongClickableLinkMovementMethod());
        } else if (config.clickable == 0) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        this.cachedSpannedParser = new CachedSpannedParser();

        config.setRichTextInstance(this);
    }

    public static RichTextConfig.RichTextConfigBuild from(String source) {
        return fromHtml(source);
    }

    public static RichTextConfig.RichTextConfigBuild fromHtml(String source) {
        return from(source, RichType.html);
    }

    public static RichTextConfig.RichTextConfigBuild fromMarkdown(String source) {
        return from(source, RichType.markdown);
    }

    public static RichTextConfig.RichTextConfigBuild from(String source, RichType richType) {
        return new RichTextConfig.RichTextConfigBuild(source, richType);
    }

    void generateAndSet() {
        TextView textView = textViewWeakReference.get();
        if (textView == null) {
            Debug.loge(TAG, "generateAndSet textView is recycle");
            return;
        }
        if (config.syncParse) {
            CharSequence richText = generateRichText();
            textView.setText(richText);
            if (config.callback != null) {
                config.callback.done(false);
            }
        } else {
            asyncGenerate(textView);
        }
    }

    /**
     * 生成富文本
     *
     * @return Spanned
     */
    CharSequence generateRichText() {
        TextView textView = textViewWeakReference.get();
        if (textView == null) {
            return null;
        }
        if (config.richType != RichType.markdown) {
            analyzeImages(config.source);
        } else {
            imageHolderMap = new HashMap<>();
        }
        state = RichState.loading;
        SpannableStringBuilder spannableStringBuilder = null;
        if (config.cacheType.intValue() > CacheType.none.intValue()) {
            spannableStringBuilder = RichTextPool.getPool().loadCache(config.source);
        }
        if (spannableStringBuilder == null) {
            spannableStringBuilder = parseRichText();
        }
        config.imageGetter.registerImageLoadNotify(this);
        count = cachedSpannedParser.parse(spannableStringBuilder, this, config);
        return spannableStringBuilder;
    }

    private void asyncGenerate(TextView textView) {

        ParseAsyncTask asyncTask = new ParseAsyncTask(this, textView);

        // 启动AsyncTask
        if (config.singleLoad) {
            //noinspection unchecked
            asyncTask.execute();
        } else {
            //noinspection unchecked
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private static class ParseAsyncTask extends AsyncTask<Void, Void, CharSequence> {

        private WeakReference<TextView> textViewWeakReference;
        private RichText richText;

        ParseAsyncTask(RichText richText, TextView textView) {
            this.richText = richText;
            this.textViewWeakReference = new WeakReference<>(textView);
        }

        @Override
        protected CharSequence doInBackground(Void[] weakReferences) {
            TextView tv = textViewWeakReference.get();
            if (tv == null) {
                return null;
            } else {
                return richText.generateRichText();
            }
        }

        @Override
        protected void onPostExecute(CharSequence charSequence) {
            if (textViewWeakReference == null) {
                return;
            }
            TextView tv = textViewWeakReference.get();
            if (tv == null || charSequence == null) {
                return;
            }
            if (richText.config.cacheType.intValue() >= CacheType.layout.intValue()) {
                RichTextPool.getPool().cache(richText.config.source, (SpannableStringBuilder) charSequence);
            }
            tv.setText(charSequence);

            if (richText.config.callback != null) {
                richText.config.callback.done(false);
            }
        }
    }

    @NonNull
    private SpannableStringBuilder parseRichText() {
        SpannableStringBuilder spannableStringBuilder;
        String source = config.source;

        Spanned spanned = spannedParser.parse(source);
        if (spanned instanceof SpannableStringBuilder) {
            spannableStringBuilder = (SpannableStringBuilder) spanned;
        } else {
            if (spanned == null) {
                spanned = new SpannableString("");
            }
            spannableStringBuilder = new SpannableStringBuilder(spanned);
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
        Matcher imageTagMatcher = IMAGE_TAG_PATTERN.matcher(text);
        while (imageTagMatcher.find()) {
            String image = imageTagMatcher.group(2).trim();
            Matcher imageSrcMatcher = IMAGE_SRC_PATTERN.matcher(image);
            String src = null;
            if (imageSrcMatcher.find()) {
                src = imageSrcMatcher.group(2).trim();
            }
            if (TextUtils.isEmpty(src)) {
                continue;
            }
            holder = new ImageHolder(src, position, config, textViewWeakReference.get());
            holder.setIsGif(isGif(src));
            if (!config.autoFix && !config.resetSize) {
                Matcher imageWidthMatcher = IMAGE_WIDTH_PATTERN.matcher(image);
                if (imageWidthMatcher.find()) {
                    holder.setWidth(parseStringToInteger(imageWidthMatcher.group(2).trim()));
                }
                Matcher imageHeightMatcher = IMAGE_HEIGHT_PATTERN.matcher(image);
                if (imageHeightMatcher.find()) {
                    holder.setHeight(parseStringToInteger(imageHeightMatcher.group(2).trim()));
                }
            }
            imageHolderMap.put(holder.getSource(), holder);
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

    private static boolean isGif(String path) {
        int index = path.lastIndexOf('.');
        return index > 0 && "gif".toUpperCase().equals(path.substring(index + 1).toUpperCase());
    }

    /**
     * 回收所有图片和任务
     */
    public void clear() {
        TextView textView = textViewWeakReference.get();
        if (textView != null) {
            textView.setText(null);
        }
        config.imageGetter.recycle();
    }


    /**
     * 获取解析的状态
     *
     * @return state
     * @see RichState
     */
    public RichState getState() {
        return state;
    }

    @Override
    public Drawable getDrawable(String source) {
        loadingCount++;
        if (config.imageGetter == null) {
            return null;
        }
        if (config.noImage) {
            return null;
        }
        TextView textView = textViewWeakReference.get();
        if (textView == null) {
            return null;
        }
        // 判断activity是否已结束
        if (!ContextKit.activityIsAlive(textView.getContext())) {
            return null;
        }
        ImageHolder holder;
        if (config.richType == RichType.markdown) {
            holder = new ImageHolder(source, loadingCount - 1, config, textView);
            imageHolderMap.put(source, holder);
        } else {
            holder = imageHolderMap.get(source);
            if (holder == null) {
                holder = new ImageHolder(source, loadingCount - 1, config, textView);
                imageHolderMap.put(source, holder);
            }
        }
        holder.setImageState(ImageHolder.ImageState.INIT);
        if (config.imageFixCallback != null) {
            config.imageFixCallback.onInit(holder);
            if (!holder.isShow()) {
                return null;
            }
        }
        return config.imageGetter.getDrawable(holder, config, textView);
    }

    @Override
    public void done(Object from) {
        if (from instanceof Integer) {
            int loadedCount = (int) from;
            if (loadedCount >= count) {
                state = RichState.loaded;
                TextView tv = textViewWeakReference.get();
                if (config.callback != null) {
                    if (null != tv) {
                        tv.post(new Runnable() {
                            @Override
                            public void run() {
                                config.callback.done(true);
                            }
                        });
                    }
                }
            }
        }
    }

}
