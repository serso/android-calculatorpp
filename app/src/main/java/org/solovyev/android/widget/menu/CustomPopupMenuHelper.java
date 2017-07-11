/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.solovyev.android.widget.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.appcompat.R;
import android.support.v7.view.menu.ListMenuItemView;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuView;
import android.support.v7.view.menu.SubMenuBuilder;
import android.support.v7.widget.ListPopupWindow;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;

import org.solovyev.android.Check;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Presents a menu as a small, simple popup anchored to another view.
 */
@SuppressWarnings({"unused", "RestrictedApi"})
@SuppressLint("PrivateResource")
public class CustomPopupMenuHelper implements AdapterView.OnItemClickListener, View.OnKeyListener,
        ViewTreeObserver.OnGlobalLayoutListener, PopupWindow.OnDismissListener,
        MenuPresenter {

    private static final int DEFAULT_VIEW_TAG_KEY = org.solovyev.android.calculator.R.id.cpm_default_view_tag_key;
    private static final int[] COLOR_ATTRS = new int[]{R.attr.colorControlNormal};
    private static final Object DEFAULT_VIEW_TAG = new Object();

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final MenuBuilder mMenu;
    private final MenuAdapter mAdapter;
    private final boolean mOverflowOnly;
    private final int mPopupMaxWidth;
    private final int mPopupStyleAttr;
    private final int mPopupStyleRes;
    boolean mForceShowIcon;
    private View mAnchorView;
    private ListPopupWindow mPopup;
    private ViewTreeObserver mTreeObserver;
    private Callback mPresenterCallback;
    private ViewGroup mMeasureParent;
    private boolean mKeepOnSubMenu;

    /**
     * Whether the cached content width value is valid.
     */
    private boolean mHasContentWidth;

    /**
     * Cached content width from {@link #measureContentWidth}.
     */
    private int mContentWidth;

    private int mGravity = Gravity.NO_GRAVITY;

    public CustomPopupMenuHelper(Context context, MenuBuilder menu) {
        this(context, menu, null, false, R.attr.popupMenuStyle);
    }

    public CustomPopupMenuHelper(Context context, MenuBuilder menu, View anchorView) {
        this(context, menu, anchorView, false, R.attr.popupMenuStyle);
    }

    public CustomPopupMenuHelper(Context context, MenuBuilder menu, View anchorView,
                                 boolean overflowOnly, int popupStyleAttr) {
        this(context, menu, anchorView, overflowOnly, popupStyleAttr, 0);
    }

    public CustomPopupMenuHelper(Context context, MenuBuilder menu, View anchorView,
                                 boolean overflowOnly, int popupStyleAttr, int popupStyleRes) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mMenu = menu;
        mAdapter = new MenuAdapter(mMenu);
        mOverflowOnly = overflowOnly;
        mPopupStyleAttr = popupStyleAttr;
        mPopupStyleRes = popupStyleRes;

        final Resources res = context.getResources();
        mPopupMaxWidth = Math.max(res.getDisplayMetrics().widthPixels / 2,
                res.getDimensionPixelSize(R.dimen.abc_config_prefDialogWidth));

        mAnchorView = anchorView;

        // Present the menu using our context, not the menu builder's context.
        menu.addMenuPresenter(this, context);
    }

    static void tintMenuItem(@Nonnull MenuItemImpl item, @Nonnull ColorStateList tintColorStateList) {
        Drawable icon = item.getIcon();
        if (icon != null) {
            icon = DrawableCompat.wrap(icon);
            DrawableCompat.setTintList(icon, tintColorStateList);
            item.setIcon(icon);
        }
        if (item.hasSubMenu()) {
            final SubMenu subMenu = item.getSubMenu();
            for (int i = 0; i < subMenu.size(); i++) {
                final MenuItem subItem = subMenu.getItem(i);
                if (subItem instanceof MenuItemImpl) {
                    tintMenuItem((MenuItemImpl) subItem, tintColorStateList);
                }
            }
        }
    }

    @Nullable
    static ColorStateList getTintColorStateList(Context context) {
        final TypedArray a = context.obtainStyledAttributes(null, COLOR_ATTRS);
        try {
            return a.getColorStateList(0);
        } finally {
            a.recycle();
        }
    }

    static void tintMenuItems(Context context, Menu menu, int from, int to) {
        final ColorStateList tintColorStateList = getTintColorStateList(context);
        if (tintColorStateList == null) {
            return;
        }
        for (int i = from; i < to; i++) {
            final MenuItem item = menu.getItem(i);
            if (item instanceof MenuItemImpl) {
                tintMenuItem((MenuItemImpl) item, tintColorStateList);
            }
        }
    }

    public void setAnchorView(View anchor) {
        mAnchorView = anchor;
    }

    public void setForceShowIcon(boolean forceShow) {
        mForceShowIcon = forceShow;
    }

    public int getGravity() {
        return mGravity;
    }

    public void setGravity(int gravity) {
        mGravity = gravity;
    }

    public boolean isKeepOnSubMenu() {
        return mKeepOnSubMenu;
    }

    public void setKeepOnSubMenu(boolean keepOnSubMenu) {
        mKeepOnSubMenu = keepOnSubMenu;
    }

    public void show() {
        if (!tryShow()) {
            throw new IllegalStateException("MenuPopupHelper cannot be used without an anchor");
        }
    }

    public ListPopupWindow getPopup() {
        return mPopup;
    }

    public boolean tryShow() {
        mPopup = new ListPopupWindow(mContext, null, mPopupStyleAttr, mPopupStyleRes);
        mPopup.setOnDismissListener(this);
        mPopup.setOnItemClickListener(this);
        mPopup.setAdapter(mAdapter);
        mPopup.setModal(true);

        View anchor = mAnchorView;
        if (anchor != null) {
            final boolean addGlobalListener = mTreeObserver == null;
            mTreeObserver = anchor.getViewTreeObserver(); // Refresh to latest
            if (addGlobalListener) mTreeObserver.addOnGlobalLayoutListener(this);
            mPopup.setAnchorView(anchor);
            mPopup.setDropDownGravity(mGravity);
        } else {
            return false;
        }

        if (!mHasContentWidth) {
            mContentWidth = measureContentWidth();
            mHasContentWidth = true;
        }

        mPopup.setContentWidth(mContentWidth);
        mPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        mPopup.show();
        mPopup.getListView().setOnKeyListener(this);
        return true;
    }

    public void dismiss() {
        if (isShowing()) {
            mPopup.dismiss();
        }
    }

    public void onDismiss() {
        mPopup = null;
        mMenu.close();
        if (mTreeObserver != null) {
            if (!mTreeObserver.isAlive()) mTreeObserver = mAnchorView.getViewTreeObserver();
            //noinspection deprecation
            mTreeObserver.removeGlobalOnLayoutListener(this);
            mTreeObserver = null;
        }
    }

    public boolean isShowing() {
        return mPopup != null && mPopup.isShowing();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MenuAdapter adapter = mAdapter;
        adapter.mAdapterMenu.performItemAction(adapter.getItem(position), 0);
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_MENU) {
            dismiss();
            return true;
        }
        return false;
    }

    private int measureContentWidth() {
        // Menus don't tend to be long, so this is more sane than it looks.
        int maxWidth = 0;
        View itemView = null;
        int itemType = 0;

        final ListAdapter adapter = mAdapter;
        final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }

            if (mMeasureParent == null) {
                mMeasureParent = new FrameLayout(mContext);
            }

            itemView = adapter.getView(i, itemView, mMeasureParent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);

            final int itemWidth = itemView.getMeasuredWidth();
            if (itemWidth >= mPopupMaxWidth) {
                return mPopupMaxWidth;
            } else if (itemWidth > maxWidth) {
                maxWidth = itemWidth;
            }
        }

        return maxWidth;
    }

    @Override
    public void onGlobalLayout() {
        if (!isShowing()) {
            return;
        }
        final View anchor = mAnchorView;
        if (anchor == null || !anchor.isShown()) {
            dismiss();
        } else {
            // Recompute window size and position
            mPopup.show();
        }
    }

    @Override
    public void initForMenu(Context context, MenuBuilder menu) {
        // Don't need to do anything; we added as a presenter in the constructor.
    }

    @Override
    public MenuView getMenuView(ViewGroup root) {
        throw new UnsupportedOperationException("MenuPopupHelpers manage their own views");
    }

    @Override
    public void updateMenuView(boolean cleared) {
        mHasContentWidth = false;

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setCallback(Callback cb) {
        mPresenterCallback = cb;
    }

    @Override
    public boolean onSubMenuSelected(SubMenuBuilder subMenu) {
        if (subMenu.hasVisibleItems()) {
            CustomPopupMenuHelper subPopup = new CustomPopupMenuHelper(mContext, subMenu, mAnchorView, false, mPopupStyleAttr, mPopupStyleRes);
            subPopup.setGravity(mGravity);
            subPopup.setCallback(mPresenterCallback);
            subPopup.setKeepOnSubMenu(mKeepOnSubMenu);

            boolean preserveIconSpacing = false;
            final int count = subMenu.size();
            for (int i = 0; i < count; i++) {
                MenuItem childItem = subMenu.getItem(i);
                if (childItem.isVisible() && childItem.getIcon() != null) {
                    preserveIconSpacing = true;
                    break;
                }
            }
            subPopup.setForceShowIcon(preserveIconSpacing);

            if (subPopup.tryShow()) {
                if (mPresenterCallback != null) {
                    mPresenterCallback.onOpenSubMenu(subMenu);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        // Only care about the (sub)menu we're presenting.
        if (menu != mMenu) return;

        if (isKeepOnSubMenu() && !allMenusAreClosing) {
            return;
        }
        dismiss();
        if (mPresenterCallback != null) {
            mPresenterCallback.onCloseMenu(menu, allMenusAreClosing);
        }
    }

    @Override
    public boolean flagActionItems() {
        return false;
    }

    public boolean expandItemActionView(MenuBuilder menu, MenuItemImpl item) {
        return false;
    }

    public boolean collapseItemActionView(MenuBuilder menu, MenuItemImpl item) {
        return false;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return null;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
    }

    private class MenuAdapter extends BaseAdapter {
        private MenuBuilder mAdapterMenu;
        private int mExpandedIndex = -1;

        public MenuAdapter(MenuBuilder menu) {
            mAdapterMenu = menu;
            findExpandedIndex();
        }

        public int getCount() {
            ArrayList<MenuItemImpl> items = mOverflowOnly ?
                    mAdapterMenu.getNonActionItems() : mAdapterMenu.getVisibleItems();
            if (mExpandedIndex < 0) {
                return items.size();
            }
            return items.size() - 1;
        }

        public MenuItemImpl getItem(int position) {
            ArrayList<MenuItemImpl> items = mOverflowOnly ?
                    mAdapterMenu.getNonActionItems() : mAdapterMenu.getVisibleItems();
            if (mExpandedIndex >= 0 && position >= mExpandedIndex) {
                position++;
            }
            return items.get(position);
        }

        public long getItemId(int position) {
            // Since a menu item's ID is optional, we'll use the position as an
            // ID for the item in the AdapterView
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final MenuItemImpl item = getItem(position);
            final ActionProvider actionProvider = MenuItemCompat.getActionProvider(item);
            if (actionProvider != null) {
                final View actionView = actionProvider.onCreateActionView(item);
                fixLayoutParams(actionView, parent);
                return actionView;
            }
            final View actionView = MenuItemCompat.getActionView(item);
            if (actionView != null) {
                ((MenuView.ItemView) actionView).initialize(item, 0);
                fixLayoutParams(actionView, parent);
                return actionView;
            }
            return getDefaultView(item, convertView, parent);
        }

        private void fixLayoutParams(View actionView, ViewGroup parent) {
            if (parent instanceof FrameLayout) {
                // width measure pass, nothing to be done
                return;
            }
            Check.isTrue(parent instanceof AbsListView);
            final ViewGroup.LayoutParams lp = actionView.getLayoutParams();
            if (lp != null && !(lp instanceof AbsListView.LayoutParams)) {
                // see android.widget.AbsListView.generateDefaultLayoutParams()
                actionView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }

        @Nonnull
        private View getDefaultView(MenuItemImpl item, View convertView, ViewGroup parent) {
            if (convertView == null || convertView.getTag(DEFAULT_VIEW_TAG_KEY) != DEFAULT_VIEW_TAG) {
                convertView = mInflater.inflate(R.layout.abc_popup_menu_item_layout, parent, false);
                convertView.setTag(DEFAULT_VIEW_TAG_KEY, DEFAULT_VIEW_TAG);
            }

            final MenuView.ItemView itemView = (MenuView.ItemView) convertView;
            if (mForceShowIcon) {
                final ListMenuItemView listItemView = (ListMenuItemView) convertView;
                final boolean preserveIconSpacing = ListMenuItemViewCompat.getPreserveIconSpacing(listItemView);
                listItemView.setForceShowIcon(true);
                ListMenuItemViewCompat.setPreserveIconSpacing(listItemView, preserveIconSpacing);
            }
            itemView.initialize(item, 0);
            return convertView;
        }

        void findExpandedIndex() {
            final MenuItemImpl expandedItem = mMenu.getExpandedItem();
            if (expandedItem != null) {
                final ArrayList<MenuItemImpl> items = mMenu.getNonActionItems();
                final int count = items.size();
                for (int i = 0; i < count; i++) {
                    final MenuItemImpl item = items.get(i);
                    if (item == expandedItem) {
                        mExpandedIndex = i;
                        return;
                    }
                }
            }
            mExpandedIndex = -1;
        }

        @Override
        public void notifyDataSetChanged() {
            findExpandedIndex();
            super.notifyDataSetChanged();
        }

        public int indexOf(MenuItem item) {
            final List<MenuItemImpl> visibleItems = mMenu.getVisibleItems();
            for (int i = 0; i < visibleItems.size(); i++) {
                MenuItemImpl candidate = visibleItems.get(i);
                if (candidate == item) {
                    return i;
                }
            }
            return -1;
        }
    }
}

