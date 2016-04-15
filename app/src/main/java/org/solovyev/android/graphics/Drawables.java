package org.solovyev.android.graphics;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;

import org.solovyev.android.calculator.R;

public final class Drawables {
    private static final int[] COLOR_ATTRS = new int[]{R.attr.colorControlNormal};

    private Drawables() {
    }

    /**
     * Applies default tinting according to theme's colorControlNormal attribute
     *
     * @param context context from which styles are used
     * @param icon    icon to be tinted, must be black
     * @return tinted version of icon
     */
    public static Drawable tint(Context context, Drawable icon) {
        final ColorStateList colorStateList = getTintColorStateList(context);
        if (colorStateList == null) {
            return icon;
        }
        return tint(icon, colorStateList);
    }

    @Nullable
    private static ColorStateList getTintColorStateList(Context context) {
        final TypedArray a = context.obtainStyledAttributes(null, COLOR_ATTRS);
        try {
            return a.getColorStateList(0);
        } finally {
            a.recycle();
        }
    }

    /**
     * Applies tinting according to specified colorStateList
     *
     * @param icon           icon to be tinted, must be black
     * @param colorStateList list of tint colors in different states
     * @return tinted version of icon
     */
    public static Drawable tint(Drawable icon, ColorStateList colorStateList) {
        icon = DrawableCompat.wrap(icon);
        icon.mutate();
        DrawableCompat.setTintList(icon, colorStateList);
        return icon;
    }
}
