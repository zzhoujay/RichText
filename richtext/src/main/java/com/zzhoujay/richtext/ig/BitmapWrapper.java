package com.zzhoujay.richtext.ig;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.annotation.Nullable;

import com.jakewharton.disklrucache.DiskLruCache;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.ext.Debug;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zhou on 2017/3/25.
 * Bitmap图片包装类，用于缓存
 */
class BitmapWrapper {

    private static final int MARK_POSITION = 1024 * 1024;

    private static final int MAX_BITMAP_SIZE = 20 * 1024 * 1024;

    private static final String RICH_TEXT_DIR_NAME = "_rt";

    private static DiskLruCache diskLruCache;

    private String name;
    private Bitmap bitmap;
    private SizeCacheHolder sizeCacheHolder;

    private BitmapWrapper(String name, Bitmap bitmap, SizeCacheHolder sizeCacheHolder) {
        this.name = name;
        this.bitmap = bitmap;
        this.sizeCacheHolder = sizeCacheHolder;
    }

    BitmapWrapper(String name, Bitmap bitmap, Rect rect, @ImageHolder.ScaleType int scaleType, ImageHolder.BorderHolder borderHolder) {
        this.name = name;
        this.bitmap = bitmap;
        this.sizeCacheHolder = new SizeCacheHolder(name, rect, scaleType, borderHolder);
    }

    public int size() {
        return bitmap == null ? 0 : bitmap.getRowBytes() * bitmap.getHeight() + 100;
    }

