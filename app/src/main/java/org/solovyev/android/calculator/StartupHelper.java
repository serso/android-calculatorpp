package org.solovyev.android.calculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import org.solovyev.android.Android;
import org.solovyev.android.calculator.wizard.CalculatorWizards;
import org.solovyev.android.wizard.Wizard;
import org.solovyev.android.wizard.Wizards;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static org.solovyev.android.calculator.release.ReleaseNotes.hasReleaseNotes;
import static org.solovyev.android.wizard.WizardUi.*;

@Singleton
public class StartupHelper {

    @Named(AppModule.PREFS_UI)
    @Inject
    SharedPreferences uiPreferences;
    @Inject
    SharedPreferences preferences;

    @Inject
    public StartupHelper() {
    }

    public void onMainActivityOpened(@NonNull AppCompatActivity activity) {
        final SharedPreferences.Editor editor = uiPreferences.edit();
        final Integer opened = UiPreferences.opened.getPreference(uiPreferences);
        UiPreferences.opened.putPreference(editor, opened == null ? 1 : opened + 1);

        if (!App.isMonkeyRunner(activity)) {
            handleOnMainActivityOpened(activity, editor, opened == null ? 0 : opened);
        }
        UiPreferences.appVersion.putPreference(editor, Android.getAppVersionCode(activity));
        editor.apply();
    }

    private void handleOnMainActivityOpened(@NonNull AppCompatActivity activity, @NonNull SharedPreferences.Editor editor, int opened) {
        final int currentVersion = Android.getAppVersionCode(activity);
        final Wizards wizards = App.getWizards();
        final Wizard wizard = wizards.getWizard(CalculatorWizards.FIRST_TIME_WIZARD);
        if (wizard.isStarted() && !wizard.isFinished()) {
            continueWizard(wizards, wizard.getName(), activity);
            return;
        }

        if (!UiPreferences.appVersion.isSet(uiPreferences)) {
            // new start
            startWizard(wizards, activity);
            return;
        }

        final Integer savedVersion = UiPreferences.appVersion.getPreference(uiPreferences);
        if (savedVersion < currentVersion) {
            if (Preferences.Gui.showReleaseNotes.getPreference(preferences) && hasReleaseNotes(activity, savedVersion + 1)) {
                final Bundle bundle = new Bundle();
                bundle.putInt(CalculatorWizards.RELEASE_NOTES_VERSION, savedVersion);
                activity.startActivity(createLaunchIntent(wizards, CalculatorWizards.RELEASE_NOTES, activity, bundle));
                return;
            }
        }

        if (opened > 30 && !UiPreferences.rateUsShown.getPreference(uiPreferences)) {
            final LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = layoutInflater.inflate(R.layout.feedback, null);

            final TextView feedbackTextView = (TextView) view.findViewById(R.id.feedbackText);
            feedbackTextView.setMovementMethod(LinkMovementMethod.getInstance());

            final AlertDialog.Builder builder = new AlertDialog.Builder(activity, App.getTheme().alertDialogTheme).setView(view);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.create().show();
            UiPreferences.rateUsShown.putPreference(editor, true);
        }
    }
}
