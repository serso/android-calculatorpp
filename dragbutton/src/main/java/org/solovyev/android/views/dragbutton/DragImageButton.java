package org.solovyev.android.views.dragbutton;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.MotionEvent;

public abstract class DragImageButton extends AppCompatImageButton implements DragView {
    @NonNull
    private final DragGestureDetector dragDetector = new DragGestureDetector(this);

    public DragImageButton(Context context) {
        super(context);
    }

    public DragImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
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

    public void setOnDragListener(@Nullable DragListener listener) {
        dragDetector.setListener(listener);
    }

    public void setVibrateOnDrag(boolean vibrateOnDrag) {
        dragDetector.setVibrateOnDrag(vibrateOnDrag);
    }
}
