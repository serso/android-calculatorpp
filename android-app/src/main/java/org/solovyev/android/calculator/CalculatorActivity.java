/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.solovyev.android.Activities;
import org.solovyev.android.Android;
import org.solovyev.android.Threads;
import org.solovyev.android.calculator.about.CalculatorReleaseNotesFragment;
import org.solovyev.android.calculator.plot.CalculatorPlotActivity;
import org.solovyev.android.calculator.wizard.CalculatorWizardActivity;
import org.solovyev.android.fragments.FragmentUtils;
import org.solovyev.android.prefs.Preference;
import org.solovyev.common.Objects;
import org.solovyev.common.history.HistoryAction;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static android.os.Build.VERSION_CODES.GINGERBREAD_MR1;
import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static org.solovyev.android.calculator.CalculatorPreferences.Gui.preventScreenFromFading;
import static org.solovyev.android.calculator.wizard.Wizard.FIRST_TIME_WIZARD;
import static org.solovyev.android.calculator.wizard.Wizard.isWizardFinished;
import static org.solovyev.android.calculator.wizard.Wizard.isWizardStarted;

public class CalculatorActivity extends SherlockFragmentActivity implements SharedPreferences.OnSharedPreferenceChangeListener, CalculatorEventListener {

	@Nonnull
	public static final String TAG = CalculatorActivity.class.getSimpleName();

	private boolean useBackAsPrev;

	@Nonnull
	private CalculatorActivityHelper activityHelper;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		final CalculatorPreferences.Gui.Layout layout = CalculatorPreferences.Gui.layout.getPreferenceNoError(preferences);

		activityHelper = CalculatorApplication.getInstance().createActivityHelper(layout.getLayoutId(), TAG);
		activityHelper.logDebug("onCreate");
		activityHelper.onCreate(this, savedInstanceState);

		super.onCreate(savedInstanceState);
		activityHelper.logDebug("super.onCreate");

		if (isMultiPane()) {
			activityHelper.addTab(this, CalculatorFragmentType.history, null, R.id.main_second_pane);
			activityHelper.addTab(this, CalculatorFragmentType.saved_history, null, R.id.main_second_pane);
			activityHelper.addTab(this, CalculatorFragmentType.variables, null, R.id.main_second_pane);
			activityHelper.addTab(this, CalculatorFragmentType.functions, null, R.id.main_second_pane);
			activityHelper.addTab(this, CalculatorFragmentType.operators, null, R.id.main_second_pane);
			activityHelper.addTab(this, CalculatorPlotActivity.getPlotterFragmentType(), null, R.id.main_second_pane);
			activityHelper.addTab(this, CalculatorFragmentType.faq, null, R.id.main_second_pane);
		} else {
			final ActionBar actionBar = getSupportActionBar();
			if (Build.VERSION.SDK_INT <= GINGERBREAD_MR1 || (Build.VERSION.SDK_INT >= ICE_CREAM_SANDWICH && ViewConfiguration.get(this).hasPermanentMenuKey())) {
				actionBar.hide();
			} else {
				actionBar.setDisplayShowTitleEnabled(false);
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			}
		}

		FragmentUtils.createFragment(this, CalculatorEditorFragment.class, R.id.editorContainer, "editor");
		FragmentUtils.createFragment(this, CalculatorDisplayFragment.class, R.id.displayContainer, "display");
		FragmentUtils.createFragment(this, CalculatorKeyboardFragment.class, R.id.keyboardContainer, "keyboard");

		this.useBackAsPrev = CalculatorPreferences.Gui.usePrevAsBack.getPreference(preferences);
		firstTimeInit(preferences, this);

		toggleOrientationChange(preferences);

		preferences.registerOnSharedPreferenceChangeListener(this);

		Locator.getInstance().getPreferenceService().checkPreferredPreferences(false);

