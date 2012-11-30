package org.solovyev.android.view;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 11/3/12
 * Time: 10:49 PM
 */
public final class AndroidViewUtils {

    private AndroidViewUtils() {
        throw new AssertionError();
    }

    public static boolean drawDrawables(Canvas canvas, @NotNull TextView textView) {
        final int compoundPaddingLeft = textView.getCompoundPaddingLeft();
        final int compoundPaddingTop = textView.getCompoundPaddingTop();
        final int compoundPaddingRight = textView.getCompoundPaddingRight();
        final int compoundPaddingBottom = textView.getCompoundPaddingBottom();

        final int scrollX = textView.getScrollX();
        final int scrollY = textView.getScrollY();

        final int right = textView.getRight();
        final int left = textView.getLeft();
        final int bottom = textView.getBottom();
        final int top = textView.getTop();

        final Drawable[] drawables = textView.getCompoundDrawables();
        if (drawables != null) {

            int vspace = bottom - top - compoundPaddingBottom - compoundPaddingTop;
            int hspace = right - left - compoundPaddingRight - compoundPaddingLeft;

            Drawable topDr = drawables[1];
            // IMPORTANT: The coordinates computed are also used in invalidateDrawable()
            // Make sure to update invalidateDrawable() when changing this code.
            if (topDr != null) {
                canvas.save();
                canvas.translate(scrollX + compoundPaddingLeft + (hspace - topDr.getBounds().width()) / 2,
                        scrollY + textView.getPaddingTop() + vspace / 2);
                topDr.draw(canvas);
                canvas.restore();
                return true;
            }
        }

        return false;
    }

    public static void applyButtonDef(@NotNull Button button, @NotNull ButtonDef buttonDef) {
        button.setText(buttonDef.getText());

        final Integer drawableResId = buttonDef.getDrawableResId();
        if ( drawableResId != null ) {
            button.setPadding(0, 0, 0, 0);

            final Drawable drawable = button.getContext().getResources().getDrawable(drawableResId);
            button.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            button.setCompoundDrawablePadding(0);
        }

        applyViewDef(button, buttonDef);
    }

    public static void applyButtonDef(@NotNull ImageButton imageButton, @NotNull ButtonDef buttonDef) {
        final Integer drawableResId = buttonDef.getDrawableResId();
        if ( drawableResId != null ) {
            imageButton.setImageDrawable(imageButton.getContext().getResources().getDrawable(drawableResId));
        }

        applyViewDef(imageButton, buttonDef);
    }

    public static void applyViewDef(@NotNull View view, @NotNull ViewDef viewDef) {
        final Integer backgroundResId = viewDef.getBackgroundResId();
        if (backgroundResId != null) {
            view.setBackgroundResource(backgroundResId);
        }

        final String tag = viewDef.getTag();
        if ( tag != null ) {
            view.setTag(tag);
        }
    }
}
