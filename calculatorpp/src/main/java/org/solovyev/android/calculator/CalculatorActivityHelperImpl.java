package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.AndroidUtils;
import org.solovyev.android.calculator.about.CalculatorFragmentType;
import org.solovyev.android.sherlock.tabs.ActionBarFragmentTabListener;

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
    private CalculatorPreferences.Gui.Theme theme;

    private int selectedNavigationIndex = 0;

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

        if (activity instanceof CalculatorEventListener) {
            CalculatorLocatorImpl.getInstance().getCalculator().addCalculatorEventListener((CalculatorEventListener) activity);
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
    }

    @Override
    public void onCreate(@NotNull final SherlockFragmentActivity activity, @Nullable Bundle savedInstanceState) {
        this.onCreate((Activity) activity, savedInstanceState);

        final ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(homeIcon);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);

        toggleTitle(activity, true);

        actionBar.setIcon(R.drawable.icon_action_bar);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }

    private void toggleTitle(@NotNull SherlockFragmentActivity activity, boolean showTitle) {
        final ActionBar actionBar = activity.getSupportActionBar();

        if (activity instanceof CalculatorActivity) {
            if (AndroidUtils.getScreenOrientation(activity) == Configuration.ORIENTATION_PORTRAIT) {
                actionBar.setDisplayShowTitleEnabled(true);
            } else {
                actionBar.setDisplayShowTitleEnabled(false);
            }
        } else {
            actionBar.setDisplayShowTitleEnabled(showTitle);
        }
    }

    public void restoreSavedTab(@NotNull SherlockFragmentActivity activity) {
        final ActionBar actionBar = activity.getSupportActionBar();
        if (selectedNavigationIndex >= 0 && selectedNavigationIndex < actionBar.getTabCount()) {
            actionBar.setSelectedNavigationItem(selectedNavigationIndex);
        }
    }

    @Override
    public void onSaveInstanceState(@NotNull SherlockFragmentActivity activity, @NotNull Bundle outState) {
        onSaveInstanceState((Activity) activity, outState);
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
    public void onPause(@NotNull Activity activity) {
    }

    @Override
    public void onPause(@NotNull SherlockFragmentActivity activity) {
        onPause((Activity) activity);

        final int selectedNavigationIndex = activity.getSupportActionBar().getSelectedNavigationIndex();
        if (selectedNavigationIndex >= 0) {
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(getSavedTabPreferenceName(activity), selectedNavigationIndex);
            editor.commit();
        }

    }

    @NotNull
    private String getSavedTabPreferenceName(@NotNull Activity activity) {
        return "tab_" + activity.getClass().getSimpleName();
    }

    @Override
    public void onDestroy(@NotNull Activity activity) {
        super.onDestroy(activity);

        if (activity instanceof CalculatorEventListener) {
            CalculatorLocatorImpl.getInstance().getCalculator().removeCalculatorEventListener((CalculatorEventListener) activity);
        }
    }

    @Override
    public void onDestroy(@NotNull SherlockFragmentActivity activity) {
        this.onDestroy((Activity) activity);
    }

    @Override
    public void addTab(@NotNull SherlockFragmentActivity activity,
                       @NotNull String tag,
                       @NotNull Class<? extends Fragment> fragmentClass,
                       @Nullable Bundle fragmentArgs,
                       int captionResId,
                       int parentViewId) {
        final ActionBar actionBar = activity.getSupportActionBar();

        final ActionBar.Tab tab = actionBar.newTab();
        tab.setTag(tag);
        tab.setText(captionResId);

        final ActionBarFragmentTabListener listener = new ActionBarFragmentTabListener(activity, tag, fragmentClass, fragmentArgs, parentViewId);
        tab.setTabListener(listener);
        actionBar.addTab(tab);
    }

    @Override
    public void addTab(@NotNull SherlockFragmentActivity activity, @NotNull CalculatorFragmentType fragmentType, @Nullable Bundle fragmentArgs, int parentViewId) {
        addTab(activity, fragmentType.getFragmentTag(), fragmentType.getFragmentClass(), fragmentArgs, fragmentType.getDefaultTitleResId(), parentViewId);
    }

    @Override
    public void setFragment(@NotNull SherlockFragmentActivity activity, @NotNull CalculatorFragmentType fragmentType, @Nullable Bundle fragmentArgs, int parentViewId) {
        final FragmentManager fm = activity.getSupportFragmentManager();

        Fragment fragment = fm.findFragmentByTag(fragmentType.getFragmentTag());
        if (fragment == null) {
            fragment = Fragment.instantiate(activity, fragmentType.getFragmentClass().getName(), fragmentArgs);
            final FragmentTransaction ft = fm.beginTransaction();
            ft.add(parentViewId, fragment, fragmentType.getFragmentTag());
            ft.commit();
        } else {
            if ( fragment.isDetached() ) {
                final FragmentTransaction ft = fm.beginTransaction();
                ft.attach(fragment);
                ft.commit();
            }

        }
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

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        selectedNavigationIndex = preferences.getInt(getSavedTabPreferenceName(activity), -1);
        restoreSavedTab(activity);
    }
}
