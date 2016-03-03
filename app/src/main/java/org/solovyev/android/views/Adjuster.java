package org.solovyev.android.views;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import static android.graphics.Matrix.MSCALE_Y;

public class Adjuster {

    private static final float[] MATRIX = new float[9];

    public static void adjustText(@NonNull final TextView view, final float percentage) {
        adjustText(view, percentage, 0);
    }

    public static void adjustText(@NonNull final TextView view, final float percentage, final float minTextSizePxs) {
        ViewTreeObserver treeObserver = getTreeObserver(view);
        if (treeObserver == null) {
            return;
        }
        treeObserver.addOnPreDrawListener(new TextViewAdjuster(view, percentage, minTextSizePxs));
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

    private static abstract class BaseViewAdjuster<V extends View> implements ViewTreeObserver.OnPreDrawListener {
        @NonNull
        protected final V view;

        protected BaseViewAdjuster(@NonNull V view) {
            this.view = view;
        }

        @Override
        public final boolean onPreDraw() {
            final int width = view.getWidth();
            final int height = view.getHeight();
            if (!ViewCompat.isLaidOut(view) || height <= 0 || width <= 0) {
                return true;
            }
            final ViewTreeObserver treeObserver = getTreeObserver(view);
            if (treeObserver != null) {
                treeObserver.removeOnPreDrawListener(this);
            }
            return adjust(width, height);
        }

        protected abstract boolean adjust(int width, int height);
    }

    private static class TextViewAdjuster extends BaseViewAdjuster<TextView> {
        private final float percentage;
        private final float minTextSizePxs;

        public TextViewAdjuster(@NonNull TextView view, float percentage, float minTextSizePxs) {
            super(view);
            this.percentage = percentage;
            this.minTextSizePxs = minTextSizePxs;
        }

        @Override
        protected boolean adjust(int width, int height) {
            final float oldTextSize = Math.round(view.getTextSize());
            final float newTextSize = Math.max(minTextSizePxs, Math.round(height * percentage));
            if (oldTextSize == newTextSize) {
                return true;
            }
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, newTextSize);
            return false;
        }
    }

    private static class MaxWidthAdjuster extends BaseViewAdjuster<View> {

        private final int maxWidth;

        public MaxWidthAdjuster(@NonNull View view, int maxWidth) {
            super(view);
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
            super(view);
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
