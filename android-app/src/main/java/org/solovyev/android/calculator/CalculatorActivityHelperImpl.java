package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.android.Activities;
import org.solovyev.android.Views;
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

	@Nonnull
	private CalculatorPreferences.Gui.Theme theme;

	@Nonnull
	private CalculatorPreferences.Gui.Layout layout;

	private int selectedNavigationIndex = 0;

	public CalculatorActivityHelperImpl(int layoutId, @Nonnull String logTag) {
		super(logTag);
		this.layoutId = layoutId;
	}

	public CalculatorActivityHelperImpl(int layoutId, boolean homeIcon) {
		this.layoutId = layoutId;
		this.homeIcon = homeIcon;
	}

	@Override
	public void onCreate(@Nonnull Activity activity, @Nullable Bundle savedInstanceState) {
		super.onCreate(activity);

		if (activity instanceof CalculatorEventListener) {
			Locator.getInstance().getCalculator().addCalculatorEventListener((CalculatorEventListener) activity);
		}

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);

		this.theme = CalculatorPreferences.Gui.getTheme(preferences);
		activity.setTheme(this.theme.getThemeId());

		this.layout = CalculatorPreferences.Gui.getLayout(preferences);

		activity.setContentView(layoutId);

		final View root = activity.findViewById(R.id.main_layout);
		if (root != null) {
			processButtons(activity, root);
			addHelpInfo(activity, root);
		} else {
			Log.e(CalculatorActivityHelperImpl.class.getSimpleName(), "Root is null for " + activity.getClass().getName());
		}
	}

	@Override
	public void onCreate(@Nonnull final SherlockFragmentActivity activity, @Nullable Bundle savedInstanceState) {
		this.onCreate((Activity) activity, savedInstanceState);

		final ActionBar actionBar = activity.getSupportActionBar();
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(homeIcon);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayShowHomeEnabled(true);

		toggleTitle(activity, true);

		actionBar.setIcon(R.drawable.ab_icon);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}

	private void toggleTitle(@Nonnull SherlockFragmentActivity activity, boolean showTitle) {
		final ActionBar actionBar = activity.getSupportActionBar();

		if (activity instanceof CalculatorActivity) {
			if (Views.getScreenOrientation(activity) == Configuration.ORIENTATION_PORTRAIT) {
				actionBar.setDisplayShowTitleEnabled(true);
			} else {
				actionBar.setDisplayShowTitleEnabled(false);
			}
		} else {
			actionBar.setDisplayShowTitleEnabled(showTitle);
		}
	}

	public void restoreSavedTab(@Nonnull SherlockFragmentActivity activity) {
		final ActionBar actionBar = activity.getSupportActionBar();
		if (selectedNavigationIndex >= 0 && selectedNavigationIndex < actionBar.getTabCount()) {
			actionBar.setSelectedNavigationItem(selectedNavigationIndex);
		}
	}

	@Override
	public void onSaveInstanceState(@Nonnull SherlockFragmentActivity activity, @Nonnull Bundle outState) {
		onSaveInstanceState((Activity) activity, outState);
	}

	@Override
	public void onSaveInstanceState(@Nonnull Activity activity, @Nonnull Bundle outState) {
	}

	@Override
	public void onResume(@Nonnull Activity activity) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);

		final CalculatorPreferences.Gui.Theme newTheme = CalculatorPreferences.Gui.theme.getPreference(preferences);
		if (!theme.equals(newTheme)) {
			Activities.restartActivity(activity);
		}
	}

	@Override
	public void onPause(@Nonnull Activity activity) {
	}

	@Override
	public void onPause(@Nonnull SherlockFragmentActivity activity) {
		onPause((Activity) activity);

		final int selectedNavigationIndex = activity.getSupportActionBar().getSelectedNavigationIndex();
		if (selectedNavigationIndex >= 0) {
			final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
			final SharedPreferences.Editor editor = preferences.edit();
			editor.putInt(getSavedTabPreferenceName(activity), selectedNavigationIndex);
			editor.commit();
		}

	}

	@Nonnull
	private String getSavedTabPreferenceName(@Nonnull Activity activity) {
		return "tab_" + activity.getClass().getSimpleName();
	}

	@Override
	public void onDestroy(@Nonnull Activity activity) {
		super.onDestroy(activity);

		if (activity instanceof CalculatorEventListener) {
			Locator.getInstance().getCalculator().removeCalculatorEventListener((CalculatorEventListener) activity);
		}
	}

	@Override
	public void onDestroy(@Nonnull SherlockFragmentActivity activity) {
		this.onDestroy((Activity) activity);
	}

	@Override
	public void addTab(@Nonnull SherlockFragmentActivity activity,
					   @Nonnull String tag,
					   @Nonnull Class<? extends Fragment> fragmentClass,
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
	public void addTab(@Nonnull SherlockFragmentActivity activity, @Nonnull CalculatorFragmentType fragmentType, @Nullable Bundle fragmentArgs, int parentViewId) {
		addTab(activity, fragmentType.getFragmentTag(), fragmentType.getFragmentClass(), fragmentArgs, fragmentType.getDefaultTitleResId(), parentViewId);
	}

	@Override
	public void setFragment(@Nonnull SherlockFragmentActivity activity, @Nonnull CalculatorFragmentType fragmentType, @Nullable Bundle fragmentArgs, int parentViewId) {
		final FragmentManager fm = activity.getSupportFragmentManager();

		Fragment fragment = fm.findFragmentByTag(fragmentType.getFragmentTag());
		if (fragment == null) {
			fragment = Fragment.instantiate(activity, fragmentType.getFragmentClass().getName(), fragmentArgs);
			final FragmentTransaction ft = fm.beginTransaction();
			ft.add(parentViewId, fragment, fragmentType.getFragmentTag());
			ft.commit();
		} else {
			if (fragment.isDetached()) {
				final FragmentTransaction ft = fm.beginTransaction();
				ft.attach(fragment);
				ft.commit();
			}

		}
	}

	@Override
	public void selectTab(@Nonnull SherlockFragmentActivity activity, @Nonnull CalculatorFragmentType fragmentType) {
		final ActionBar actionBar = activity.getSupportActionBar();
		for (int i = 0; i < actionBar.getTabCount(); i++) {
			final ActionBar.Tab tab = actionBar.getTabAt(i);
			if (tab != null && CalculatorFragmentType.plotter.getFragmentTag().equals(tab.getTag())) {
				actionBar.setSelectedNavigationItem(i);
				break;
			}
		}
	}

	@Override
	public int getLayoutId() {
		return layoutId;
	}

	@Override
	@Nonnull
	public CalculatorPreferences.Gui.Theme getTheme() {
		return theme;
	}

	@Override
	@Nonnull
	public CalculatorPreferences.Gui.Layout getLayout() {
		return layout;
	}

	@Override
	public void onResume(@Nonnull SherlockFragmentActivity activity) {
		onResume((Activity) activity);

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		selectedNavigationIndex = preferences.getInt(getSavedTabPreferenceName(activity), -1);
		restoreSavedTab(activity);
	}

	private void addHelpInfo(@Nonnull Activity activity, @Nonnull View root) {
		if (CalculatorApplication.isMonkeyRunner(activity)) {
			if (root instanceof ViewGroup) {
				final TextView helperTextView = new TextView(activity);

				final DisplayMetrics dm = new DisplayMetrics();
				activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

				helperTextView.setTextSize(15);
				helperTextView.setTextColor(Color.WHITE);

				final Configuration c = activity.getResources().getConfiguration();

				final StringBuilder helpText = new StringBuilder();
				helpText.append("Size: ");
				if (Views.isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_XLARGE, c)) {
					helpText.append("xlarge");
				} else if (Views.isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_LARGE, c)) {
					helpText.append("large");
				} else if (Views.isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_NORMAL, c)) {
					helpText.append("normal");
				} else if (Views.isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_SMALL, c)) {
					helpText.append("small");
				} else {
					helpText.append("unknown");
				}

				helpText.append(" (").append(dm.widthPixels).append("x").append(dm.heightPixels).append(")");

				helpText.append(" Density: ");
				switch (dm.densityDpi) {
					case DisplayMetrics.DENSITY_LOW:
						helpText.append("ldpi");
						break;
					case DisplayMetrics.DENSITY_MEDIUM:
						helpText.append("mdpi");
						break;
					case DisplayMetrics.DENSITY_HIGH:
						helpText.append("hdpi");
						break;
					case DisplayMetrics.DENSITY_XHIGH:
						helpText.append("xhdpi");
						break;
					case DisplayMetrics.DENSITY_TV:
						helpText.append("tv");
						break;
				}

				helpText.append(" (").append(dm.densityDpi).append(")");

				helperTextView.setText(helpText);

				((ViewGroup) root).addView(helperTextView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			}
		}
	}
}
