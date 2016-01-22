package org.solovyev.android.calculator.view;

import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import javax.annotation.Nonnull;

public abstract class BaseLongClickEraser implements View.OnTouchListener {

    @Nonnull
    private final View view;

    @Nonnull
    private final GestureDetector gestureDetector;

    @Nonnull
    private final Eraser eraser = new Eraser();

    protected BaseLongClickEraser(@Nonnull final View view) {
        this.view = view;
        this.gestureDetector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent e) {
                if (eraser.isTracking()) {
                    eraser.start();
                }
            }
        });
        this.view.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                eraser.stopTracking();
                break;
            default:
                eraser.startTracking();
                gestureDetector.onTouchEvent(event);
                break;
        }
        return false;
    }

    protected abstract void onStopErase();

    protected abstract void onStartErase();

    protected abstract boolean erase();

    private class Eraser implements Runnable {
        private static final int DELAY = 300;
        private long delay;
        private boolean erasing;
        private boolean tracking = true;

        @Override
        public void run() {
            if (!erase()) {
                stop();
                return;
            }
            delay = Math.max(50, 2 * delay / 3);
            view.postDelayed(this, delay);
        }

        void start() {
            if (erasing) {
                stop();
            }
            erasing = true;
            delay = DELAY;
            view.removeCallbacks(this);
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            onStartErase();
            run();
        }

        void stop() {
            view.removeCallbacks(this);
            if (!erasing) {
                return;
            }

            erasing = false;
            onStopErase();
        }

        public void stopTracking() {
            stop();
            tracking = false;
        }

        public boolean isTracking() {
            return tracking;
        }

        public void startTracking() {
            tracking = true;
        }
    }
}
