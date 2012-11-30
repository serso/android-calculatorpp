package org.solovyev.android.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 4/19/12
 * Time: 12:51 AM
 */
public class GrayableImpl implements ViewGrayable {

    @Nullable
    private Paint paint;

    @Override
    public void grayOut() {
        paint = new Paint();
        paint.setARGB(180, 75, 75, 75);
    }

    @Override
    public void grayIn() {
        paint = null;
    }

    @Override
    public void dispatchDraw(@NotNull View view, @NotNull Canvas canvas) {
        final Paint localPaint = paint;
        if (localPaint != null) {
            final RectF drawRect = new RectF();
            drawRect.set(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

            canvas.drawRoundRect(drawRect, 5, 5, localPaint);
        }
    }
}
