package org.solovyev.android.widget.menu;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.view.menu.ListMenuItemView;
import android.util.Log;

import java.lang.reflect.Field;

final class ListMenuItemViewCompat {

    @Nullable
    private static Field preserveIconSpacingField;

    public static void setPreserveIconSpacing(@NonNull ListMenuItemView view, boolean preserveIconSpacing) {
        final Field field = getPreserveIconSpacingField();
        if (field == null) {
            return;
        }
        try {
            field.set(view, preserveIconSpacing);
        } catch (IllegalAccessException e) {
            Log.e("CustomListMenuItemView", e.getMessage(), e);
        }
    }

    public static boolean getPreserveIconSpacing(@NonNull ListMenuItemView view) {
        final Field field = getPreserveIconSpacingField();
        if (field == null) {
            return false;
        }
        try {
            return field.getBoolean(view);
        } catch (IllegalAccessException e) {
            Log.e("CustomListMenuItemView", e.getMessage(), e);
            return false;
        }
    }

    @Nullable
    private static Field getPreserveIconSpacingField() {
        if (preserveIconSpacingField != null) {
            return preserveIconSpacingField;
        }
        try {
            preserveIconSpacingField = ListMenuItemView.class.getDeclaredField("mPreserveIconSpacing");
            preserveIconSpacingField.setAccessible(true);
            return preserveIconSpacingField;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