		if (CalculatorApplication.isMonkeyRunner(this)) {
			Locator.getInstance().getKeyboard().buttonPressed("123");
			Locator.getInstance().getKeyboard().buttonPressed("+");
			Locator.getInstance().getKeyboard().buttonPressed("321");
		}
	}

	private boolean isMultiPane() {
		return findViewById(R.id.main_second_pane) != null;
	}

	@Nonnull
	private AndroidCalculator getCalculator() {
		return ((AndroidCalculator) Locator.getInstance().getCalculator());
	}

	private static void firstTimeInit(@Nonnull SharedPreferences preferences, @Nonnull Context context) {
		final Integer appOpenedCounter = CalculatorPreferences.appOpenedCounter.getPreference(preferences);
		if (appOpenedCounter != null) {
			CalculatorPreferences.appOpenedCounter.putPreference(preferences, appOpenedCounter + 1);
		}

		final Integer savedVersion = CalculatorPreferences.appVersion.getPreference(preferences);

		final int appVersion = Android.getAppVersionCode(context);

		CalculatorPreferences.appVersion.putPreference(preferences, appVersion);

		if (!CalculatorApplication.isMonkeyRunner(context)) {

			boolean dialogShown = false;
			if(isWizardStarted(FIRST_TIME_WIZARD) && !isWizardFinished(FIRST_TIME_WIZARD)) {
				CalculatorWizardActivity.continueWizard(FIRST_TIME_WIZARD, context);
				dialogShown = true;
			} else {
				if (Objects.areEqual(savedVersion, CalculatorPreferences.appVersion.getDefaultValue())) {
					// new start
					context.startActivity(new Intent(context, CalculatorWizardActivity.class));
					dialogShown = true;
				} else {
					if (savedVersion < appVersion) {
						final boolean showReleaseNotes = CalculatorPreferences.Gui.showReleaseNotes.getPreference(preferences);
						if (showReleaseNotes) {
							final String releaseNotes = CalculatorReleaseNotesFragment.getReleaseNotes(context, savedVersion + 1);
							if (!Strings.isEmpty(releaseNotes)) {
								final AlertDialog.Builder builder = new AlertDialog.Builder(context).setMessage(Html.fromHtml(releaseNotes));
								builder.setPositiveButton(android.R.string.ok, null);
								builder.setTitle(R.string.c_release_notes);
								builder.create().show();
								dialogShown = true;
							}
						}
					}
				}
			}

			//Log.d(this.getClass().getName(), "Application was opened " + appOpenedCounter + " time!");
			if (!dialogShown) {
				if (appOpenedCounter != null && appOpenedCounter > 100) {
					dialogShown = showSpecialWindow(preferences, CalculatorPreferences.Gui.feedbackWindowShown, R.layout.feedback, R.id.feedbackText, context);
				}
			}
		}
	}

	private static boolean showSpecialWindow(@Nonnull SharedPreferences preferences, @Nonnull Preference<Boolean> specialWindowShownPref, int layoutId, int textViewId, @Nonnull Context context) {
		boolean result = false;

		final Boolean specialWindowShown = specialWindowShownPref.getPreference(preferences);
		if (specialWindowShown != null && !specialWindowShown) {
			final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
			final View view = layoutInflater.inflate(layoutId, null);

			final TextView feedbackTextView = (TextView) view.findViewById(textViewId);
			feedbackTextView.setMovementMethod(LinkMovementMethod.getInstance());

			final AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(view);
			builder.setPositiveButton(android.R.string.ok, null);
			builder.create().show();

			result = true;
			specialWindowShownPref.putPreference(preferences, true);
		}

		return result;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (useBackAsPrev) {
				getCalculator().doHistoryAction(HistoryAction.undo);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void equalsButtonClickHandler(@Nonnull View v) {
		buttonPressed(CalculatorSpecialButton.equals);
	}

	@Override
	protected void onPause() {
		this.activityHelper.onPause(this);

		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		final CalculatorPreferences.Gui.Layout newLayout = CalculatorPreferences.Gui.layout.getPreference(preferences);
		if (newLayout != activityHelper.getLayout()) {
			Activities.restartActivity(this);
		}

		final Window window = getWindow();
		if (preventScreenFromFading.getPreference(preferences)) {
			window.addFlags(FLAG_KEEP_SCREEN_ON);
		} else {
			window.clearFlags(FLAG_KEEP_SCREEN_ON);
		}

		this.activityHelper.onResume(this);
	}

	@Override
	protected void onDestroy() {
		activityHelper.onDestroy(this);

		PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);

		super.onDestroy();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences preferences, @Nullable String key) {
		if (CalculatorPreferences.Gui.usePrevAsBack.getKey().equals(key)) {
			useBackAsPrev = CalculatorPreferences.Gui.usePrevAsBack.getPreference(preferences);
		}

		if (CalculatorPreferences.Gui.autoOrientation.getKey().equals(key)) {
			toggleOrientationChange(preferences);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		activityHelper.onSaveInstanceState(this, outState);
	}

	private void toggleOrientationChange(@Nullable SharedPreferences preferences) {
		preferences = preferences == null ? PreferenceManager.getDefaultSharedPreferences(this) : preferences;
		if (CalculatorPreferences.Gui.autoOrientation.getPreference(preferences)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	/*
	**********************************************************************
	*
	*                           BUTTON HANDLERS
	*
	**********************************************************************
	*/

	@SuppressWarnings({"UnusedDeclaration"})
	public void elementaryButtonClickHandler(@Nonnull View v) {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void historyButtonClickHandler(@Nonnull View v) {
		buttonPressed(CalculatorSpecialButton.history);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void eraseButtonClickHandler(@Nonnull View v) {
		buttonPressed(CalculatorSpecialButton.erase);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void simplifyButtonClickHandler(@Nonnull View v) {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void pasteButtonClickHandler(@Nonnull View v) {
		buttonPressed(CalculatorSpecialButton.paste);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void copyButtonClickHandler(@Nonnull View v) {
		buttonPressed(CalculatorSpecialButton.copy);
	}

	@Nonnull
	private static CalculatorKeyboard getKeyboard() {
		return Locator.getInstance().getKeyboard();
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void clearButtonClickHandler(@Nonnull View v) {
		buttonPressed(CalculatorSpecialButton.clear);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void digitButtonClickHandler(@Nonnull View v) {
		Log.d(String.valueOf(v.getId()), "digitButtonClickHandler() for: " + v.getId() + ". Pressed: " + v.isPressed());

		if (v instanceof Button) {
			buttonPressed(((Button) v).getText().toString());
		}
	}

	private void buttonPressed(@Nonnull CalculatorSpecialButton button) {
		buttonPressed(button.getActionCode());
	}

	private void buttonPressed(@Nonnull String text) {
		getKeyboard().buttonPressed(text);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void functionsButtonClickHandler(@Nonnull View v) {
		buttonPressed(CalculatorSpecialButton.functions);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void operatorsButtonClickHandler(@Nonnull View v) {
		buttonPressed(CalculatorSpecialButton.operators);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void varsButtonClickHandler(@Nonnull View v) {
		buttonPressed(CalculatorSpecialButton.vars);
	}

	@SuppressWarnings({"UnusedDeclaration"})
	public void likeButtonClickHandler(@Nonnull View v) {
		buttonPressed(CalculatorSpecialButton.like);
	}

	@Override
	public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {
		switch (calculatorEventType) {
			case plot_graph:
				Threads.tryRunOnUiThread(this, new Runnable() {
					@Override
					public void run() {
						if (isMultiPane()) {
							final ActionBar.Tab selectedTab = getSupportActionBar().getSelectedTab();
							if (selectedTab != null && CalculatorFragmentType.plotter.getFragmentTag().equals(selectedTab.getTag())) {
								// do nothing - fragment shown and already registered for plot updates
							} else {
								// otherwise - open fragment
								activityHelper.selectTab(CalculatorActivity.this, CalculatorFragmentType.plotter);
							}
						} else {
							// start new activity
							CalculatorActivityLauncher.plotGraph(CalculatorActivity.this);
						}
					}
				});
				break;
		}
	}
}