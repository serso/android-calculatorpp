package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.AndroidUtils;
import org.solovyev.android.fragments.FragmentUtils;
import org.solovyev.android.sherlock.tabs.ActionBarFragmentTabListener;

import java.util.ArrayList;
import java.util.List;

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

    private boolean homeIcon = false;

    @NotNull
    private final List<String> fragmentTags = new ArrayList<String>();

    @NotNull
    private CalculatorPreferences.Gui.Theme theme;
    private int navPosition = 0;

    public CalculatorActivityHelperImpl(int layoutId) {
        this.layoutId = layoutId;
    }

    public CalculatorActivityHelperImpl(int layoutId, boolean homeIcon) {
        this.layoutId = layoutId;
        this.homeIcon = homeIcon;
    }

    @Override
    public void onCreate(@NotNull Activity activity, @Nullable Bundle savedInstanceState) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);

        this.theme = CalculatorPreferences.Gui.getTheme(preferences);
        activity.setTheme(this.theme.getThemeId());

        activity.setContentView(layoutId);
    }

    @Override
    public void onCreate(@NotNull final SherlockFragmentActivity activity, @Nullable Bundle savedInstanceState) {
        this.onCreate((Activity) activity, savedInstanceState);

        final ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(homeIcon);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        if (savedInstanceState != null) {
            navPosition = savedInstanceState.getInt(SELECTED_NAV, 0);
        }
    }

    @Override
    public void restoreSavedTab(@NotNull SherlockFragmentActivity activity) {
        final ActionBar actionBar = activity.getSupportActionBar();
        if (navPosition >= 0 && navPosition < actionBar.getTabCount()) {
            activity.getSupportActionBar().setSelectedNavigationItem(navPosition);
        }
    }

    @Override
    public void onSaveInstanceState(@NotNull SherlockFragmentActivity activity, @NotNull Bundle outState) {
        FragmentUtils.detachFragments(activity, fragmentTags);

        onSaveInstanceState((Activity) activity, outState);
        outState.putInt(SELECTED_NAV, activity.getSupportActionBar().getSelectedNavigationIndex());
    }

    @Override
    public void onSaveInstanceState(@NotNull Activity activity, @NotNull Bundle outState) {
    }

    @Override
    public void onResume(@NotNull Activity activity) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);

        final CalculatorPreferences.Gui.Theme newTheme = CalculatorPreferences.Gui.theme.getPreference(preferences);
        if (!theme.equals(newTheme)) {
            AndroidUtils.restartActivity(activity);
        }
    }

    @Override
    public void addTab(@NotNull SherlockFragmentActivity activity,
                       @NotNull String tag,
                       @NotNull Class<? extends Fragment> fragmentClass,
                       @Nullable Bundle fragmentArgs,
                       int captionResId,
                       int parentViewId) {
        activity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        final ActionBar actionBar = activity.getSupportActionBar();
        final ActionBar.Tab tab = actionBar.newTab();
        tab.setTag(tag);
        tab.setText(captionResId);
        tab.setTabListener(new ActionBarFragmentTabListener(activity, tag, fragmentClass, fragmentArgs, parentViewId));
        actionBar.addTab(tab);

        fragmentTags.add(tag);
    }

    @Override
    public int getLayoutId() {
        return layoutId;
    }

    @Override
    @NotNull
    public CalculatorPreferences.Gui.Theme getTheme() {
        return theme;
    }

    @Override
    public void onResume(@NotNull SherlockFragmentActivity activity) {
        onResume((Activity) activity);
    }
}