    void save() {

        DiskLruCache diskLruCache = getDiskLruCache();

        if (diskLruCache != null) {
            try {
                DiskLruCache.Editor edit = diskLruCache.edit(name);

                // write size
                OutputStream sizeOutputStream = edit.newOutputStream(0);
                sizeCacheHolder.save(sizeOutputStream);

                // write bitmap
                OutputStream bitmapOutputStream = edit.newOutputStream(1);
                writeBoolean(bitmapOutputStream, bitmap != null);
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bitmapOutputStream);
                }
                bitmapOutputStream.flush();
                bitmapOutputStream.close();
                // write bitmap

                edit.commit();
            } catch (IOException e) {
                Debug.e(e);
            }
        }

    }

    static int exist(String name) {

        DiskLruCache diskLruCache = getDiskLruCache();
        if (diskLruCache == null) {
            return -1;
        }
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(name);
            if (snapshot == null) {
                return -1;
            }
            InputStream stream = snapshot.getInputStream(1);
            boolean hasBitmap = readBoolean(stream);
            return hasBitmap ? 1 : 0;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }


    static BitmapWrapper read(String name, boolean readBitmap) {

        DiskLruCache diskLruCache = getDiskLruCache();

        if (diskLruCache != null) {

            try {
                DiskLruCache.Snapshot snapshot = diskLruCache.get(name);

                if (snapshot == null) {
                    return null;
                }

                InputStream sizeInputStream = snapshot.getInputStream(0);
                SizeCacheHolder sizeCacheHolder = SizeCacheHolder.read(sizeInputStream, name);
                if (sizeCacheHolder == null) {
                    return null;
                }

                Bitmap bitmap = null;
                if (readBitmap) {

                    InputStream bitmapInputStream = snapshot.getInputStream(1);
                    boolean hasBitmap = readBoolean(bitmapInputStream);
                    if (hasBitmap) {
                        bitmap = decodeBitmap(bitmapInputStream, sizeCacheHolder.rect);
                    }

                    bitmapInputStream.close();
                }

                return new BitmapWrapper(name, bitmap, sizeCacheHolder);

            } catch (IOException e) {
                Debug.e(e);
            }

        }

        return null;

    }

    private static Bitmap decodeBitmap(InputStream inputStream, Rect rect) {

        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inPreferredConfig = Bitmap.Config.RGB_565;

        if (rect != null) {
            options.inJustDecodeBounds = true;
            bufferedInputStream.mark(MARK_POSITION);
            BitmapFactory.decodeStream(bufferedInputStream, null, options);
            try {
                bufferedInputStream.reset();
            } catch (IOException e) {
                Debug.e(e);
            }
            options.inJustDecodeBounds = false;
            options.inSampleSize = AbstractImageLoader.getSampleSize(options.outWidth, options.outHeight, rect.width(), rect.height());
        }

        return BitmapFactory.decodeStream(bufferedInputStream, null, options);
    }


    private static File checkRichText(File dir) {
        File cacheDir = new File(dir, RICH_TEXT_DIR_NAME);
        if (!cacheDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cacheDir.mkdir();
        }
        return cacheDir;
    }

    @Nullable
    private static DiskLruCache getDiskLruCache() {
        if (diskLruCache == null) {
            try {
                diskLruCache = DiskLruCache.open(checkRichText(BitmapPool.getCacheDir()), BitmapPool.getVersion(), 2, MAX_BITMAP_SIZE);
            } catch (IOException e) {
                Debug.e(e);
            }
        }
        return diskLruCache;
    }

    static void clearCache() {
        DiskLruCache diskLruCache = getDiskLruCache();
        try {
            if (diskLruCache != null) {
                diskLruCache.delete();
            }
        } catch (IOException e) {
            Debug.e(e);
        }
    }

    public String getName() {
        return name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    SizeCacheHolder getSizeCacheHolder() {
        return sizeCacheHolder;
    }

    private static void writeBoolean(OutputStream stream, boolean b) throws IOException {
        stream.write(b ? 1 : 0);
    }

    private static boolean readBoolean(InputStream stream) throws IOException {
        return stream.read() != 0;
    }

    private static int readInt(InputStream stream) throws IOException {
        byte[] bs = new byte[4];
        //noinspection ResultOfMethodCallIgnored
        stream.read(bs);
        return byte2int(bs);
    }

    private static void writeInt(OutputStream stream, int i) throws IOException {
        stream.write(int2byte(i));
    }

    private static void writeFloat(OutputStream stream, float f) throws IOException {
        stream.write(int2byte(Float.floatToIntBits(f)));
    }

    private static float readFloat(InputStream stream) throws IOException {
        return Float.intBitsToFloat(readInt(stream));
    }


    private static byte[] int2byte(int res) {
        byte[] targets = new byte[4];

        targets[0] = (byte) (res & 0xff);// 最低位
        targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
        targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
        targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
        return targets;
    }

    private static int byte2int(byte[] res) {
        return (res[0] & 0xff) | ((res[1] << 8) & 0xff00) // | 表示安位或
                | ((res[2] << 24) >>> 8) | (res[3] << 24);
    }

    static class SizeCacheHolder {

        Rect rect;
        @ImageHolder.ScaleType
        int scaleType;
        String name;
        ImageHolder.BorderHolder borderHolder;

        SizeCacheHolder(String name, Rect rect, @ImageHolder.ScaleType int scaleType, ImageHolder.BorderHolder borderHolder) {
            this.rect = rect;
            this.scaleType = scaleType;
            this.name = name;
            this.borderHolder = borderHolder;
        }

        private void save(OutputStream fos) {
            try {
                writeInt(fos, rect.left);
                writeInt(fos, rect.top);
                writeInt(fos, rect.right);
                writeInt(fos, rect.bottom);
                writeInt(fos, scaleType);
                writeBoolean(fos, borderHolder.isShowBorder());
                writeInt(fos, borderHolder.getBorderColor());
                writeFloat(fos, borderHolder.getBorderSize());
                writeFloat(fos, borderHolder.getRadius());
                fos.flush();
                fos.close();
            } catch (IOException e) {
                Debug.e(e);
            }
        }

        private static SizeCacheHolder read(InputStream fis, String name) {
            try {
                int left = readInt(fis);
                int top = readInt(fis);
                int right = readInt(fis);
                int bottom = readInt(fis);
                int scaleType = readInt(fis);
                boolean showBorder = readBoolean(fis);
                int color = readInt(fis);
                float borderSize = readFloat(fis);
                float borderRadius = readFloat(fis);
                fis.close();
                Rect rect = new Rect(left, top, right, bottom);
                ImageHolder.BorderHolder borderHolder = new ImageHolder.BorderHolder(showBorder, borderSize, color, borderRadius);
                return new SizeCacheHolder(name, rect, getScaleType(scaleType), borderHolder);
            } catch (IOException e) {
                Debug.e(e);
            }
            return null;
        }


        @ImageHolder.ScaleType
        private static int getScaleType(int value) {
            switch (value) {
                case ImageHolder.ScaleType.CENTER:
                    return ImageHolder.ScaleType.CENTER;
                case ImageHolder.ScaleType.CENTER_CROP:
                    return ImageHolder.ScaleType.CENTER_CROP;
                case ImageHolder.ScaleType.CENTER_INSIDE:
                    return ImageHolder.ScaleType.CENTER_INSIDE;
                case ImageHolder.ScaleType.FIT_CENTER:
                    return ImageHolder.ScaleType.FIT_CENTER;
                case ImageHolder.ScaleType.FIT_START:
                    return ImageHolder.ScaleType.FIT_START;
                case ImageHolder.ScaleType.FIT_END:
                    return ImageHolder.ScaleType.FIT_END;
                case ImageHolder.ScaleType.FIT_XY:
                    return ImageHolder.ScaleType.FIT_XY;
                case ImageHolder.ScaleType.FIT_AUTO:
                    return ImageHolder.ScaleType.FIT_AUTO;
                default:
                    return ImageHolder.ScaleType.NONE;
            }
        }
    }
}
