/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.app.Activity;
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
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import net.robotmedia.billing.BillingController;
import net.robotmedia.billing.IBillingObserver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.AndroidUtils;
import org.solovyev.android.FontSizeAdjuster;
import org.solovyev.android.calculator.about.CalculatorReleaseNotesActivity;
import org.solovyev.android.calculator.history.CalculatorHistoryFragment;
import org.solovyev.android.calculator.history.CalculatorSavedHistoryFragment;
import org.solovyev.android.calculator.math.edit.CalculatorVarsFragment;
import org.solovyev.android.calculator.model.VarCategory;
import org.solovyev.android.fragments.FragmentUtils;
import org.solovyev.android.prefs.Preference;
import org.solovyev.android.view.ColorButton;
import org.solovyev.common.equals.EqualsTool;
import org.solovyev.common.history.HistoryAction;
import org.solovyev.common.text.StringUtils;

public class CalculatorActivity extends SherlockFragmentActivity implements FontSizeAdjuster, SharedPreferences.OnSharedPreferenceChangeListener {

    @NotNull
    public static final String TAG = CalculatorActivity.class.getSimpleName();

	private static final int HVGA_WIDTH_PIXELS = 320;

	@Nullable
	private IBillingObserver billingObserver;

	private boolean useBackAsPrev;

    @NotNull
    private CalculatorActivityHelper activityHelper;

    /**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {

		CalculatorApplication.registerOnRemoteStackTrace();

		/*final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);*/

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        final CalculatorPreferences.Gui.Layout layout = CalculatorPreferences.Gui.layout.getPreferenceNoError(preferences);

        activityHelper = CalculatorApplication.getInstance().createActivityHelper(layout.getLayoutId(), TAG);
        activityHelper.logDebug("onCreate");
        activityHelper.onCreate(this, savedInstanceState);

        super.onCreate(savedInstanceState);
        activityHelper.logDebug("super.onCreate");

        if (findViewById(R.id.main_second_pane) != null) {
            activityHelper.addTab(this, "history", CalculatorHistoryFragment.class, null, R.string.c_history, R.id.main_second_pane);
            activityHelper.addTab(this, "saved_history", CalculatorSavedHistoryFragment.class, null, R.string.c_saved_history, R.id.main_second_pane);

            for (VarCategory category : VarCategory.getCategoriesByTabOrder()) {
                activityHelper.addTab(this, "vars_" + category.name(), CalculatorVarsFragment.class, CalculatorVarsFragment.createBundleFor(category.name()), category.getCaptionId(), R.id.main_second_pane);
            }
            activityHelper.restoreSavedTab(this);
        }

        CalculatorKeyboardFragment.fixThemeParameters(true, activityHelper.getTheme(), this.getWindow().getDecorView());

        FragmentUtils.createFragment(this, CalculatorEditorFragment.class, R.id.editorContainer, "editor");
        FragmentUtils.createFragment(this, CalculatorDisplayFragment.class, R.id.displayContainer, "display");
        FragmentUtils.createFragment(this, CalculatorKeyboardFragment.class, R.id.keyboardContainer, "keyboard");

        /*if (customTitleSupported) {
			try {
				getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.calc_title);
				final CalculatorAdditionalTitle additionalAdditionalTitleText = (CalculatorAdditionalTitle)findViewById(R.id.additional_title_text);
				additionalAdditionalTitleText.init(preferences);
				preferences.registerOnSharedPreferenceChangeListener(additionalAdditionalTitleText);
			} catch (ClassCastException e) {
				// super fix for issue with class cast in android.view.Window.setFeatureInt() (see app error reports)
				Log.e(CalculatorActivity.class.getName(), e.getMessage(), e);
			}
		}*/

		billingObserver = new CalculatorBillingObserver(this);
		BillingController.registerObserver(billingObserver);

        this.useBackAsPrev = CalculatorPreferences.Gui.usePrevAsBack.getPreference(preferences);
        firstTimeInit(preferences, this);

		// init billing controller
		BillingController.checkBillingSupported(this);

        toggleOrientationChange(preferences);

        CalculatorKeyboardFragment.toggleEqualsButton(preferences, this, activityHelper.getTheme(), findViewById(R.id.main_layout));

