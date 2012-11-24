/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.AndroidUtils;
import org.solovyev.android.calculator.about.CalculatorFragmentType;
import org.solovyev.android.calculator.about.CalculatorReleaseNotesFragment;
import org.solovyev.android.fragments.FragmentUtils;
import org.solovyev.android.prefs.Preference;
import org.solovyev.common.equals.EqualsTool;
import org.solovyev.common.history.HistoryAction;
import org.solovyev.common.text.StringUtils;

public class CalculatorActivity extends SherlockFragmentActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @NotNull
    public static final String TAG = CalculatorActivity.class.getSimpleName();

	private boolean useBackAsPrev;

    @NotNull
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

        if (findViewById(R.id.main_second_pane) != null) {
            activityHelper.addTab(this, CalculatorFragmentType.history, null, R.id.main_second_pane);
            activityHelper.addTab(this, CalculatorFragmentType.saved_history, null, R.id.main_second_pane);
            activityHelper.addTab(this, CalculatorFragmentType.variables, null, R.id.main_second_pane);
            activityHelper.addTab(this, CalculatorFragmentType.functions, null, R.id.main_second_pane);
            activityHelper.addTab(this, CalculatorFragmentType.operators, null, R.id.main_second_pane);
            activityHelper.addTab(this, CalculatorFragmentType.plotter, null, R.id.main_second_pane);
            activityHelper.addTab(this, CalculatorFragmentType.faq, null, R.id.main_second_pane);
        } else {
            getSupportActionBar().hide();
        }

        FragmentUtils.createFragment(this, CalculatorEditorFragment.class, R.id.editorContainer, "editor");
        FragmentUtils.createFragment(this, CalculatorDisplayFragment.class, R.id.displayContainer, "display");
        FragmentUtils.createFragment(this, CalculatorKeyboardFragment.class, R.id.keyboardContainer, "keyboard");

        this.useBackAsPrev = CalculatorPreferences.Gui.usePrevAsBack.getPreference(preferences);
        firstTimeInit(preferences, this);

        toggleOrientationChange(preferences);

        preferences.registerOnSharedPreferenceChangeListener(this);

        Locator.getInstance().getPreferenceService().checkPreferredPreferences(false);

        if ( CalculatorApplication.isMonkeyRunner(this) ) {
            Locator.getInstance().getKeyboard().buttonPressed("123");
            Locator.getInstance().getKeyboard().buttonPressed("+");
            Locator.getInstance().getKeyboard().buttonPressed("321");
        }
    }

    @NotNull
    private AndroidCalculator getCalculator() {
        return ((AndroidCalculator) Locator.getInstance().getCalculator());
    }

    private static void firstTimeInit(@NotNull SharedPreferences preferences, @NotNull Context context) {
        final Integer appOpenedCounter = CalculatorPreferences.appOpenedCounter.getPreference(preferences);
        if (appOpenedCounter != null) {
            CalculatorPreferences.appOpenedCounter.putPreference(preferences, appOpenedCounter + 1);
        }

        final Integer savedVersion = CalculatorPreferences.appVersion.getPreference(preferences);

        final int appVersion = AndroidUtils.getAppVersionCode(context, CalculatorActivity.class.getPackage().getName());

        CalculatorPreferences.appVersion.putPreference(preferences, appVersion);

        if (!CalculatorApplication.isMonkeyRunner(context)) {

            boolean dialogShown = false;
            if (EqualsTool.areEqual(savedVersion, CalculatorPreferences.appVersion.getDefaultValue())) {
                // new start
                final AlertDialog.Builder builder = new AlertDialog.Builder(context).setMessage(R.string.c_first_start_text);
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setTitle(R.string.c_first_start_text_title);
                builder.create().show();
                dialogShown = true;
            } else {
                if (savedVersion < appVersion) {
                    final boolean showReleaseNotes = CalculatorPreferences.Gui.showReleaseNotes.getPreference(preferences);
                    if (showReleaseNotes) {
                        final String releaseNotes = CalculatorReleaseNotesFragment.getReleaseNotes(context, savedVersion + 1);
                        if (!StringUtils.isEmpty(releaseNotes)) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(context).setMessage(Html.fromHtml(releaseNotes));
                            builder.setPositiveButton(android.R.string.ok, null);
                            builder.setTitle(R.string.c_release_notes);
                            builder.create().show();
                            dialogShown = true;
                        }
                    }
                }
            }


            //Log.d(this.getClass().getName(), "Application was opened " + appOpenedCounter + " time!");
            if (!dialogShown) {
                if (appOpenedCounter != null && appOpenedCounter > 10) {
                    dialogShown = showSpecialWindow(preferences, CalculatorPreferences.Gui.feedbackWindowShown, R.layout.feedback, R.id.feedbackText, context);
                }
            }
        }
    }

    private static boolean showSpecialWindow(@NotNull SharedPreferences preferences, @NotNull Preference<Boolean> specialWindowShownPref, int layoutId, int textViewId, @NotNull Context context) {
        boolean result = false;

        final Boolean specialWindowShown = specialWindowShownPref.getPreference(preferences);
        if ( specialWindowShown != null && !specialWindowShown ) {
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
    public void equalsButtonClickHandler(@NotNull View v) {
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
        if ( newLayout != activityHelper.getLayout() ) {
            AndroidUtils.restartActivity(this);
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
		if ( CalculatorPreferences.Gui.usePrevAsBack.getKey().equals(key) ) {
			useBackAsPrev = CalculatorPreferences.Gui.usePrevAsBack.getPreference(preferences);
		}

        if ( CalculatorPreferences.Gui.autoOrientation.getKey().equals(key) ) {
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
    public void elementaryButtonClickHandler(@NotNull View v) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void historyButtonClickHandler(@NotNull View v) {
        buttonPressed(CalculatorSpecialButton.history);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void eraseButtonClickHandler(@NotNull View v) {
        buttonPressed(CalculatorSpecialButton.erase);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void simplifyButtonClickHandler(@NotNull View v) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void pasteButtonClickHandler(@NotNull View v) {
        buttonPressed(CalculatorSpecialButton.paste);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void copyButtonClickHandler(@NotNull View v) {
        buttonPressed(CalculatorSpecialButton.copy);
    }

    @NotNull
    private static CalculatorKeyboard getKeyboard() {
        return Locator.getInstance().getKeyboard();
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void clearButtonClickHandler(@NotNull View v) {
        buttonPressed(CalculatorSpecialButton.clear);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void digitButtonClickHandler(@NotNull View v) {
        Log.d(String.valueOf(v.getId()), "digitButtonClickHandler() for: " + v.getId() + ". Pressed: " + v.isPressed());

        if (v instanceof Button) {
            buttonPressed(((Button)v).getText().toString());
        }
    }

    private void buttonPressed(@NotNull CalculatorSpecialButton button) {
        buttonPressed(button.getActionCode());
    }

    private void buttonPressed(@NotNull String text) {
        getKeyboard().buttonPressed(text);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void functionsButtonClickHandler(@NotNull View v) {
        buttonPressed(CalculatorSpecialButton.functions);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void operatorsButtonClickHandler(@NotNull View v) {
        buttonPressed(CalculatorSpecialButton.operators);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void varsButtonClickHandler(@NotNull View v) {
        buttonPressed(CalculatorSpecialButton.vars);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void likeButtonClickHandler(@NotNull View v) {
        buttonPressed(CalculatorSpecialButton.like);
    }

}