package org.solovyev.android.views.dragbutton;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;

import javax.annotation.Nonnull;

import static android.graphics.Color.BLACK;
import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.util.TypedValue.applyDimension;
import static org.solovyev.android.views.dragbutton.DirectionTextView.SHADOW_RADIUS_DPS;

public class DirectionDragButton extends DragButton implements DirectionDragView {
    private final DirectionTextView textView = new DirectionTextView();
    @NonNull
    private final TextPaint baseTextPaint = new TextPaint();
    private boolean highContrast;

    public DirectionDragButton(Context context) {
        super(context);
        init(null);
    }

    public DirectionDragButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DirectionDragButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public DirectionDragButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        textView.init(this, attrs);
        baseTextPaint.set(getPaint());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final TextPaint paint = getPaint();
        if (baseTextPaint.getTextSize() != paint.getTextSize() ||
                baseTextPaint.getTypeface() != paint.getTypeface() ||
                baseTextPaint.getColor() != paint.getColor() ||
                baseTextPaint.getAlpha() != paint.getAlpha()) {
            baseTextPaint.set(paint);
            textView.setBaseTextPaint(paint);
        }
        textView.draw(canvas);
    }

    @NonNull
    public String getTextValue(@NonNull DragDirection direction) {
        return getText(direction).getValue();
    }

    @NonNull
    public DirectionDragButton setText(@NonNull DragDirection direction, @NonNull String value) {
        getText(direction).setValue(value);
        return this;
    }

    @Override
    @Nonnull
    public DirectionTextView.Text getText(@NonNull DragDirection direction) {
        return textView.getText(direction);
    }

    public void setShowDirectionText(@NonNull DragDirection direction, boolean show) {
        getText(direction).setVisible(show);
    }

    public void setDirectionTextColor(@ColorInt int color) {
        for (DragDirection direction : DragDirection.values()) {
            getText(direction).setColor(color);
        }
    }

    public void setDirectionTextAlpha(float alpha) {
        for (DragDirection direction : DragDirection.values()) {
            getText(direction).setAlpha(alpha);
        }
    }

    @Override
    public void setHighContrast(boolean highContrast) {
        if(this.highContrast == highContrast) {
            return;
        }
        this.highContrast = highContrast;
        this.textView.setHighContrast(highContrast);
        if (highContrast) {
            setShadowLayer(applyDimension(COMPLEX_UNIT_DIP, SHADOW_RADIUS_DPS, getResources().getDisplayMetrics()), 0, 0, BLACK);
        } else {
            setShadowLayer(0, 0, 0, BLACK);
        }
    }
}
