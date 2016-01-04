package org.solovyev.android.calculator.view;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import javax.annotation.Nonnull;

public class ScreenMetrics {

    @Nonnull
    private final android.view.Display display;
    @Nonnull
    private final DisplayMetrics metrics;
    private final int layout;
    private float xDpi;
    private float yDpi;
    private float diagonalIns;
    private float widthIns;
    private float heightIns;

    public ScreenMetrics(@Nonnull Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        initDpi();
        initDimensions();
        layout = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
    }

    private void initDpi() {
        if ((Build.DEVICE.equals("qsd8250_surf")
                || Build.MODEL.equals("Dell Streak"))) {
            xDpi = 190f;
            yDpi = 190f;
        } else if (Build.MODEL.equals("VTAB1008")) {
            xDpi = 160f;
            yDpi = 160f;
        } else if (Build.MODEL.equals("Dell Streak 7")) {
            xDpi = 150f;
            yDpi = 150f;
        } else if (Build.MODEL.equals("A1_07")) {
            xDpi = 127.5f;
            yDpi = 100f;
        } else if (Build.MODEL.equals("N12GPS")
                || Build.MODEL.equals("MID_Serials")) {
            xDpi = 133f;
            yDpi = 133f;
        } else if (Build.MODEL.startsWith("GT-N710")
                || Build.MODEL.equalsIgnoreCase("SCH-N719")
                || Build.MODEL.startsWith("SHV-E250")) {
            xDpi = 267f;
            yDpi = 267f;
        } else if (metrics.densityDpi - metrics.xdpi >= 79.0
                || metrics.densityDpi - metrics.ydpi >= 79.0
                || Math.abs(metrics.ydpi - metrics.xdpi) > 40.0) {
            xDpi = yDpi = metrics.densityDpi;
        } else {
            xDpi = metrics.xdpi;
            yDpi = metrics.ydpi;
        }
    }

    private void initDimensions() {
        final float w = getWidthPxs() / xDpi;
        final float h = getHeightPxs() / yDpi;
        diagonalIns = (float) Math.sqrt(w * w + h * h);
        widthIns = w;
        heightIns = h;
    }

    public boolean isInPortraitMode() {
        return getWidthPxs() < getHeightPxs();
    }

    @SuppressWarnings("deprecation")
    public int getWidthPxs() {
        return display.getWidth();
    }

    @SuppressWarnings("deprecation")
    public int getHeightPxs() {
        return display.getHeight();
    }

    public float getWidthIns() {
        return widthIns;
    }

    public float getHeightIns() {
        return heightIns;
    }

    public int getDensityDpi() {
        return metrics.densityDpi;
    }

    public float getDensity() {
        return metrics.density;
    }

    public float getXDpi() {
        return xDpi;
    }

    public float getYDpi() {
        return yDpi;
    }

    public float getDiagonalIns() {
        return diagonalIns;
    }

    public int getLayout() {
        return layout;
    }
}
