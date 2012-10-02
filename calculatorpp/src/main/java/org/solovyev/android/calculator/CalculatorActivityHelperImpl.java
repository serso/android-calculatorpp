package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.AndroidUtils;
import org.solovyev.android.sherlock.tabs.ActionBarFragmentTabListener;

import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 9/25/12
 * Time: 10:32 PM
 */
public class CalculatorActivityHelperImpl extends AbstractCalculatorHelper implements CalculatorActivityHelper {

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


    public CalculatorActivityHelperImpl(int layoutId, @NotNull String logTag) {
        super(logTag);
        this.layoutId = layoutId;
    }

    public CalculatorActivityHelperImpl(int layoutId, boolean homeIcon) {
        this.layoutId = layoutId;
        this.homeIcon = homeIcon;
    }

    @Override
    public void onCreate(@NotNull Activity activity, @Nullable Bundle savedInstanceState) {
        super.onCreate(activity);

        if ( activity instanceof CalculatorEventListener) {
            CalculatorLocatorImpl.getInstance().getCalculator().addCalculatorEventListener((CalculatorEventListener)activity);
        }

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);

        this.theme = CalculatorPreferences.Gui.getTheme(preferences);
        activity.setTheme(this.theme.getThemeId());

        activity.setContentView(layoutId);

        final View root = activity.findViewById(R.id.main_layout);
        if (root != null) {
            processButtons(activity, root);
        } else {
            Log.e(CalculatorActivityHelperImpl.class.getSimpleName(), "Root is null for " + activity.getClass().getName());
        }

        if (savedInstanceState != null) {
            navPosition = savedInstanceState.getInt(SELECTED_NAV, 0);
        }
    }

    @Override
    public void onCreate(@NotNull final SherlockFragmentActivity activity, @Nullable Bundle savedInstanceState) {
        this.onCreate((Activity) activity, savedInstanceState);

        final ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(homeIcon);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);

        if (activity instanceof CalculatorActivity) {
            if ( AndroidUtils.getScreenOrientation(activity) == Configuration.ORIENTATION_PORTRAIT ) {
                actionBar.setDisplayShowTitleEnabled(true);
            } else {
            }
        } else {
            actionBar.setDisplayShowTitleEnabled(true);
        }
        actionBar.setIcon(R.drawable.icon_action_bar);
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
    public void onDestroy(@NotNull Activity activity) {
        super.onDestroy(activity);

        if ( activity instanceof CalculatorEventListener) {
            CalculatorLocatorImpl.getInstance().getCalculator().removeCalculatorEventListener((CalculatorEventListener)activity);
        }
    }

    @Override
    public void onDestroy(@NotNull SherlockFragmentActivity activity) {
        this.onDestroy((Activity)activity);
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

        final ActionBarFragmentTabListener listener = new ActionBarFragmentTabListener(activity, tag, fragmentClass, fragmentArgs, parentViewId);
        tab.setTabListener(listener);
        actionBar.addTab(tab);

        fragmentTags.add(tag);

        restoreSavedTab(activity);
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
