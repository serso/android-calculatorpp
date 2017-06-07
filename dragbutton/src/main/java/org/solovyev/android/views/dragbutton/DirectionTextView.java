package org.solovyev.android.views.dragbutton;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.util.EnumMap;
import java.util.Map;

class DirectionTextView {

    static final float SHADOW_RADIUS_DPS = 2;
    private static final float DEF_ALPHA = 0.4f;

    @NonNull
    private final Map<DragDirection, DirectionText> texts = new EnumMap<>(DragDirection.class);
    private float textSize;
    private Typeface typeface;

    DirectionTextView() {
    }

    public void init(@NonNull TextView view, @Nullable AttributeSet attrs) {
        init(view, attrs, view.getPaint());
    }

    public void init(@NonNull View view, @Nullable AttributeSet attrs, @NonNull TextPaint base) {
        textSize = base.getTextSize();
        typeface = base.getTypeface() == null ? Typeface.DEFAULT : base.getTypeface();

        final Context context = view.getContext();
        final Resources res = context.getResources();

        final float minTextSize =
                res.getDimensionPixelSize(R.dimen.drag_direction_text_min_size);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DirectionText);
        final float scale =
                a.getFloat(R.styleable.DirectionText_directionTextScale, DirectionText.DEF_SCALE);
        final float alpha = a.getFloat(R.styleable.DirectionText_directionTextAlpha, DEF_ALPHA);
        final int color = a.getColor(R.styleable.DirectionText_directionTextColor, base.getColor());
        final int padding = a.getDimensionPixelSize(R.styleable.DirectionText_directionTextPadding,
                res.getDimensionPixelSize(R.dimen.drag_direction_text_default_padding));
        for (DragDirection direction : DragDirection.values()) {
            final DirectionText text = new DirectionText(direction, view, minTextSize);
            text.init(a, scale, color, alpha, padding, typeface, textSize);
            texts.put(direction, text);
        }
        a.recycle();
    }

    void draw(@NonNull Canvas canvas) {
        for (DirectionText text : texts.values()) {
            text.draw(canvas);
        }
    }

    @NonNull
    public DirectionText getText(@NonNull DragDirection direction) {
        return texts.get(direction);
    }

    void setHighContrast(boolean highContrast) {
        for (DirectionText text : texts.values()) {
            text.setHighContrast(highContrast);
        }
    }

    public void setTypeface(@NonNull Typeface typeface) {
        if(this.typeface == typeface) {
            return;
        }
        for (DirectionText text : texts.values()) {
            text.setTypeface(typeface);
        }
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        if (this.textSize == textSize) {
            return;
        }
        this.textSize = textSize;
        for (DirectionText text : texts.values()) {
            text.setBaseTextSize(textSize);
        }
    }
}
