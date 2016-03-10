package org.solovyev.android.views.dragbutton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import javax.annotation.Nonnull;

public class DirectionDragImageButton extends DragImageButton implements DirectionDragView {
    private final DirectionTextView textView = new DirectionTextView();
    private final TextPaint baseTextPaint = new TextPaint();

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
        baseTextPaint.set(view.getPaint());
        textView.init(this, attrs, baseTextPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        textView.draw(canvas);
    }

    @Nonnull
    public DirectionTextView.Text getText(@NonNull DragDirection direction) {
        return textView.getText(direction);
    }

    public void setTypeface(@Nonnull Typeface newTypeface) {
        final Typeface oldTypeface = baseTextPaint.getTypeface();
        if(oldTypeface == newTypeface) {
            return;
        }
        baseTextPaint.setTypeface(newTypeface);
        textView.setBaseTextPaint(baseTextPaint);
    }
}
