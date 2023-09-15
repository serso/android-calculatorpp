package org.solovyev.android.views.dragbutton;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DirectionText {

    static final float DEF_SCALE = 0.4f;
    private static final Rect TMP = new Rect();
    @NonNull
    private final DragDirection direction;
    @NonNull
    private final View view;
    private final float minTextSize;
    @NonNull
    private final PointF offset = new PointF(Integer.MIN_VALUE, Integer.MIN_VALUE);
    @NonNull
    private final PaintCache paintCache;
    @NonNull
    private PaintCache.Entry entry;
    @NonNull
    private String value = "";
    private boolean visible = true;
    private int padding;
    private float scale;
    private float baseTextSize;

    public DirectionText(@NonNull DragDirection direction, @NonNull View view,
            float minTextSize) {
        this.direction = direction;
        this.view = view;
        this.minTextSize = minTextSize;
        this.paintCache = PaintCache.get();
    }

    public void init(@Nullable TypedArray array, float defScale,
            int defColor, float defAlpha, int defPadding, @NonNull Typeface defTypeface,
            float textSize) {
        baseTextSize = textSize;
        if (array != null) {
            if (array.hasValue(direction.textAttr)) {
                value = nullToEmpty(array.getString(direction.textAttr));
            }
            padding = array.getDimensionPixelSize(direction.paddingAttr, defPadding);
            scale = array.getFloat(direction.scaleAttr, defScale);
        } else {
            value = "";
            scale = defScale;
            padding = defPadding;
        }
        final PaintCache.Spec spec = new PaintCache.Spec(defColor, defAlpha,
                defTypeface, scaledTextSize(textSize, scale), false);
        entry = paintCache.get(view.getContext(), spec);
    }

    @NonNull
    private String nullToEmpty(@Nullable String s) {
        return s == null ? "" : s;
    }

    private float scaledTextSize(float textSize, float scale) {
        return Math.max(textSize * scale, minTextSize);
    }

    public void setVisible(boolean visible) {
        if (this.visible == visible) {
            return;
        }
        this.visible = visible;
        invalidate(false);
    }

    private void invalidate(boolean remeasure) {
        view.invalidate();
        if (remeasure) {
            offset.set(Integer.MIN_VALUE, Integer.MIN_VALUE);
        }
    }

    public void setColor(int color) {
        setColor(color, entry.spec.alpha);
    }

    private void setColor(int color, float alpha) {
        if (entry.spec.color == color && entry.spec.alpha == alpha) {
            return;
        }
        entry = paintCache.get(view.getContext(), entry.spec.color(color, alpha));
        invalidate(false);
    }

    public void setAlpha(float alpha) {
        setColor(entry.spec.color, alpha);
    }

    void setHighContrast(boolean highContrast) {
        if (entry.spec.highContrast == highContrast) {
            return;
        }
        entry = paintCache.get(view.getContext(), entry.spec.highContrast(highContrast));
        invalidate(false);
    }

    public void setTypeface(@NonNull Typeface typeface) {
        if (entry.spec.typeface.equals(typeface)) {
            return;
        }
        entry = paintCache.get(view.getContext(), entry.spec.typeface(typeface));
        invalidate(true);
    }

    void draw(@NonNull Canvas canvas) {
        if (!hasValue()) {
            return;
        }
        if (offset.x == Integer.MIN_VALUE || offset.y == Integer.MIN_VALUE) {
            calculatePosition();
        }
        final int width = view.getWidth();
        final int height = view.getHeight();
        switch (direction) {
            case up:
                canvas.drawText(value, width + offset.x, offset.y, entry.paint);
                break;
            case down:
                canvas.drawText(value, width + offset.x, height + offset.y, entry.paint);
                break;
            case left:
                canvas.drawText(value, offset.x, height / 2 + offset.y, entry.paint);
                break;
            case right:
                canvas.drawText(value, width + offset.x, height / 2 + offset.y, entry.paint);
                break;
        }
    }

    boolean hasValue() {
        return visible && !TextUtils.isEmpty(value);
    }

    private void calculatePosition() {
        TMP.setEmpty();
        entry.paint.getTextBounds(value, 0, value.length(), TMP);

        final int paddingLeft = padding;
        final int paddingRight = padding;
        final int paddingTop = padding;
        final int paddingBottom = padding;

        switch (direction) {
            case up:
            case down:
                offset.x = -paddingLeft - TMP.width() - TMP.left;
                if (direction == DragDirection.up) {
                    offset.y = paddingTop + entry.getFixedTextHeight(scaledTextSize(baseTextSize, DEF_SCALE));
                } else {
                    offset.y = -paddingBottom;
                }
                break;
            case left:
            case right:
                if (direction == DragDirection.left) {
                    offset.x = paddingLeft;
                } else {
                    offset.x = -paddingRight - TMP.width();
                }
                offset.y = (paddingTop - paddingBottom) / 2 + entry.getFixedTextHeight(scaledTextSize(baseTextSize, DEF_SCALE)) / 2;
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

    public void setBaseTextSize(float baseTextSize) {
        if (this.baseTextSize == baseTextSize) {
            return;
        }
        this.baseTextSize = baseTextSize;
        entry = paintCache.get(view.getContext(), entry.spec.textSize(scaledTextSize(baseTextSize, scale)));
        invalidate(true);
    }
}
