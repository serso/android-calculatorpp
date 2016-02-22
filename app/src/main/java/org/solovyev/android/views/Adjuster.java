package org.solovyev.android.views;

import static android.graphics.Matrix.MSCALE_Y;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

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
        ViewTreeObserver treeObserver = getTreeObserver(view);
        if (treeObserver == null) {
            return;
        }
        treeObserver.addOnPreDrawListener(new ImageViewAdjuster(view, percentage));
    }

    private static class TextViewAdjuster implements ViewTreeObserver.OnPreDrawListener {
        @NonNull
        private final TextView view;
        private final float percentage;
        private final float minTextSizePxs;
        private int lastHeight;

        public TextViewAdjuster(@NonNull TextView view, float percentage, float minTextSizePxs) {
            this.view = view;
            this.percentage = percentage;
            this.minTextSizePxs = minTextSizePxs;
        }

        @Override
        public boolean onPreDraw() {
            final int height = view.getHeight();
            if (lastHeight == height || height <= 0) {
                return true;
            }
            lastHeight = height;
            final float oldTextSize = Math.round(view.getTextSize());
            final float newTextSize = Math.max(minTextSizePxs, Math.round(height * percentage));
            if (oldTextSize == newTextSize) {
                return true;
            }
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, newTextSize);
            return false;
        }
    }

    private static class ImageViewAdjuster implements ViewTreeObserver.OnPreDrawListener {
        @NonNull
        private final ImageView view;
        private final float percentage;
        private int lastHeight;

        public ImageViewAdjuster(@NonNull ImageView view, float percentage) {
            this.view = view;
            this.percentage = percentage;
        }

        @Override
        public boolean onPreDraw() {
            final int height = view.getHeight();
            if (lastHeight == height || height <= 0) {
                return true;
            }
            lastHeight = height;
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
