package org.solovyev.android.calculator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.view.ActionProvider;
import android.support.v7.view.menu.ListMenuItemView;
import android.support.v7.view.menu.MenuItemImpl;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.solovyev.android.Check;
import org.solovyev.android.widget.menu.CustomPopupMenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

final class MainMenu {

    @NonNull
    private CalculatorActivity activity;
    @Nullable
    private CustomPopupMenu popup;

    public MainMenu(@Nonnull CalculatorActivity activity) {
        this.activity = activity;
    }

    public void toggle() {
        if (popup == null) {
            popup = new CustomPopupMenu(activity, activity.mainMenuButton);
            popup.inflate(R.menu.main);
            final int menuButtonHeight = activity.mainMenuButton.getLayoutParams().height;
            Check.isTrue(menuButtonHeight > 0);
            popup.setVerticalOffset(menuButtonHeight);
            popup.setOnMenuItemClickListener(activity);
        }
        if (popup.isShowing()) {
            popup.dismiss();
        } else {
            popup.show();
        }
    }

    public static final class ViewProvider extends ActionProvider {

        @Nonnull
        private final LayoutInflater inflater;

        @Inject
        SharedPreferences preferences;

        public ViewProvider(Context context) {
            super(context);
            inflater = LayoutInflater.from(context);
            App.cast(context).getComponent().inject(this);
        }

        @Override
        public View onCreateActionView() {
            throw new UnsupportedOperationException();
        }

        @Override
        public View onCreateActionView(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_mode:
                    return makeModeView(menuItem);
            }
            throw new IllegalArgumentException("Can't create a view for menu item: " + menuItem);
        }

        @Nonnull
        private View makeModeView(@Nonnull MenuItem menuItem) {
            final ViewGroup view = makeDefaultView(menuItem);
            final TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
            final Preferences.Gui.Mode mode = Preferences.Gui.mode.getPreference(preferences);
            subtitle.setText(getContext().getString(mode.name));
            return view;
        }

        @SuppressLint("InflateParams")
        private ViewGroup makeDefaultView(@Nonnull MenuItem menuItem) {
            final ViewGroup view = (ViewGroup) inflater.inflate(R.layout.popup_menu_item_layout, null);
            final ListMenuItemView listItemView = (ListMenuItemView) view.findViewById(R.id.menu_list_item_view);
            listItemView.initialize((MenuItemImpl) menuItem, 0);
            return view;
        }
    }
}
