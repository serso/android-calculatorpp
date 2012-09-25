package org.solovyev.android.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.history.CalculatorHistoryFragment;
import org.solovyev.android.sherlock.tabs.ActionBarFragmentTabListener;

/**
 * User: serso
 * Date: 9/25/12
 * Time: 10:32 PM
 */
public class CalculatorActivityHelperImpl implements CalculatorActivityHelper {

    /*
    **********************************************************************
    *
    *                           CONSTANTS
    *
    **********************************************************************
    */
    private static final String SELECTED_NAV = "selected_nav";

    /*
    **********************************************************************
    *
    *                           FIELDS
    *
    **********************************************************************
    */

    private int layoutId;

    private boolean showActionBarTabs = true;

    private boolean homeIcon = false;

    public CalculatorActivityHelperImpl(int layoutId) {
        this.layoutId = layoutId;
    }

    public CalculatorActivityHelperImpl(int layoutId, boolean showActionBarTabs, boolean homeIcon) {
        this.layoutId = layoutId;
        this.showActionBarTabs = showActionBarTabs;
        this.homeIcon = homeIcon;
    }

    @Override
    public void onCreate(@NotNull final SherlockFragmentActivity activity, @Nullable Bundle savedInstanceState) {
        activity.setContentView(layoutId);

        final ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(homeIcon);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        if (showActionBarTabs) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            addTab(activity, "history", CalculatorHistoryFragment.class, null, R.string.c_history, R.drawable.icon);
            //addTab(activity, "messages", MessengerChatsFragment.class, null, R.string.c_messages, R.drawable.msg_footer_messages_icon);

            // settings tab
            final ActionBar.Tab tab = actionBar.newTab();
            tab.setTag("settings");
            tab.setText(R.string.c_settings);
            //tab.setIcon(R.drawable.msg_footer_settings_icon);
            tab.setTabListener(new ActionBar.TabListener() {
                @Override
                public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                    activity.startActivity(new Intent(activity.getApplicationContext(), CalculatorPreferencesActivity.class));
                }

                @Override
                public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                }

                @Override
                public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                }
            });
            actionBar.addTab(tab);

            int navPosition = -1;
            if (savedInstanceState != null) {
                navPosition = savedInstanceState.getInt(SELECTED_NAV, -1);
            }

            if (navPosition >= 0) {
                activity.getSupportActionBar().setSelectedNavigationItem(navPosition);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NotNull SherlockFragmentActivity activity, @NotNull Bundle outState) {
        outState.putInt(SELECTED_NAV, activity.getSupportActionBar().getSelectedNavigationIndex());
    }

    private void addTab(@NotNull SherlockFragmentActivity activity,
                        @NotNull String tag,
                        @NotNull Class<? extends Fragment> fragmentClass,
                        @Nullable Bundle fragmentArgs,
                        int captionResId,
                        int iconResId) {
        final ActionBar actionBar = activity.getSupportActionBar();
        final ActionBar.Tab tab = actionBar.newTab();
        tab.setTag(tag);
        tab.setText(captionResId);
        //tab.setIcon(iconResId);
        tab.setTabListener(new ActionBarFragmentTabListener(activity, tag, fragmentClass, fragmentArgs, R.id.content_second_pane));
        actionBar.addTab(tab);
    }
}
