package org.solovyev.android.views.dragbutton;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import static android.view.HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING;
import static android.view.HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING;
import static android.view.HapticFeedbackConstants.KEYBOARD_TAP;

public class DragGestureDetector {

    @NonNull
    private final View view;
    @Nullable
    private DragListener listener;
    @Nullable
    private PointF start;
    private boolean vibrateOnDrag = true;

    public DragGestureDetector(@NonNull View view) {
        this.view = view;
    }

    @NonNull
    static MotionEvent makeCancelEvent(@NonNull MotionEvent original) {
        final MotionEvent event = MotionEvent.obtain(original);
        event.setAction(MotionEvent.ACTION_CANCEL);
        return event;
    }

    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTracking(event);
                return false;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                return stopTracking(event);
        }

        return false;
    }

    private boolean stopTracking(@NonNull MotionEvent event) {
        if (start == null || listener == null) {
            start = null;
            return false;
        }
        if (!listener.onDrag(view, new DragEvent(start, event))) {
            start = null;
            return false;
        }
        start = null;
        if (vibrateOnDrag) {
            view.performHapticFeedback(KEYBOARD_TAP, FLAG_IGNORE_GLOBAL_SETTING | FLAG_IGNORE_VIEW_SETTING);
        }
        return true;
    }

    public void setVibrateOnDrag(boolean vibrateOnDrag) {
        this.vibrateOnDrag = vibrateOnDrag;
    }

    private void startTracking(@NonNull MotionEvent event) {
        start = new PointF(event.getX(), event.getY());
    }

    public void setListener(@Nullable DragListener listener) {
        this.listener = listener;
    }
}
