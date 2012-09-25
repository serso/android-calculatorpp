/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.TextView;
import net.robotmedia.billing.BillingController;
import net.robotmedia.billing.IBillingObserver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.AndroidUtils;
import org.solovyev.android.FontSizeAdjuster;
import org.solovyev.android.calculator.about.CalculatorReleaseNotesActivity;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;
import org.solovyev.android.calculator.view.CalculatorAdditionalTitle;
import org.solovyev.android.menu.ActivityMenu;
import org.solovyev.android.menu.LayoutActivityMenu;
import org.solovyev.android.prefs.Preference;
import org.solovyev.android.view.ColorButton;
import org.solovyev.common.equals.EqualsTool;
import org.solovyev.common.history.HistoryAction;
import org.solovyev.common.text.StringUtils;

public class CalculatorActivity extends Activity implements FontSizeAdjuster, SharedPreferences.OnSharedPreferenceChangeListener {

    @NotNull
    public static final String TAG = "Calculator++";

	private static final int HVGA_WIDTH_PIXELS = 320;

	@Nullable
	private IBillingObserver billingObserver;

	@NotNull
	private CalculatorPreferences.Gui.Theme theme;

	@NotNull
	private CalculatorPreferences.Gui.Layout layout;

	private boolean useBackAsPrev;

    @NotNull
    private ActivityMenu<Menu, MenuItem> menu = LayoutActivityMenu.newInstance(R.menu.main_menu, CalculatorMenu.class);

    /**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {

		CalculatorApplication.registerOnRemoteStackTrace();

		final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		setTheme(preferences);
		super.onCreate(savedInstanceState);
		setLayout(preferences);

        CalculatorKeyboardFragment.fixThemeParameters(true, theme, this.getWindow().getDecorView());

        final FragmentManager fragmentManager = getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        final CalculatorEditorFragment editorFragment = new CalculatorEditorFragment();
        fragmentTransaction.add(R.id.editorContainer, editorFragment, "editor");
        fragmentTransaction.commit();

        fragmentTransaction = fragmentManager.beginTransaction();
        final CalculatorDisplayFragment displayFragment = new CalculatorDisplayFragment();
        fragmentTransaction.add(R.id.displayContainer, displayFragment, "display");
        fragmentTransaction.commit();

        fragmentTransaction = fragmentManager.beginTransaction();
        final CalculatorKeyboardFragment keyboardFragment = new CalculatorKeyboardFragment();
        fragmentTransaction.add(R.id.keyboardContainer, keyboardFragment, "keyboard");
        fragmentTransaction.commit();

        if (customTitleSupported) {
			try {
				getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.calc_title);
				final CalculatorAdditionalTitle additionalAdditionalTitleText = (CalculatorAdditionalTitle)findViewById(R.id.additional_title_text);
				additionalAdditionalTitleText.init(preferences);
				preferences.registerOnSharedPreferenceChangeListener(additionalAdditionalTitleText);
			} catch (ClassCastException e) {
				// super fix for issue with class cast in android.view.Window.setFeatureInt() (see app error reports)
				Log.e(CalculatorActivity.class.getName(), e.getMessage(), e);
			}
		}

		billingObserver = new CalculatorBillingObserver(this);
		BillingController.registerObserver(billingObserver);

        this.useBackAsPrev = CalculatorPreferences.Gui.usePrevAsBack.getPreference(preferences);
        firstTimeInit(preferences, this);

		// init billing controller
		BillingController.checkBillingSupported(this);

        toggleOrientationChange(preferences);

        CalculatorKeyboardFragment.toggleEqualsButton(preferences, this, theme, findViewById(R.id.main_layout));

        preferences.registerOnSharedPreferenceChangeListener(this);
	}

    @NotNull
    private AndroidCalculatorEngine getEngine() {
        return ((AndroidCalculatorEngine) CalculatorLocatorImpl.getInstance().getEngine());
    }

    @NotNull
    private AndroidCalculator getCalculator() {
        return ((AndroidCalculator) CalculatorLocatorImpl.getInstance().getCalculator());
    }

    private synchronized void setLayout(@NotNull SharedPreferences preferences) {
        layout = CalculatorPreferences.Gui.layout.getPreferenceNoError(preferences);

        setContentView(layout.getLayoutId());
	}

	private synchronized void setTheme(@NotNull SharedPreferences preferences) {
		theme = CalculatorPreferences.Gui.theme.getPreferenceNoError(preferences);
		setTheme(theme.getThemeId());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return this.menu.onCreateOptionsMenu(this, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return menu.onOptionsItemSelected(this, item);
    }

	/**
	 * The font sizes in the layout files are specified for a HVGA display.
	 * Adjust the font sizes accordingly if we are running on a different
	 * display.
	 */
	@Override
	public void adjustFontSize(@NotNull TextView view) {
		float fontPixelSize = view.getTextSize();
		Display display = getWindowManager().getDefaultDisplay();
		int h = Math.min(display.getWidth(), display.getHeight());
		float ratio = (float) h / HVGA_WIDTH_PIXELS;
		view.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontPixelSize * ratio);
	}

	@Override
	protected void onResume() {
		super.onResume();

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		final CalculatorPreferences.Gui.Layout newLayout = CalculatorPreferences.Gui.layout.getPreference(preferences);
		final CalculatorPreferences.Gui.Theme newTheme = CalculatorPreferences.Gui.theme.getPreference(preferences);
		if (!theme.equals(newTheme) || !layout.equals(newLayout)) {
			AndroidUtils.restartActivity(this);
		}
	}

	@Override
	protected void onDestroy() {
		if (billingObserver !=  null) {
			BillingController.unregisterObserver(billingObserver);
		}

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

    public static void operatorsButtonClickHandler(@NotNull Activity activity, @NotNull View view) {
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