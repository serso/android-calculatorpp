package org.solovyev.android.views.dragbutton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class DirectionDragButton extends DragButton implements DirectionDragView {
    private final DirectionTextView textView = new DirectionTextView();

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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
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
    public void setTypeface(Typeface tf, int style) {
        super.setTypeface(tf, style);
        textView.setTypeface(getPaint().getTypeface());
    }

    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        textView.setTextSize(getPaint().getTextSize());
    }

    @Override
    @NonNull
    public DirectionText getText(@NonNull DragDirection direction) {
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
        textView.setHighContrast(highContrast);
    }
}
