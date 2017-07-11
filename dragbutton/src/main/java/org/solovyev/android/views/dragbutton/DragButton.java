package org.solovyev.android.views.dragbutton;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;

public abstract class DragButton extends AppCompatButton implements DragView {
    @NonNull
    private final DragGestureDetector dragDetector = new DragGestureDetector(this);

    public DragButton(Context context) {
        super(context);
    }

    public DragButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (dragDetector.onTouchEvent(event)) {
            final MotionEvent cancelEvent = DragGestureDetector.makeCancelEvent(event);
            super.onTouchEvent(cancelEvent);
            cancelEvent.recycle();
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void setOnDragListener(@Nullable DragListener listener) {
        dragDetector.setListener(listener);
    }

    @Override
    public void setVibrateOnDrag(boolean vibrateOnDrag) {
        dragDetector.setVibrateOnDrag(vibrateOnDrag);
    }
}
