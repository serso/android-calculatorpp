package org.solovyev.android.views.dragbutton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

public class DirectionDragImageButton extends DragImageButton implements DirectionDragView {
    private final DirectionTextView textView = new DirectionTextView();

    public DirectionDragImageButton(Context context) {
        super(context);
        init(null);
    }

    public DirectionDragImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DirectionDragImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public DirectionDragImageButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        final TextView view = new TextView(getContext(), attrs);
        textView.init(this, attrs, view.getPaint());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        textView.draw(canvas);
    }

    @NonNull
    public DirectionText getText(@NonNull DragDirection direction) {
        return textView.getText(direction);
    }

    public void setTypeface(@NonNull Typeface typeface) {
        textView.setTypeface(typeface);
    }

    public void setTextSize(float textSize) {
        textView.setTextSize(textSize);
    }

    public float getTextSize() {
        return textView.getTextSize();
    }

    @Override
    public void setHighContrast(boolean highContrast) {
        textView.setHighContrast(highContrast);
    }
}
