package com.zzhoujay.richtext.cache;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * Created by zhou on 16-10-24.
 * Rich Text Cache Manager
 */
public final class RichCacheManager {

    private final HashMap<String, SoftReference<CharSequence>> pool;

    private RichCacheManager() {
        pool = new HashMap<>();
    }

    public void put(String key, CharSequence value) {
        pool.put(key, new SoftReference<>(value));
    }

    public CharSequence get(String key) {
        SoftReference<CharSequence> charSequenceSoftReference = pool.get(key);
        if (charSequenceSoftReference == null) {
            return null;
        }
        return charSequenceSoftReference.get();
    }

    public void clear(String key) {
        pool.remove(key);
    }


    private static class RichCacheManagerHolder {
        private static final RichCacheManager RICH_CACHE_MANAGER = new RichCacheManager();
    }

    public static RichCacheManager getCache() {
        return RichCacheManagerHolder.RICH_CACHE_MANAGER;
    }

}
