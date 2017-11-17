package com.zzhoujay.richtext.cache;

import com.jakewharton.disklrucache.DiskLruCache;
import com.zzhoujay.richtext.callback.BitmapStream;
import com.zzhoujay.richtext.drawable.DrawableSizeHolder;
import com.zzhoujay.richtext.ext.Debug;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zhou on 2017/10/21.
 * CacheIOHelper
 */

interface CacheIOHelper<INPUT, OUTPUT> {

    int BUFFER_SIZE = 1024;

    CacheIOHelper<DrawableSizeHolder, DrawableSizeHolder> SIZE_CACHE_IO_HELPER = new CacheIOHelper<DrawableSizeHolder, DrawableSizeHolder>() {
        @Override
        public void writeToCache(String key, DrawableSizeHolder input, DiskLruCache cache) {
            if (cache != null) {

                try {
                    DiskLruCache.Editor edit = cache.edit(key);

                    if (edit == null) {
                        return;
                    }

                    OutputStream outputStream = edit.newOutputStream(0);

                    input.save(outputStream);

                    outputStream.flush();
                    outputStream.close();

                    edit.commit();
                } catch (IOException e) {
                    Debug.e(e);
                }
            }
        }

        @Override
        public DrawableSizeHolder readFromCache(String key, DiskLruCache cache) {
            if (cache != null) {

                try {
                    DiskLruCache.Snapshot snapshot = cache.get(key);

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

        @Override
        public boolean hasCache(String key, DiskLruCache cache) {
            if (cache != null) {
                try {
                    DiskLruCache.Snapshot snapshot = cache.get(key);
                    return snapshot != null;
                } catch (IOException e) {
                    Debug.e(e);
                }
            }
            return false;
        }
    };

    CacheIOHelper<InputStream, InputStream> REMOTE_IMAGE_CACHE_IO_HELPER = new CacheIOHelper<InputStream, InputStream>() {
        @Override
        public void writeToCache(String key, InputStream input, DiskLruCache cache) {
            if (cache != null) {

                try {
                    DiskLruCache.Editor edit = cache.edit(key);

                    if (edit == null) {
                        return;
                    }

                    OutputStream outputStream = edit.newOutputStream(0);

                    byte[] buffer = new byte[BUFFER_SIZE];

                    int len;

                    while ((len = input.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, len);
                    }

                    outputStream.flush();
                    outputStream.close();

                    input.close();

                    edit.commit();
                } catch (IOException e) {
                    Debug.e(e);
                }

            }
        }

        @Override
        public InputStream readFromCache(String key, DiskLruCache cache) {
            if (cache != null) {

                DiskLruCache.Snapshot snapshot = null;
                try {
                    snapshot = cache.get(key);
                } catch (IOException e) {
                    Debug.e(e);
                }
                return snapshot == null ? null : snapshot.getInputStream(0);
            }
            return null;
        }

        @Override
        public boolean hasCache(String key, DiskLruCache cache) {
            if (cache != null) {
                try {
                    DiskLruCache.Snapshot snapshot = cache.get(key);
                    return snapshot != null;
                } catch (IOException e) {
                    Debug.e(e);
                }
            }
            return false;
        }
    };


    void writeToCache(String key, INPUT input, DiskLruCache cache);

    OUTPUT readFromCache(String key, DiskLruCache cache);

    boolean hasCache(String key, DiskLruCache cache);


}
