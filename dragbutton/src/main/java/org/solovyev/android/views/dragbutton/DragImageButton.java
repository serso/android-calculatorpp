package org.solovyev.android.views.dragbutton;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

public abstract class DragImageButton extends ImageButton implements DragView {
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DragImageButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
