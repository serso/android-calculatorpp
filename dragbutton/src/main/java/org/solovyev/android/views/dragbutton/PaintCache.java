package org.solovyev.android.views.dragbutton;

import static android.graphics.Color.BLACK;
import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.util.TypedValue.applyDimension;
import static org.solovyev.android.views.dragbutton.DirectionTextView.SHADOW_RADIUS_DPS;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import java.util.HashMap;
import java.util.Map;

class PaintCache {

    @NonNull
    private static final Rect TMP = new Rect();
    private static final String TAG = "PaintCache";
    @NonNull
    private static final PaintCache INSTANCE = new PaintCache();

    static class Entry {
        @NonNull
        public final Spec spec;
        @NonNull
        public final Paint paint;
        private float lastTextSize;
        private float fixedTextHeight;

        Entry(@NonNull Spec spec, @NonNull Paint paint) {
            this.spec = spec;
            this.paint = paint;
        }

        float getFixedTextHeight(float textSize) {
            if (lastTextSize == textSize) {
                return fixedTextHeight;
            }
            if (lastTextSize != 0) {
                Log.d(TAG, "Remeasuring text for size: " + textSize);
            }
            final float oldTextSize = paint.getTextSize();
            paint.setTextSize(textSize);
            TMP.setEmpty();
            paint.getTextBounds("|", 0, 1, TMP);
            paint.setTextSize(oldTextSize);
            lastTextSize = textSize;
            fixedTextHeight = TMP.height();
            return fixedTextHeight;
        }
    }

    static class Spec {
        @ColorInt
        public final int color;
        public final float alpha;
        @NonNull
        public final Typeface typeface;
        public final float textSize;
        public final boolean highContrast;

        Spec(int color, float alpha, @NonNull Typeface typeface,
                float textSize,
                boolean highContrast) {
            this.color = color;
            this.alpha = alpha;
            this.typeface = typeface;
            this.textSize = textSize;
            this.highContrast = highContrast;
        }

        private int contrastColor(@NonNull Context context) {
            final int colorRes =
                    isLightColor(color) ? R.color.drag_button_text : R.color.drag_text_inverse;
            return ContextCompat.getColor(context, colorRes);
        }

        private static boolean isLightColor(@ColorInt int color) {
            return ColorUtils.calculateLuminance(color) > 0.5f;
        }

        private int intAlpha() {
            return (int) (255 * alpha);
        }

        private boolean needsShadow() {
            return needsShadow(color);
        }

        public static boolean needsShadow(@ColorInt int color) {
            return isLightColor(color);
        }

        @Override
        public int hashCode() {
            int result = color;
            result = 31 * result + (alpha != +0.0f ? Float.floatToIntBits(alpha) : 0);
            result = 31 * result + typeface.hashCode();
            result = 31 * result + (textSize != +0.0f ? Float.floatToIntBits(textSize) : 0);
            result = 31 * result + (highContrast ? 1 : 0);
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final Spec spec = (Spec) o;

            if (color != spec.color) return false;
            if (Float.compare(spec.alpha, alpha) != 0) return false;
            if (Float.compare(spec.textSize, textSize) != 0) return false;
            if (highContrast != spec.highContrast) return false;
            return typeface.equals(spec.typeface);

        }

        @NonNull
        public Spec highContrast(boolean highContrast) {
            return new Spec(color, alpha, typeface, textSize, highContrast);
        }

        @NonNull
        public Spec color(int color, float alpha) {
            return new Spec(color, alpha, typeface, textSize, highContrast);
        }

        @NonNull
        public Spec typeface(@NonNull Typeface typeface) {
            return new Spec(color, alpha, typeface, textSize, highContrast);
        }

        @NonNull
        public Spec textSize(float textSize) {
            return new Spec(color, alpha, typeface, textSize, highContrast);
        }
    }

    private float shadowRadius;
    @NonNull
    private final Map<Spec, Entry> map = new HashMap<>();

    private void lazyLoad(@NonNull Context context) {
        if (shadowRadius != 0) {
            return;
        }
        final Resources res = context.getResources();
        shadowRadius = applyDimension(COMPLEX_UNIT_DIP, SHADOW_RADIUS_DPS,
                res.getDisplayMetrics());
    }

    @NonNull
    public static PaintCache get() {
        return INSTANCE;
    }

    @NonNull
    public Entry get(@NonNull Context context, @NonNull Spec spec) {
        lazyLoad(context);
        Entry entry = map.get(spec);
        if (entry == null) {
            entry = new Entry(spec, makePaint(context, spec));
            map.put(spec, entry);
        }
        return entry;
    }

    @NonNull
    private Paint makePaint(@NonNull Context context, @NonNull Spec spec) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        if (spec.highContrast) {
            paint.setColor(spec.contrastColor(context));
            paint.setAlpha(255);
        } else {
            paint.setColor(spec.color);
            paint.setAlpha(spec.intAlpha());
        }
        if (spec.typeface.getStyle() != Typeface.NORMAL) {
            paint.setTypeface(Typeface.create(spec.typeface, Typeface.NORMAL));
        } else {
            paint.setTypeface(spec.typeface);
        }
        paint.setTextSize(spec.textSize);
        setHighContrast(paint, spec.highContrast, spec.color);
        return paint;
    }

    public static void setHighContrast(@NonNull Paint paint, boolean highContrast, @ColorInt int color) {
        if (highContrast && Spec.needsShadow(color)) {
            paint.setShadowLayer(get().shadowRadius, 0, 0, BLACK);
        } else {
            paint.clearShadowLayer();
        }
    }
}
