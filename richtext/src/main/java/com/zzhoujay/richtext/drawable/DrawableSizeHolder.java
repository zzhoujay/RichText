package com.zzhoujay.richtext.drawable;

import android.graphics.RectF;

import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.ext.Debug;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zhou on 2017/10/2.
 * DrawableSizeHolder
 */
@SuppressWarnings("unused")
public class DrawableSizeHolder {

    RectF border;
    ImageHolder.ScaleType scaleType;
    private String name;
    DrawableBorderHolder borderHolder;

    private DrawableSizeHolder(String name, RectF border, ImageHolder.ScaleType scaleType, DrawableBorderHolder borderHolder) {
        this.border = border;
        this.scaleType = scaleType;
        this.name = name;
        this.borderHolder = borderHolder;
    }

    DrawableSizeHolder(ImageHolder holder) {
        this(
                holder.getKey(),
                new RectF(0, 0, holder.getWidth(), holder.getHeight()),
                holder.getScaleType(),
                new DrawableBorderHolder(holder.getBorderHolder())
        );
    }

    void set(DrawableSizeHolder sizeHolder) {
        this.borderHolder.set(sizeHolder.borderHolder);
        this.border.set(sizeHolder.border);
        this.scaleType = sizeHolder.scaleType;
        this.name = sizeHolder.name;
    }

    public void save(OutputStream fos) {
        try {
            writeFloat(fos, border.left);
            writeFloat(fos, border.top);
            writeFloat(fos, border.right);
            writeFloat(fos, border.bottom);
            writeInt(fos, scaleType.intValue());
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

    public static DrawableSizeHolder read(InputStream fis, String name) {
        try {
            float left = readFloat(fis);
            float top = readFloat(fis);
            float right = readFloat(fis);
            float bottom = readFloat(fis);
            int scaleType = readInt(fis);
            boolean showBorder = readBoolean(fis);
            int color = readInt(fis);
            float borderSize = readFloat(fis);
            float borderRadius = readFloat(fis);
            fis.close();
            RectF border = new RectF(left, top, right, bottom);
            DrawableBorderHolder borderHolder = new DrawableBorderHolder(showBorder, borderSize, color, borderRadius);
            return new DrawableSizeHolder(name, border, ImageHolder.ScaleType.valueOf(scaleType), borderHolder);
        } catch (IOException e) {
            Debug.e(e);
        }
        return null;
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

    public RectF getBorder() {
        return border;
    }

    public ImageHolder.ScaleType getScaleType() {
        return scaleType;
    }

    public String getName() {
        return name;
    }

    public DrawableBorderHolder getBorderHolder() {
        return borderHolder;
    }
}
