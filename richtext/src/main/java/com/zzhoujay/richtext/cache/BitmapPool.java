package com.zzhoujay.richtext.ig;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.jakewharton.disklrucache.DiskLruCache;
import com.zzhoujay.richtext.drawable.DrawableSizeHolder;
import com.zzhoujay.richtext.exceptions.BitmapCacheException;
import com.zzhoujay.richtext.ext.CacheIOHelper;
import com.zzhoujay.richtext.ext.Debug;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zhou on 2017/3/25.
 * Bitmap图片缓存池
 */
public class BitmapPool {

    private static final String RICH_TEXT_DIR_NAME = "_rt";
    private static final int MAX_BITMAP_LOCAL_CACHE_SIZE = 50 * 1024 * 1024;
    private static final int MAX_SIZE_LOCAL_CACHE_SIZE = 1024 * 1024;
    private static final int MAX_TEMP_LOCAL_CACHE_SIZE = 1024 * 1024 * 10;


    private static final int bitmapCacheSize = (int) (Runtime.getRuntime().maxMemory() / 4);
    private static final int MAX_SIZE_CACHE_SIZE = 100;
    private static final String BITMAP_DIR_NAME = "_b";
    private static final String SIZE_DIR_NAME = "_s";
    private static final String TEMP_DIR_NAME = "_t";
    public static final int BUFFER_SIZE = 1024;
    public static final int MAX_WATI_TIME = 5000;

    private LruCache<String, Bitmap> bitmapCache;
    private LruCache<String, DrawableSizeHolder> sizeHolderCache;
    //    private LruCache<String, BitmapWrapper> bitmapLruCache;
    private static File cacheDir;
    private static final int version = 1;

    private static DiskLruCache bitmapDiskLruCache;
    private static DiskLruCache sizeDiskLruCache;
    private static DiskLruCache tempDiskLruCache;


    private static File bitmapDir;
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

    void cacheBitmap(String key, Bitmap bitmap) {
        bitmapCache.put(key, bitmap);
    }

    void cacheSize(String key, DrawableSizeHolder drawableSizeHolder) {
        sizeHolderCache.put(key, drawableSizeHolder);
        CacheIOHelper.SIZE_CACHE_IO_HELPER.writeToCache(key, drawableSizeHolder, getSizeDiskLruCache());
//        writeSizeToLocalCache(key, drawableSizeHolder);
    }

    void cache(String key, Bitmap bitmap, DrawableSizeHolder drawableSizeHolder) {
        cacheBitmap(key, bitmap);
        cacheSize(key, drawableSizeHolder);
    }

    void writeBitmapToLocal(String key, InputStream inputStream) {
        CacheIOHelper.REMOTE_IMAGE_CACHE_IO_HELPER.writeToCache(key, inputStream, getBitmapDiskLruCache());
//        writeBitmapToLocalCache(key, inputStream);
    }

    InputStream writeAndReadBitmapFromLocal(String key, InputStream inputStream) {
        return writeAndReadBitmapFromLocalCache(key, inputStream);
    }

    void writeBitmapToTemp(String key, InputStream inputStream) {
        CacheIOHelper.REMOTE_IMAGE_CACHE_IO_HELPER.writeToCache(key, inputStream, getTempDiskLruCache());
        writeTempToLocalCache(key, inputStream);
    }

    Bitmap getBitmap(String key) {
        return bitmapCache.get(key);
    }

    boolean hasBitmapLocalCache(String key) {
        return isBitmapLocalCacheExists(key);
    }

    InputStream readBitmapFromLocal(String key) {
        return readBitmapFromLocalCache(key);
    }


    DrawableSizeHolder getSizeHolder(String key) {
        DrawableSizeHolder drawableSizeHolder = sizeHolderCache.get(key);
        if (drawableSizeHolder == null) {
            drawableSizeHolder = readSizeFromLocalCache(key);
        }
        return drawableSizeHolder;
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
            bitmapDir = new File(richTextDir, BITMAP_DIR_NAME);
            if (!bitmapDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                bitmapDir.mkdir();
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

    static File getCacheDir() {
        return cacheDir;
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
            DiskLruCache bitmapDiskLruCache = getBitmapDiskLruCache();
            if (bitmapDiskLruCache != null) {
                bitmapDiskLruCache.delete();
            }
            DiskLruCache sizeDiskLruCache = getSizeDiskLruCache();
            if (sizeDiskLruCache != null) {
                sizeDiskLruCache.delete();
            }
        } catch (IOException e) {
            Debug.e(e);
        }
    }

    /**
     * 从本地缓存读取尺寸信息
     *
     * @param key key
     * @return DrawableSizeHolder
     */
    private static DrawableSizeHolder readSizeFromLocalCache(String key) {

        DiskLruCache sizeDiskLruCache = getSizeDiskLruCache();

        if (sizeDiskLruCache != null) {

            try {
                DiskLruCache.Snapshot snapshot = sizeDiskLruCache.get(key);

                if (snapshot == null) {
                    return null;
                }

                InputStream inputStream = snapshot.getInputStream(0);

                DrawableSizeHolder drawableSizeHolder = DrawableSizeHolder.read(inputStream, key);

                inputStream.close();

                return drawableSizeHolder;
            } catch (IOException e) {
                Debug.e(e);
            }
        }
        return null;
    }

    /**
     * 从本地缓存读取Bitmap流
     *
     * @param key key
     * @return Bitmap流
     */
    private static InputStream readBitmapFromLocalCache(String key) {

        DiskLruCache bitmapDiskLruCache = getBitmapDiskLruCache();

        if (bitmapDiskLruCache != null) {

            DiskLruCache.Snapshot snapshot = null;
            try {
                snapshot = bitmapDiskLruCache.get(key);
            } catch (IOException e) {
                Debug.e(e);
            }
            return snapshot == null ? null : snapshot.getInputStream(0);
        }
        return null;
    }

    /**
     * 向本地缓存写入尺寸信息
     *
     * @param key                key
     * @param drawableSizeHolder DrawableSizeHolder
     */
    private static void writeSizeToLocalCache(String key, DrawableSizeHolder drawableSizeHolder) {

        DiskLruCache sizeDiskLruCache = getSizeDiskLruCache();

        if (sizeDiskLruCache != null) {

            try {
                DiskLruCache.Editor edit = sizeDiskLruCache.edit(key);

                if (edit == null) {
                    return;
                }

                OutputStream outputStream = edit.newOutputStream(0);

                drawableSizeHolder.save(outputStream);

                outputStream.flush();
                outputStream.close();

                edit.commit();
            } catch (IOException e) {
                Debug.e(e);
            }
        }
    }

    /**
     * 向本地缓存写入Bitmap流（下载用）
     *
     * @param key         key
     * @param inputStream 图片输入流
     */
    private static void writeBitmapToLocalCache(String key, InputStream inputStream) {

        DiskLruCache bitmapDiskLruCache = getBitmapDiskLruCache();

        if (bitmapDiskLruCache != null) {

            try {
                DiskLruCache.Editor edit = bitmapDiskLruCache.edit(key);

                if (edit == null) {
                    return;
                }

                OutputStream outputStream = edit.newOutputStream(0);

                byte[] buffer = new byte[BUFFER_SIZE];

                int len;

                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }

                outputStream.flush();
                outputStream.close();

                inputStream.close();

                edit.commit();
            } catch (IOException e) {
                Debug.e(e);
            }

        }

    }

