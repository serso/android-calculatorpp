package org.solovyev.android.widget.menu;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuView;
import android.util.AttributeSet;
import android.view.View;

@SuppressWarnings("unused")
public class MenuItemDivider extends View implements MenuView.ItemView {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    public MenuItemDivider(Context context) {
        super(context);
    }

    public MenuItemDivider(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuItemDivider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MenuItemDivider(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void initialize(MenuItemImpl itemData, int menuType) {
        final Drawable divider = getDivider();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(divider);
        } else {
            //noinspection deprecation
            setBackgroundDrawable(divider);
        }
        setEnabled(false);
    }

    @Nullable
    private Drawable getDivider() {
        final TypedArray a = getContext().obtainStyledAttributes(ATTRS);
        try {
            return a.getDrawable(0);
        } finally {
            a.recycle();
        }
    }

    @Override
    public MenuItemImpl getItemData() {
        return null;
    }

    @Override
    public void setTitle(CharSequence title) {

    }

    @Override
    public void setEnabled(boolean enabled) {

    }

    @Override
    public void setCheckable(boolean checkable) {

    }

    @Override
    public void setChecked(boolean checked) {

    }

    @Override
    public void setShortcut(boolean showShortcut, char shortcutKey) {

    }

    @Override
    public void setIcon(Drawable icon) {

    }

    @Override
    public boolean prefersCondensedTitle() {
        return false;
    }

    @Override
    public boolean showsIcon() {
        return false;
    }
}
