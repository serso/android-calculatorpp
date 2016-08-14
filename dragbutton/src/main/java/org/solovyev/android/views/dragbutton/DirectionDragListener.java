package org.solovyev.android.views.dragbutton;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import static java.lang.Math.toDegrees;
import static org.solovyev.android.views.dragbutton.Drag.*;


public abstract class DirectionDragListener implements DragListener {

    @NonNull
    private static final PointF axis = new PointF(0, 1);

    private final float minDistancePxs;
    private final boolean[] right = new boolean[1];

    public DirectionDragListener(@NonNull Context context) {
        this.minDistancePxs = context.getResources().getDimensionPixelSize(R.dimen.drag_min_distance);
    }

    @Override
    public boolean onDrag(@NonNull View view, @NonNull DragEvent e) {
        final long duration = e.motionEvent.getEventTime() - e.motionEvent.getDownTime();
        if (duration < 40 || duration > 2500) {
            Log.v("DirectionDragListener", "Drag stopped: too fast movement, " + duration + "ms");
            return false;
        }

        final float distance = distance(e.start, e.end);
        if (distance < minDistancePxs) {
            Log.v("DirectionDragListener", "Drag stopped: too short distance, " + distance + "pxs");
            return false;
        }

        final double angle = toDegrees(getAngle(e.start, sum(e.start, axis), e.end, right));
        final DragDirection direction = getDirection((float) angle, right[0]);
        if (direction == null) {
            Log.v("DirectionDragListener", "Drag stopped: unknown direction");
            return false;
        }

        return onDrag(view, e, direction);
    }

    protected abstract boolean onDrag(@NonNull View view, @NonNull DragEvent event, @NonNull DragDirection direction);

    @Nullable
    private static DragDirection getDirection(float angle, boolean right) {
        for (DragDirection direction : DragDirection.values()) {
            if (direction == DragDirection.left && right) {
                continue;
            }
            if (direction == DragDirection.right && !right) {
                continue;
            }
            if (direction.angleFrom <= angle && angle <= direction.angleTo) {
                return direction;
            }
        }
        return null;
    }
}