    private static void writeTempToLocalCache(String key, InputStream inputStream) {
        DiskLruCache tempDiskLruCache = getTempDiskLruCache();

        if (tempDiskLruCache != null) {

            try {
                DiskLruCache.Editor edit = tempDiskLruCache.edit(key);

                if (edit == null) {
                    return;
                }

                OutputStream outputStream = edit.newOutputStream(0);

                byte[] buffer = new byte[BUFFER_SIZE];

                int len;

                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }

                outputStream.flush();
                outputStream.close();

                inputStream.close();

                edit.commit();

            } catch (IOException | InterruptedException e) {
                Debug.e(e);
            }
        }
    }

    private static InputStream writeAndReadBitmapFromLocalCache(String key, InputStream inputStream) {
        DiskLruCache bitmapDiskLruCache = getBitmapDiskLruCache();

        if (bitmapDiskLruCache != null) {

            try {
                DiskLruCache.Editor edit = bitmapDiskLruCache.edit(key);

                if (edit != null) {

                    OutputStream outputStream = edit.newOutputStream(0);

                    byte[] buffer = new byte[BUFFER_SIZE];

                    int len;

                    while ((len = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, len);
                    }

                    outputStream.flush();
                    outputStream.close();

                    inputStream.close();

                    edit.commit();
                }

                int i = MAX_WATI_TIME;

                while (i > 0) {

                    DiskLruCache.Snapshot snapshot = bitmapDiskLruCache.get(key);

                    if (snapshot != null) {
                        return snapshot.getInputStream(0);
                    }

                    Thread.sleep(50);
                    i -= 50;
                }

            } catch (Exception e) {
                Debug.e(e);
                throw new BitmapCacheException(e);
            }

        }
        return null;
    }

    /**
     * 检查图片是否有本地缓存
     *
     * @param key key
     * @return true：已有缓存，false：未缓存
     */
    private static boolean isBitmapLocalCacheExists(String key) {
        DiskLruCache bitmapDiskLruCache = getBitmapDiskLruCache();

        if (bitmapDiskLruCache != null) {

            DiskLruCache.Snapshot snapshot = null;
            try {
                snapshot = bitmapDiskLruCache.get(key);
            } catch (IOException e) {
                Debug.e(e);
                return false;
            }

            return snapshot != null;

        }

        return false;
    }

//    private static Bitmap readBitmapFromLocalCache(String key) {
//        DiskLruCache diskLruCache = getBitmapDiskLruCache();
//
//        if (diskLruCache != null) {
//
//            try {
//                DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
//
//                if (snapshot == null) {
//                    return null;
//                }
//
//                InputStream inputStream = snapshot.getInputStream(0);
//
//
//                if (drawableSizeHolder == null) {
//                    return null;
//                }
//
//                Bitmap bitmap = null;
//                if (readBitmap) {
//
//                    InputStream bitmapInputStream = snapshot.getInputStream(1);
//                    boolean hasBitmap = readBoolean(bitmapInputStream);
//                    if (hasBitmap) {
//                        bitmap = decodeBitmap(bitmapInputStream, drawableSizeHolder.rect);
//                    }
//
//                    bitmapInputStream.close();
//                }
//
//                return new BitmapWrapper(name, bitmap, drawableSizeHolder);
//
//            } catch (IOException e) {
//                Debug.e(e);
//            }
//
//        }
//    }

    private static DiskLruCache getBitmapDiskLruCache() {
        if (bitmapDiskLruCache == null && cacheDir != null) {
            try {
                bitmapDiskLruCache = DiskLruCache.open(bitmapDir, version, 1, MAX_BITMAP_LOCAL_CACHE_SIZE);
            } catch (IOException e) {
                Debug.e(e);
            }
        }
        return bitmapDiskLruCache;
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
