package com.zzhoujay.richtext.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

/**
 * Created by zhou on 2017/2/21.
 */

public class GifDrawable extends Drawable {

    private Movie movie;
    private long start;
    private int height;
    private int width;
    private boolean running;
    private TextView textView;
    private float scaleX;
    private float scaleY;

    private Paint paint;

    public GifDrawable(Movie movie, int height, int width) {
        this.movie = movie;
        this.height = height;
        this.width = width;
        scaleX = scaleY = 1.0f;
        paint = new Paint();
    }

    @Override
    public void draw(Canvas canvas) {
        long now = android.os.SystemClock.uptimeMillis();
        if (start == 0) { // first time
            start = now;
        }
        if (movie != null) {
            int dur = movie.duration();
            if (dur == 0) {
                dur = 1000;
            }
            int relTime = (int) ((now - start) % dur);
            movie.setTime(relTime);
            Rect bounds = getBounds();
            canvas.scale(scaleX, scaleY);
            movie.draw(canvas, bounds.left, bounds.top);
            if (this.running && textView != null) {
                textView.setText(textView.getText());
            }
        }
    }

    @Override
    public void setBounds(Rect bounds) {
        super.setBounds(bounds);
        calculateScale();
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        calculateScale();
    }

    private void calculateScale() {
        scaleX = (float) getBounds().width() / width;
        scaleY = (float) getBounds().height() / height;
    }

    public void start(TextView textView) {
        running = true;
        this.textView = textView;
    }

    public void stop() {
        running = false;
        this.textView = null;
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getIntrinsicHeight() {
        return height;
    }

    @Override
    public int getIntrinsicWidth() {
        return width;
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
