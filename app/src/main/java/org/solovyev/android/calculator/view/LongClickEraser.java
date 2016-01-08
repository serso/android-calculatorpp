package org.solovyev.android.calculator.view;

import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import org.solovyev.android.calculator.Calculator;
import org.solovyev.android.calculator.Editor;
import org.solovyev.android.calculator.EditorState;
import org.solovyev.android.calculator.Locator;

import javax.annotation.Nonnull;

import static android.text.TextUtils.isEmpty;

public final class LongClickEraser implements View.OnTouchListener {

    @Nonnull
    private final View view;

    @Nonnull
    private final GestureDetector gestureDetector;

    @Nonnull
    private final Editor editor = Locator.getInstance().getEditor();

    @Nonnull
    private final Calculator calculator = Locator.getInstance().getCalculator();

    @Nonnull
    private final Eraser eraser = new Eraser();

    private LongClickEraser(@Nonnull final View view) {
        this.view = view;
        this.gestureDetector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent e) {
                if (eraser.isTracking()) {
                    eraser.start();
                }
            }
        });
    }

    public static void createAndAttach(@Nonnull View view) {
        final LongClickEraser l = new LongClickEraser(view);
        view.setOnTouchListener(l);
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

    private class Eraser implements Runnable {
        private static final int DELAY = 300;
        private long delay;
        private boolean erasing;
        private boolean tracking = true;
        private boolean wasCalculatingOnFly;

        @Override
        public void run() {
            final EditorState state = editor.erase();
            if (isEmpty(state.text)) {
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
            wasCalculatingOnFly = calculator.isCalculateOnFly();
            if (wasCalculatingOnFly) {
                calculator.setCalculateOnFly(false);
            }
            run();
        }

        void stop() {
            view.removeCallbacks(this);
            if (!erasing) {
                return;
            }

            erasing = false;
            if (wasCalculatingOnFly) {
                calculator.setCalculateOnFly(true);
            }
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
