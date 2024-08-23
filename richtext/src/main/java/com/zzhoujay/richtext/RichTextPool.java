package com.zzhoujay.richtext;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.LruCache;

import com.zzhoujay.richtext.ext.Debug;
import com.zzhoujay.richtext.ext.MD5;
import com.zzhoujay.richtext.parser.CachedSpannedParser;
import com.zzhoujay.richtext.spans.ClickableImageSpan;
import com.zzhoujay.richtext.spans.LongClickableURLSpan;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.WeakHashMap;

/**
 * Created by zhou on 2017/3/25.
 * RichTextPool
 */

@SuppressWarnings("WeakerAccess")
class RichTextPool {

    private static final String TAG = "RichTextPool";

    private static final int MAX_RICH_TEXT_SIZE = 50;

    private final LruCache<String, SoftReference<SpannableStringBuilder>> richCache;
    private final WeakHashMap<Object, HashSet<WeakReference<RichText>>> instances;


    private RichTextPool() {
        richCache = new LruCache<>(MAX_RICH_TEXT_SIZE);
        instances = new WeakHashMap<>();
    }

    void cache(String source, SpannableStringBuilder ssb) {
        String key = MD5.generate(source);
        if (richCache.get(key) != null) {
            Debug.log(TAG, "cached");
            return;
        }
        ssb = clearSpans(new SpannableStringBuilder(ssb));
        ssb.setSpan(new CachedSpannedParser.Cached(), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        richCache.put(key, new SoftReference<>(ssb));
    }

    SpannableStringBuilder loadCache(String source) {
        SoftReference<SpannableStringBuilder> cache = richCache.get(MD5.generate(source));
        SpannableStringBuilder ssb = cache == null ? null : cache.get();
        if (ssb != null) {
            Debug.log(TAG, "cache hit -- text");
            return new SpannableStringBuilder(ssb);
        }
        return null;
    }

    SpannableStringBuilder clearSpans(SpannableStringBuilder ssb) {
        ClickableImageSpan[] spans = ssb.getSpans(0, ssb.length(), ClickableImageSpan.class);
        if (spans != null && spans.length > 0) {
            for (ClickableImageSpan span : spans) {
                int start = ssb.getSpanStart(span);
                int end = ssb.getSpanEnd(span);
                int flags = ssb.getSpanFlags(span);
                ClickableImageSpan copy = span.copy();
                ssb.removeSpan(span);
                ssb.setSpan(copy, start, end, flags);
            }
            Debug.loge(TAG, "clearSpans > " + spans.length);
        }

        LongClickableURLSpan[] lcus = ssb.getSpans(0, ssb.length(), LongClickableURLSpan.class);
        if (lcus != null) {
            for (LongClickableURLSpan span : lcus) {
                int start = ssb.getSpanStart(span);
                int end = ssb.getSpanEnd(span);
                int flags = ssb.getSpanFlags(span);
                LongClickableURLSpan copy = span.copy();
                ssb.removeSpan(span);
                ssb.setSpan(copy, start, end, flags);
            }
        }

        return ssb;
    }

    void clear(Object tag) {
        HashSet<WeakReference<RichText>> richTexts = instances.get(tag);
        if (richTexts != null) {
            for (WeakReference<RichText> weakReference : richTexts) {
                RichText richText = weakReference.get();
                if (richText != null) {
                    richText.clear();
                }
            }
        }
        instances.remove(tag);
    }

    void bind(Object tag, RichText richText) {
        HashSet<WeakReference<RichText>> richTexts = instances.get(tag);
        if (richTexts == null) {
            richTexts = new HashSet<>();
            instances.put(tag, richTexts);
        }
        richTexts.add(new WeakReference<>(richText));
    }


    private static class RichTextPoolHolder {
        private static final RichTextPool RICH_TEXT_POOL = new RichTextPool();
    }

    public static RichTextPool getPool() {
        return RichTextPoolHolder.RICH_TEXT_POOL;
    }

    public void recycle() {
        richCache.evictAll();
    }

}
