package com.zzhoujay.richtext.ig;

import android.support.v4.util.LruCache;

import java.io.File;

/**
 * Created by zhou on 2017/3/25.
 */

public class BitmapPool {

    private static final int boundCacheSize = 30;
    private static final int bitmapCacheSize = (int) (Runtime.getRuntime().maxMemory() / 3);

    private LruCache<String, BitmapWrapper> bitmapLruCache;
    private static File cacheDir;

    private BitmapPool() {
        System.out.println(bitmapCacheSize);
        bitmapLruCache = new LruCache<String, BitmapWrapper>(bitmapCacheSize) {

            @Override
            protected int sizeOf(String key, BitmapWrapper value) {
                return value.size();
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, BitmapWrapper oldValue, BitmapWrapper newValue) {
                if (oldValue != null && cacheDir != null) {
                    oldValue.save(cacheDir);
                }
            }
        };
    }

    void put(String key, BitmapWrapper bitmapWrapper) {
        bitmapLruCache.put(key, bitmapWrapper);
    }

    public BitmapWrapper get(String key, boolean useLocal, boolean readBitmap) {
        BitmapWrapper bitmapWrapper = bitmapLruCache.get(key);
        if (bitmapWrapper == null && useLocal && cacheDir != null) {
            bitmapWrapper = BitmapWrapper.read(cacheDir, key, readBitmap);
            if (bitmapWrapper != null) {
                put(key, bitmapWrapper);
            }
        }
        return bitmapWrapper;
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
        return cacheDir == null ? -1 : BitmapWrapper.exist(cacheDir, key);
    }


    private static class BitmapPoolHolder {
        private static final BitmapPool BITMAP_POOL = new BitmapPool();
    }

    public static BitmapPool getPool() {
        return BitmapPoolHolder.BITMAP_POOL;
    }

    static File getCacheDir() {
        return cacheDir;
    }


    public static void setCacheDir(File cacheDir) {
        if (BitmapPool.cacheDir == null)
            BitmapPool.cacheDir = cacheDir;
    }

    public void clear() {
        bitmapLruCache.evictAll();
    }
}
