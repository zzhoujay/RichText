package com.zzhoujay.richtext.cache;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.jakewharton.disklrucache.DiskLruCache;
import com.zzhoujay.richtext.drawable.DrawableSizeHolder;
import com.zzhoujay.richtext.ext.Debug;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhou on 2017/3/25.
 * Bitmap cache pool
 */
public class BitmapPool {

    private static final String RICH_TEXT_DIR_NAME = "_rt";
    private static final int MAX_SIZE_LOCAL_CACHE_SIZE = 1024 * 1024;
    private static final int MAX_TEMP_LOCAL_CACHE_SIZE = 1024 * 1024 * 500;


    private static final int bitmapCacheSize = (int) (Runtime.getRuntime().maxMemory() / 4);
    private static final int MAX_SIZE_CACHE_SIZE = 100;
    private static final String SIZE_DIR_NAME = "_s";
    private static final String TEMP_DIR_NAME = "_t";

    private final LruCache<String, Bitmap> bitmapCache;
    private final LruCache<String, DrawableSizeHolder> sizeHolderCache;
    private static File cacheDir;
    private static final int version = 1;

    private static DiskLruCache sizeDiskLruCache;
    private static DiskLruCache tempDiskLruCache;

    private static File sizeDir;
    private static File tempDir;

    private BitmapPool() {
        bitmapCache = new LruCache<String, Bitmap>(bitmapCacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap == null ? 0 : bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
        sizeHolderCache = new LruCache<>(MAX_SIZE_CACHE_SIZE);
    }

    public void cacheBitmap(String key, Bitmap bitmap) {
        bitmapCache.put(key, bitmap);
    }

    public void cacheSize(String key, DrawableSizeHolder drawableSizeHolder) {
        sizeHolderCache.put(key, drawableSizeHolder);
        CacheIOHelper.SIZE_CACHE_IO_HELPER.writeToCache(key, drawableSizeHolder, getSizeDiskLruCache());
    }

    public void cache(String key, Bitmap bitmap, DrawableSizeHolder drawableSizeHolder) {
        cacheBitmap(key, bitmap);
        cacheSize(key, drawableSizeHolder);
    }

    public Bitmap getBitmap(String key) {
        return bitmapCache.get(key);
    }

    public DrawableSizeHolder getSizeHolder(String key) {
        DrawableSizeHolder drawableSizeHolder = sizeHolderCache.get(key);
        if (drawableSizeHolder == null) {
            drawableSizeHolder = CacheIOHelper.SIZE_CACHE_IO_HELPER.readFromCache(key, getSizeDiskLruCache());
        }
        return drawableSizeHolder;
    }

    public void writeBitmapToTemp(String key, InputStream inputStream) {
        CacheIOHelper.REMOTE_IMAGE_CACHE_IO_HELPER.writeToCache(key, inputStream, getTempDiskLruCache());
    }

    public InputStream readBitmapFromTemp(String key) {
        return CacheIOHelper.REMOTE_IMAGE_CACHE_IO_HELPER.readFromCache(key, getTempDiskLruCache());
    }

    public boolean hasBitmapLocalCache(String key) {
        return CacheIOHelper.REMOTE_IMAGE_CACHE_IO_HELPER.hasCache(key, getTempDiskLruCache());
    }

    private static class BitmapPoolHolder {
        private static final BitmapPool BITMAP_POOL = new BitmapPool();
    }

    public static BitmapPool getPool() {
        return BitmapPoolHolder.BITMAP_POOL;
    }

    public static void setCacheDir(File cacheDir) {
        if (BitmapPool.cacheDir == null && cacheDir != null) {
            BitmapPool.cacheDir = cacheDir;
            File richTextDir = new File(cacheDir, RICH_TEXT_DIR_NAME);
            if (!richTextDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                richTextDir.mkdir();
            }
            sizeDir = new File(richTextDir, SIZE_DIR_NAME);
            if (!sizeDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                sizeDir.mkdir();
            }
            tempDir = new File(richTextDir, TEMP_DIR_NAME);
            if (!tempDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                tempDir.mkdir();
            }
        }
    }

    public static int getVersion() {
        return version;
    }

    public void clear() {
        bitmapCache.evictAll();
        sizeHolderCache.evictAll();
    }

    @SuppressWarnings("unused")
    public void clearLocalDiskCache() {
        try {
            DiskLruCache sizeDiskLruCache = getSizeDiskLruCache();
            if (sizeDiskLruCache != null) {
                sizeDiskLruCache.delete();
            }
        } catch (IOException e) {
            Debug.e(e);
        }
    }

    private static DiskLruCache getSizeDiskLruCache() {

        if (sizeDiskLruCache == null && cacheDir != null) {
            try {
                sizeDiskLruCache = DiskLruCache.open(sizeDir, version, 1, MAX_SIZE_LOCAL_CACHE_SIZE);
            } catch (IOException e) {
                Debug.e(e);
            }
        }

        return sizeDiskLruCache;
    }

    private static DiskLruCache getTempDiskLruCache() {
        if (tempDiskLruCache == null && cacheDir != null) {
            try {
                tempDiskLruCache = DiskLruCache.open(tempDir, version, 1, MAX_TEMP_LOCAL_CACHE_SIZE);
            } catch (IOException e) {
                Debug.e(e);
            }
        }
        return tempDiskLruCache;
    }
}
