/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.text.method.LinkMovementMethod;
import android.view.*;
import android.widget.TextView;
import org.solovyev.android.Activities;
import org.solovyev.android.Android;
import org.solovyev.android.calculator.history.History;
import org.solovyev.android.calculator.wizard.CalculatorWizards;
import org.solovyev.android.fragments.FragmentUtils;
import org.solovyev.android.prefs.Preference;
import org.solovyev.android.wizard.Wizard;
import org.solovyev.android.wizard.Wizards;
import org.solovyev.common.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static android.os.Build.VERSION_CODES.GINGERBREAD_MR1;
import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static org.solovyev.android.calculator.Preferences.Gui.preventScreenFromFading;
import static org.solovyev.android.calculator.release.ReleaseNotes.hasReleaseNotes;
import static org.solovyev.android.wizard.WizardUi.*;

public class CalculatorActivity extends BaseActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Nonnull
    public static final String TAG = CalculatorActivity.class.getSimpleName();

    private boolean useBackAsPrev;
    
    @Inject
    PreferredPreferences preferredPreferences;
    @Inject
    Keyboard keyboard;

    public CalculatorActivity() {
        super(0, TAG);
    }

    private static void firstTimeInit(@Nonnull SharedPreferences preferences, @Nonnull Context context) {
        final Integer appOpenedCounter = Preferences.appOpenedCounter.getPreference(preferences);
        if (appOpenedCounter != null) {
            Preferences.appOpenedCounter.putPreference(preferences, appOpenedCounter + 1);
        }

        final Integer savedVersion = Preferences.appVersion.getPreference(preferences);

        final int appVersion = Android.getAppVersionCode(context);

        Preferences.appVersion.putPreference(preferences, appVersion);

        if (!App.isMonkeyRunner(context)) {

            boolean dialogShown = false;
            final Wizards wizards = App.getWizards();
            final Wizard wizard = wizards.getWizard(CalculatorWizards.FIRST_TIME_WIZARD);
            if (wizard.isStarted() && !wizard.isFinished()) {
                continueWizard(wizards, wizard.getName(), context);
                dialogShown = true;
            } else {
                if (Objects.areEqual(savedVersion, Preferences.appVersion.getDefaultValue())) {
                    // new start
                    startWizard(wizards, context);
                    dialogShown = true;
                } else {
                    if (savedVersion < appVersion) {
                        final boolean showReleaseNotes = Preferences.Gui.showReleaseNotes.getPreference(preferences);
                        if (showReleaseNotes && hasReleaseNotes(context, savedVersion + 1)) {
                            final Bundle bundle = new Bundle();
                            bundle.putInt(CalculatorWizards.RELEASE_NOTES_VERSION, savedVersion);
                            context.startActivity(createLaunchIntent(wizards, CalculatorWizards.RELEASE_NOTES, context, bundle));
                            dialogShown = true;
                        }
                    }
                }
            }

            //Log.d(this.getClass().getName(), "Application was opened " + appOpenedCounter + " time!");
            if (!dialogShown) {
                if (appOpenedCounter != null && appOpenedCounter > 30) {
                    dialogShown = showSpecialWindow(preferences, Preferences.Gui.feedbackWindowShown, R.layout.feedback, R.id.feedbackText, context);
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

    @Inject
    History history;

    @Inject
    ActivityLauncher launcher;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final Preferences.Gui.Layout layout = Preferences.Gui.layout.getPreferenceNoError(preferences);
        ui.setLayoutId(layout.layoutId);

        super.onCreate(savedInstanceState);

        if (isMultiPane()) {
            ui.addTab(this, CalculatorFragmentType.history, null, R.id.main_second_pane);
            ui.addTab(this, CalculatorFragmentType.saved_history, null, R.id.main_second_pane);
            ui.addTab(this, CalculatorFragmentType.variables, null, R.id.main_second_pane);
            ui.addTab(this, CalculatorFragmentType.functions, null, R.id.main_second_pane);
            ui.addTab(this, CalculatorFragmentType.operators, null, R.id.main_second_pane);
        } else {
            final ActionBar actionBar = getSupportActionBar();
            if (Build.VERSION.SDK_INT <= GINGERBREAD_MR1 || (Build.VERSION.SDK_INT >= ICE_CREAM_SANDWICH && hasPermanentMenuKey())) {
                actionBar.hide();
            } else {
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            }
        }

        FragmentUtils.createFragment(this, EditorFragment.class, R.id.editorContainer, "editor");
        FragmentUtils.createFragment(this, CalculatorDisplayFragment.class, R.id.displayContainer, "display");
        FragmentUtils.createFragment(this, CalculatorKeyboardFragment.class, R.id.keyboardContainer, "keyboard");

        this.useBackAsPrev = Preferences.Gui.usePrevAsBack.getPreference(preferences);
        if (savedInstanceState == null) {
            firstTimeInit(preferences, this);
        }

        toggleOrientationChange(preferences);

        preferences.registerOnSharedPreferenceChangeListener(this);

        preferredPreferences.check(this, false);

        if (App.isMonkeyRunner(this)) {
            keyboard.buttonPressed("123");
            keyboard.buttonPressed("+");
            keyboard.buttonPressed("321");
        }
    }

    @Override
    protected void inject(@Nonnull AppComponent component) {
        super.inject(component);
        component.inject(this);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private boolean hasPermanentMenuKey() {
        return ViewConfiguration.get(this).hasPermanentMenuKey();
    }

    private boolean isMultiPane() {
        return findViewById(R.id.main_second_pane) != null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (useBackAsPrev) {
                history.undo();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        launcher.setActivity(this);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final Preferences.Gui.Layout newLayout = Preferences.Gui.layout.getPreference(preferences);
        if (newLayout != ui.getLayout()) {
            Activities.restartActivity(this);
        }

        final Window window = getWindow();
        if (preventScreenFromFading.getPreference(preferences)) {
            window.addFlags(FLAG_KEEP_SCREEN_ON);
        } else {
            window.clearFlags(FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    protected void onPause() {
        launcher.clearActivity(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);

        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, @Nullable String key) {
        if (Preferences.Gui.usePrevAsBack.getKey().equals(key)) {
            useBackAsPrev = Preferences.Gui.usePrevAsBack.getPreference(preferences);
        }

        if (Preferences.Gui.autoOrientation.getKey().equals(key)) {
            toggleOrientationChange(preferences);
        }
    }

    private void toggleOrientationChange(@Nullable SharedPreferences preferences) {
        preferences = preferences == null ? PreferenceManager.getDefaultSharedPreferences(this) : preferences;
        if (Preferences.Gui.autoOrientation.getPreference(preferences)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
}