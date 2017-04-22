package com.zzhoujay.richtext.ig;

import android.support.v4.util.LruCache;

import java.io.File;

/**
 * Created by zhou on 2017/3/25.
 * Bitmap图片缓存池
 */
public class BitmapPool {

    private static final int bitmapCacheSize = (int) (Runtime.getRuntime().maxMemory() / 4);

    private LruCache<String, BitmapWrapper> bitmapLruCache;
    private static File cacheDir;
    private static final int version = 1;

    private BitmapPool() {
        bitmapLruCache = new LruCache<String, BitmapWrapper>(bitmapCacheSize) {

            @Override
            protected int sizeOf(String key, BitmapWrapper value) {
                return value.size();
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, BitmapWrapper oldValue, BitmapWrapper newValue) {
                if (oldValue != null && cacheDir != null) {
                    oldValue.save();
                }
            }
        };
    }

    void put(String key, BitmapWrapper bitmapWrapper) {
        bitmapLruCache.put(key, bitmapWrapper);
    }

    BitmapWrapper get(String key, boolean useLocal, boolean readBitmap) {
        BitmapWrapper bitmapWrapper = bitmapLruCache.get(key);
        if (bitmapWrapper == null && useLocal && cacheDir != null) {
            bitmapWrapper = BitmapWrapper.read(key, readBitmap);
            if (bitmapWrapper != null) {
                put(key, bitmapWrapper);
            }
        }
        return bitmapWrapper;
    }

    BitmapWrapper read(String name, boolean readBitmap) {
        if (cacheDir != null) {
            return BitmapWrapper.read(name, readBitmap);
        }
        return null;
    }

    int exist(String name) {
        if (cacheDir != null) {
            return BitmapWrapper.exist(name);
        }
        return -1;
    }

    int hit(String key) {
        BitmapWrapper bitmapWrapper = bitmapLruCache.get(key);
        if (bitmapWrapper != null) {
            if (bitmapWrapper.getBitmap() != null) {
                return 3;
            } else {
                return 2;
            }
        }
        return cacheDir == null ? -1 : BitmapWrapper.exist(key);
    }


    private static class BitmapPoolHolder {
        private static final BitmapPool BITMAP_POOL = new BitmapPool();
    }

    public static BitmapPool getPool() {
        return BitmapPoolHolder.BITMAP_POOL;
    }

    public static void setCacheDir(File cacheDir) {
        if (BitmapPool.cacheDir == null)
            BitmapPool.cacheDir = cacheDir;
    }

    static File getCacheDir() {
        return cacheDir;
    }

    public static int getVersion() {
        return version;
    }

    public void clear() {
        bitmapLruCache.evictAll();
    }

    @SuppressWarnings("unused")
    public void clearLocalDiskCache() {
        BitmapWrapper.clearCache();
    }
}
