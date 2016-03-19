package org.solovyev.android.views.dragbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
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

    public static final float DEF_ALPHA = 0.4f;
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
        private final PointF offset = new PointF(0, 0);
        private float fixedTextHeight = 0;
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
                padding = array.getDimensionPixelSize(direction.paddingAttr, defPadding);
                scale = array.getFloat(direction.scaleAttr, defScale);
            } else {
                value = "";
                scale = defScale;
                padding = defPadding;
            }
            alpha = defAlpha;
            color = defColor;
            initPaint(base);
        }

        public void initPaint(@NonNull TextPaint base) {
            paint.set(base);
            paint.setColor(color);
            paint.setAlpha(intAlpha());
            final Typeface typeface = base.getTypeface();
            if (typeface != null && typeface.getStyle() != Typeface.NORMAL) {
                paint.setTypeface(Typeface.create(typeface, Typeface.NORMAL));
            }

            // pre-calculate fixed height
            paint.setTextSize(Math.max(base.getTextSize() * DEF_SCALE, minTextSize));
            paint.getTextBounds("|", 0, 1, bounds);
            fixedTextHeight = bounds.height();

            // set real text size value
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
            setColor(color, alpha);
        }

        public void setAlpha(float alpha) {
            setColor(color, alpha);
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
                offset.set(0, 0);
            }
        }

        public void draw(@NonNull Canvas canvas) {
            if (!hasValue()) {
                return;
            }
            if (offset.x == 0 && offset.y == 0) {
                calculatePosition();
            }
            final int width = view.getWidth();
            final int height = view.getHeight();
            switch (direction) {
                case up:
                    canvas.drawText(value, width + offset.x, offset.y, paint);
                    break;
                case down:
                    canvas.drawText(value, width + offset.x, height + offset.y, paint);
                    break;
                case left:
                    canvas.drawText(value, offset.x, height / 2 + offset.y, paint);
                    break;
                case right:
                    canvas.drawText(value, width + offset.x, height / 2 + offset.y, paint);
                    break;
            }
        }

        private void calculatePosition() {
            paint.getTextBounds(value, 0, value.length(), bounds);

            final int paddingLeft = padding;
            final int paddingRight = padding;
            final int paddingTop = padding;
            final int paddingBottom = padding;

            switch (direction) {
                case up:
                case down:
                    offset.x = -paddingLeft - bounds.width() - bounds.left;
                    if (direction == DragDirection.up) {
                        offset.y = paddingTop + fixedTextHeight;
                    } else {
                        offset.y = -paddingBottom;
                    }
                    break;
                case left:
                case right:
                    if (direction == DragDirection.left) {
                        offset.x = paddingLeft;
                    } else {
                        offset.x = -paddingRight - bounds.width();
                    }
                    offset.y = (paddingTop - paddingBottom) / 2 + fixedTextHeight / 2;
                    break;
            }
        }

        @NonNull
        public String getValue() {
            return visible ? value : "";
        }

        public void setValue(@NonNull String value) {
            if (TextUtils.equals(this.value, value)) {
                return;
            }
            this.value = value;
            invalidate(true);
        }

        public boolean hasValue() {
            return visible && !TextUtils.isEmpty(value);
        }
    }
}
