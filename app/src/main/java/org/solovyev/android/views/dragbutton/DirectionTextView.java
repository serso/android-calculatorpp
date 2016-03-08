package org.solovyev.android.views.dragbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Strings;

import org.solovyev.android.Check;
import org.solovyev.android.calculator.R;

import java.util.EnumMap;
import java.util.Map;

public class DirectionTextView {

    public static final float DEF_ALPHA = 0.55f;
    public static final float DEF_SCALE = 0.4f;

    @NonNull
    private final Map<DragDirection, Text> texts = new EnumMap<>(DragDirection.class);

    public DirectionTextView() {
    }

    public void init(@NonNull TextView view, @Nullable AttributeSet attrs) {
        init(view, attrs, view.getPaint());
    }

    public void setBaseTextPaint(@NonNull TextPaint baseTextPaint) {
        for (Text text : texts.values()) {
            text.initPaint(baseTextPaint);
        }
    }

    public void init(@NonNull View view, @Nullable AttributeSet attrs, @NonNull TextPaint baseTextPaint) {
        Check.isTrue(texts.isEmpty());
        final Context context = view.getContext();
        final int defColor = baseTextPaint.getColor();
        final int defPadding = context.getResources().getDimensionPixelSize(R.dimen.cpp_direction_text_default_padding);
        final float minTextSize = context.getResources().getDimensionPixelSize(R.dimen.cpp_direction_text_min_size);


        if (attrs == null) {
            for (DragDirection direction : DragDirection.values()) {
                final Text text = new Text(direction, view, minTextSize);
                text.init(baseTextPaint, null, DEF_SCALE, defColor, DEF_ALPHA, defPadding);
                texts.put(direction, text);
            }
            return;
        }
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DirectionText);
        final float scale = array.getFloat(R.styleable.DirectionText_directionTextScale, DEF_SCALE);
        final float alpha = array.getFloat(R.styleable.DirectionText_directionTextAlpha, DEF_ALPHA);
        final int color = array.getColor(R.styleable.DirectionText_directionTextColor, defColor);
        final int padding = array.getDimensionPixelSize(R.styleable.DirectionText_directionTextPadding, defPadding);
        for (DragDirection direction : DragDirection.values()) {
            final Text text = new Text(direction, view, minTextSize);
            text.init(baseTextPaint, array, scale, color, alpha, padding);
            texts.put(direction, text);
        }
        array.recycle();
    }

    public void draw(@NonNull Canvas canvas) {
        for (Text text : texts.values()) {
            text.draw(canvas);
        }
    }

    @NonNull
    public Text getText(@NonNull DragDirection direction) {
        return texts.get(direction);
    }

    public static class Text {
        public final Rect bounds = new Rect();
        @NonNull
        private final TextPaint paint = new TextPaint();
        @NonNull
        private final DragDirection direction;
        @NonNull
        private final View view;
        private final float minTextSize;
        @NonNull
        private final PointF position = new PointF(-1, -1);
        @NonNull
        private String value = "";
        private float scale;
        private int color;
        private float alpha;
        private boolean visible = true;
        private int padding;

        public Text(@NonNull DragDirection direction, @NonNull View view, float minTextSize) {
            this.direction = direction;
            this.view = view;
            this.minTextSize = minTextSize;
        }

        public void init(@NonNull TextPaint base, @Nullable TypedArray array, float defScale, int defColor, float defAlpha, int defPadding) {
            if (array != null) {
                if (array.hasValue(direction.textAttr)) {
                    value = Strings.nullToEmpty(array.getString(direction.textAttr));
                }
                scale = array.getFloat(direction.scaleAttr, defScale);
            } else {
                value = "";
                scale = defScale;
            }
            alpha = defAlpha;
            color = defColor;
            padding = defPadding;
            initPaint(base);
        }

        public void initPaint(@NonNull TextPaint base) {
            paint.set(base);
            paint.setColor(color);
            paint.setAlpha(intAlpha());
            paint.setTextSize(Math.max(base.getTextSize() * scale, minTextSize));
            invalidate(true);
        }

        private int intAlpha() {
            return (int) (255 * alpha);
        }

        public void setVisible(boolean visible) {
            if (this.visible == visible) {
                return;
            }
            this.visible = visible;
            invalidate(false);
        }

        public void setColor(int color) {
            setColor(color, this.alpha);
        }

        public void setColor(int color, float alpha) {
            if (this.color == color && this.alpha == alpha) {
                return;
            }
            this.color = color;
            this.alpha = alpha;
            paint.setColor(color);
            paint.setAlpha(intAlpha());
            invalidate(false);
        }

        private void invalidate(boolean remeasure) {
            Check.isNotNull(view);
            view.invalidate();
            if (remeasure) {
                position.set(-1, -1);
            }
        }

        public void draw(@NonNull Canvas canvas) {
            if (!visible || TextUtils.isEmpty(value)) {
                return;
            }
            if (position.x < 0 || position.y < 0) {
                calculatePosition();
            }
            canvas.drawText(value, position.x, position.y, paint);
        }

        private void calculatePosition() {
            paint.getTextBounds(value, 0, value.length(), bounds);
            switch (direction) {
                case up:
                case down:
                    position.x = view.getWidth() - view.getPaddingLeft() - padding - bounds.width();
                    if (direction == DragDirection.up) {
                        position.y = view.getPaddingTop() + padding + bounds.height();
                    } else {
                        position.y = view.getHeight() - view.getPaddingBottom() - padding;
                    }
                    break;
                case left:
                case right:
                    if (direction == DragDirection.left) {
                        position.x = view.getPaddingLeft() + padding;
                    } else {
                        position.x = view.getWidth() - view.getPaddingLeft() - padding - bounds.width();
                    }
                    final int availableHeight = view.getHeight() - view.getPaddingTop() - view.getPaddingBottom();
                    position.y = view.getPaddingTop() + padding + availableHeight / 2 + bounds.height() / 2;
                    break;
            }
        }

        @NonNull
        public String getValue() {
            return value;
        }

        public void setValue(@NonNull String value) {
            if (TextUtils.equals(this.value, value)) {
                return;
            }
            this.value = value;
            invalidate(true);
        }
    }
}
