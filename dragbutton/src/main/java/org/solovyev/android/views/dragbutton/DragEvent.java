package org.solovyev.android.views.dragbutton;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

public class DragEvent {

    @NonNull
    public final PointF start;

    @NonNull
    public final PointF end;

    @NonNull
    public final MotionEvent motionEvent;

    public DragEvent(@NonNull PointF start, @NonNull MotionEvent motionEvent) {
        this.start = start;
        this.end = new PointF(motionEvent.getX(), motionEvent.getY());
        this.motionEvent = motionEvent;
    }
}
