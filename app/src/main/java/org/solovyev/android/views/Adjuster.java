package org.solovyev.android.views;

import static android.graphics.Matrix.MSCALE_Y;

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

public class Adjuster {

    private static final float[] MATRIX = new float[9];
    @NonNull
    private static final Helper<TextView> textViewHelper = new Helper<TextView>() {
        @Override
        public void apply(@NonNull TextView view, float textSize) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }

        @Override
        public float getTextSize(@NonNull TextView view) {
            return view.getTextSize();
        }
    };

    public static void adjustText(@NonNull final TextView view, final float percentage) {
        adjustText(view, textViewHelper, percentage, 0);
    }

    public static void adjustText(@NonNull final TextView view, final float percentage, int minTextSizePxs) {
        adjustText(view, textViewHelper, percentage, minTextSizePxs);
    }

    public static <V extends View> void adjustText(@NonNull final V view, @NonNull Helper<V> helper, final float percentage, final float minTextSizePxs) {
        ViewTreeObserver treeObserver = getTreeObserver(view);
        if (treeObserver == null) {
            return;
        }
        treeObserver.addOnPreDrawListener(new TextViewAdjuster<V>(view, helper, percentage, minTextSizePxs));
    }

    @Nullable
    public static ViewTreeObserver getTreeObserver(@NonNull View view) {
        final ViewTreeObserver treeObserver = view.getViewTreeObserver();
        if (treeObserver == null) {
            return null;
        }
        if (!treeObserver.isAlive()) {
            return null;
        }
        return treeObserver;
    }

    public static void adjustImage(@NonNull final ImageView view, final float percentage) {
        final ViewTreeObserver treeObserver = getTreeObserver(view);
        if (treeObserver == null) {
            return;
        }
        treeObserver.addOnPreDrawListener(new ImageViewAdjuster(view, percentage));
    }

    public static void maxWidth(@NonNull View view, int maxWidth) {
        final ViewTreeObserver treeObserver = getTreeObserver(view);
        if (treeObserver == null) {
            return;
        }
        treeObserver.addOnPreDrawListener(new MaxWidthAdjuster(view, maxWidth));
    }

    public interface Helper<V extends View> {
        void apply(@NonNull V view, float textSize);

        float getTextSize(@NonNull V view);
    }

    private static abstract class BaseViewAdjuster<V extends View> implements ViewTreeObserver.OnPreDrawListener {
        @NonNull
        protected final V view;
        protected final boolean oneShot;
        private int usedWidth;
        private int usedHeight;

        protected BaseViewAdjuster(@NonNull V view, boolean oneShot) {
            this.view = view;
            this.oneShot = oneShot;
        }

        @Override
        public final boolean onPreDraw() {
            final int width = view.getWidth();
            final int height = view.getHeight();
            if (!ViewCompat.isLaidOut(view) || height <= 0 || width <= 0) {
                return true;
            }
            if (usedWidth == width && usedHeight == height) {
                return true;
            }
            usedWidth = width;
            usedHeight = height;
            if (oneShot) {
                final ViewTreeObserver treeObserver = getTreeObserver(view);
                if (treeObserver != null) {
                    treeObserver.removeOnPreDrawListener(this);
                }
            }
            return adjust(width, height);
        }

        protected abstract boolean adjust(int width, int height);
    }

    private static class TextViewAdjuster<V extends View> extends BaseViewAdjuster<V> {
        private final float percentage;
        private final float minTextSizePxs;
        private final Helper<V> helper;

        public TextViewAdjuster(@NonNull V view, @NonNull Helper<V> helper, float percentage, float minTextSizePxs) {
            super(view, true);
            this.helper = helper;
            this.percentage = percentage;
            this.minTextSizePxs = minTextSizePxs;
        }

        @Override
        protected boolean adjust(int width, int height) {
            final float oldTextSize = Math.round(helper.getTextSize(view));
            final float newTextSize = Math.max(minTextSizePxs, Math.round(height * percentage));
            if (oldTextSize == newTextSize) {
                return true;
            }
            helper.apply(view, newTextSize);
            return false;
        }
    }

    private static class MaxWidthAdjuster extends BaseViewAdjuster<View> {

        private final int maxWidth;

        public MaxWidthAdjuster(@NonNull View view, int maxWidth) {
            super(view, true);
            this.maxWidth = maxWidth;
        }

        @Override
        protected boolean adjust(int width, int height) {
            if (width <= maxWidth) {
                return true;
            }
            final ViewGroup.LayoutParams lp = view.getLayoutParams();
            lp.width = maxWidth;
            view.setLayoutParams(lp);
            return false;
        }
    }

    private static class ImageViewAdjuster extends BaseViewAdjuster<ImageView> {
        private final float percentage;

        public ImageViewAdjuster(@NonNull ImageView view, float percentage) {
            super(view, false);
            this.percentage = percentage;
        }

        @Override
        protected boolean adjust(int width, int height) {
            final Drawable d = view.getDrawable();
            if (d == null) {
                return true;
            }
            view.getImageMatrix().getValues(MATRIX);
            final int oldImageHeight = Math.round(d.getIntrinsicHeight() * MATRIX[MSCALE_Y]);
            final int newImageHeight = Math.round(height * percentage);
            if (oldImageHeight == newImageHeight) {
                return true;
            }
            final int newPaddings = Math.max(0, height - newImageHeight) / 2;
            view.setPadding(0, newPaddings, 0, newPaddings);

            return false;
        }
    }
}