        preferences.registerOnSharedPreferenceChangeListener(this);
	}

    @NotNull
    private AndroidCalculator getCalculator() {
        return ((AndroidCalculator) CalculatorLocatorImpl.getInstance().getCalculator());
    }

    private static void firstTimeInit(@NotNull SharedPreferences preferences, @NotNull Context context) {
        final Integer appOpenedCounter = CalculatorPreferences.appOpenedCounter.getPreference(preferences);
        if (appOpenedCounter != null) {
            CalculatorPreferences.appOpenedCounter.putPreference(preferences, appOpenedCounter + 1);
        }

        final Integer savedVersion = CalculatorPreferences.appVersion.getPreference(preferences);

        final int appVersion = AndroidUtils.getAppVersionCode(context, CalculatorActivity.class.getPackage().getName());

        CalculatorPreferences.appVersion.putPreference(preferences, appVersion);

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
                    final String releaseNotes = CalculatorReleaseNotesActivity.getReleaseNotes(context, savedVersion + 1);
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

        if (!dialogShown) {
            dialogShown = showSpecialWindow(preferences, CalculatorPreferences.Gui.notesppAnnounceShown, R.layout.notespp_announce, R.id.notespp_announce, context);
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
        getCalculator().evaluate();
    }


	/**
	 * The font sizes in the layout files are specified for a HVGA display.
	 * Adjust the font sizes accordingly if we are running on a different
	 * display.
	 */
	@Override
	public void adjustFontSize(@NotNull TextView view) {
		/*float fontPixelSize = view.getTextSize();
		Display display = getWindowManager().getDefaultDisplay();
		int h = Math.min(display.getWidth(), display.getHeight());
		float ratio = (float) h / HVGA_WIDTH_PIXELS;
		view.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontPixelSize * ratio);*/
	}

    @Override
    protected void onPause() {
        super.onPause();

        activityHelper.onPause(this);
    }

    @Override
	protected void onResume() {
		super.onResume();

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final CalculatorPreferences.Gui.Layout newLayout = CalculatorPreferences.Gui.layout.getPreference(preferences);
        if ( newLayout.getLayoutId() != activityHelper.getLayoutId() ) {
            AndroidUtils.restartActivity(this);
        }

        this.activityHelper.onResume(this);
	}

	@Override
	protected void onDestroy() {
		if (billingObserver !=  null) {
			BillingController.unregisterObserver(billingObserver);
		}

        activityHelper.onDestroy(this);

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
        CalculatorActivityLauncher.showHistory(this);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void eraseButtonClickHandler(@NotNull View v) {
        CalculatorLocatorImpl.getInstance().getEditor().erase();
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void simplifyButtonClickHandler(@NotNull View v) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void moveLeftButtonClickHandler(@NotNull View v) {
        getKeyboard().moveCursorLeft();
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void moveRightButtonClickHandler(@NotNull View v) {
        getKeyboard().moveCursorRight();
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void pasteButtonClickHandler(@NotNull View v) {
        getKeyboard().pasteButtonPressed();
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void copyButtonClickHandler(@NotNull View v) {
        getKeyboard().copyButtonPressed();
    }

    @NotNull
    private static CalculatorKeyboard getKeyboard() {
        return CalculatorLocatorImpl.getInstance().getKeyboard();
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void clearButtonClickHandler(@NotNull View v) {
        getKeyboard().clearButtonPressed();
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void digitButtonClickHandler(@NotNull View v) {
        Log.d(String.valueOf(v.getId()), "digitButtonClickHandler() for: " + v.getId() + ". Pressed: " + v.isPressed());
        if (((ColorButton) v).isShowText()) {
            getKeyboard().digitButtonPressed(((ColorButton) v).getText().toString());
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void functionsButtonClickHandler(@NotNull View v) {
        CalculatorActivityLauncher.showFunctions(this);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void operatorsButtonClickHandler(@NotNull View v) {
        CalculatorActivityLauncher.showOperators(this);
    }

    public static void operatorsButtonClickHandler(@NotNull Activity activity) {
        CalculatorActivityLauncher.showOperators(activity);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void varsButtonClickHandler(@NotNull View v) {
        CalculatorActivityLauncher.showVars(this);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void donateButtonClickHandler(@NotNull View v) {
        CalculatorApplication.showDonationDialog(this);
    }

}