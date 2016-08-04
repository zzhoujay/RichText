package com.zzhoujay.richtext.ext;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

import com.zzhoujay.richtext.spans.LongClickable;

/**
 * Created by zhou on 16-8-4.
 * 支持长按的MovementMethod
 */
public class LongClickableLinkMovementMethod extends LinkMovementMethod {

    private static final int MIN_INTERVAL = 500;

    private long lastTime;

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

            if (link.length != 0) {
                long currTime = System.currentTimeMillis();
                if (action == MotionEvent.ACTION_UP) {
                    // 如果按下时间超过５００毫秒，触发长按事件
                    if (currTime - lastTime > MIN_INTERVAL && link[0] instanceof LongClickable) {
                        if (!((LongClickable) link[0]).onLongClick(widget)) {
                            // onLongClick返回false代表事件未处理，交由onClick处理
                            link[0].onClick(widget);
                        }
                    } else {
                        link[0].onClick(widget);
                    }
                } else {
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]));
                }
                lastTime = currTime;
                return true;
            } else {
                Selection.removeSelection(buffer);
            }
        }

        return super.onTouchEvent(widget, buffer, event);
    }
}
