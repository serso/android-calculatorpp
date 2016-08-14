package org.solovyev.android.views.dragbutton;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public final class Drag {

    private Drag() {
    }

    public static float distance(@NonNull PointF start,
                                 @NonNull PointF end) {
        return norm(end.x - start.x, end.y - start.y);
    }

    @NonNull
    public static PointF subtract(@NonNull PointF p1, @NonNull PointF p2) {
        return new PointF(p1.x - p2.x, p1.y - p2.y);
    }

    public static boolean hasDirectionText(@NonNull View view, @NonNull DragDirection direction) {
        if (view instanceof DirectionDragView) {
            return ((DirectionDragView) view).getText(direction).hasValue();
        }
        return false;
    }

    @NonNull
    public static PointF sum(@NonNull PointF p1, @NonNull PointF p2) {
        return new PointF(p1.x + p2.x, p1.y + p2.y);
    }

    public static float norm(@NonNull PointF point) {
        return norm(point.x, point.y);
    }

    private static float norm(float x, float y) {
        //noinspection SuspiciousNameCombination
        return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public static float getAngle(@NonNull PointF start,
                                 @NonNull PointF axisEnd,
                                 @NonNull PointF end,
                                 @Nullable boolean[] right) {
        final PointF axisVector = subtract(axisEnd, start);
        final PointF vector = subtract(end, start);

        double a_2 = Math.pow(distance(vector, axisVector), 2);
        double b = norm(vector);
        double b_2 = Math.pow(b, 2);
        double c = norm(axisVector);
        double c_2 = Math.pow(c, 2);

        if (right != null) {
            right[0] = axisVector.x * vector.y - axisVector.y * vector.x < 0;
        }

        return (float) Math.acos((-a_2 + b_2 + c_2) / (2 * b * c));
    }

}