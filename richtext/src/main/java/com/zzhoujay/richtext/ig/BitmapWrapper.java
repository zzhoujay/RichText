package com.zzhoujay.richtext.ig;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zhou on 2017/3/25.
 */

class BitmapWrapper {

    private static final String BITMAP_DIR_NAME = "_b";
    private static final String RECT_DIR_NAME = "_r";

    private String name;
    private Bitmap bitmap;
    private Rect rect;

    BitmapWrapper(String name, Bitmap bitmap, Rect rect) {
        this.name = name;
        this.bitmap = bitmap;
        this.rect = rect;
    }

    public int size() {
        return bitmap == null ? 0 : bitmap.getRowBytes() * bitmap.getHeight() + 100;
    }

    void save(File dir) {

        File rectFile = new File(checkRectDir(dir), name);

        try {
            FileOutputStream ros = new FileOutputStream(rectFile);
            writeInt(ros, rect.left);
            writeInt(ros, rect.top);
            writeInt(ros, rect.right);
            writeInt(ros, rect.bottom);
            ros.flush();
            ros.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bitmap != null) {
            File bitmapFile = new File(checkBitmapDir(dir), name);
            try {
                FileOutputStream fos = new FileOutputStream(bitmapFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static int exist(File dir, String name) {
        File rectFile = new File(checkRectDir(dir), name);
        if (rectFile.exists()) {
            File bitmapFile = new File(checkBitmapDir(dir), name);
            if (bitmapFile.exists()) {
                return 1;
            }
            return 0;
        }
        return -1;
    }

    static BitmapWrapper read(File dir, String name, boolean readBitmap) {
        File rectFile = new File(checkRectDir(dir), name);

        try {
            FileInputStream ris = new FileInputStream(rectFile);
            int left = readInt(ris);
            int top = readInt(ris);
            int right = readInt(ris);
            int bottom = readInt(ris);
            ris.close();
            Rect rect = new Rect(left, top, right, bottom);
            return new BitmapWrapper(name, readBitmap ? decodeBitmap(dir, name, rect) : null, rect);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    private static Bitmap decodeBitmap(File dir, String name, Rect rect) {
        File bitmapFile = new File(checkBitmapDir(dir), name);
        if (!bitmapFile.exists()) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(bitmapFile.getAbsolutePath());
        options.inJustDecodeBounds = false;
        options.inSampleSize = AbstractImageLoader.getSampleSize(options.outWidth, options.outHeight, rect.width(), rect.height());
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), options);
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


    private static File checkBitmapDir(File dir) {
        File bitmapDir = new File(dir, BITMAP_DIR_NAME);
        if (!bitmapDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            bitmapDir.mkdir();
        }
        return bitmapDir;
    }

    private static File checkRectDir(File dir) {
        File rectDir = new File(dir, RECT_DIR_NAME);
        if (!rectDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            rectDir.mkdir();
        }
        return rectDir;
    }


    public String getName() {
        return name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Rect getRect() {
        return rect;
    }
}
