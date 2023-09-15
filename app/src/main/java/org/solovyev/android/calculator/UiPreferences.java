package org.solovyev.android.calculator;

import android.content.SharedPreferences;
import androidx.annotation.NonNull;

import org.solovyev.android.prefs.BooleanPreference;
import org.solovyev.android.prefs.IntegerPreference;
import org.solovyev.android.prefs.Preference;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class UiPreferences {
    @NonNull
    public static final Preference<Integer> opened = IntegerPreference.of("opened", 0);
    @NonNull
    public static final Preference<Integer> version = IntegerPreference.of("version", 1);
    @NonNull
    public static final Preference<Integer> appVersion = IntegerPreference.of("appVersion", IntegerPreference.DEF_VALUE);
    @NonNull
    public static final Preference<Boolean> rateUsShown = BooleanPreference.of("rateUsShown", false);
    public boolean showFixableErrorDialog = true;

    @Inject
    public UiPreferences() {
    }

    public static void init(@NonNull SharedPreferences preferences, @NonNull SharedPreferences uiPreferences) {
        final int currentVersion = getVersion(uiPreferences);
        if (currentVersion == 0) {
            final SharedPreferences.Editor editor = uiPreferences.edit();
            migratePreference(uiPreferences, preferences, editor, UiPreferences.rateUsShown, Preferences.Deleted.feedbackWindowShown);
            migratePreference(uiPreferences, preferences, editor, UiPreferences.opened, Preferences.Deleted.appOpenedCounter);
            migratePreference(uiPreferences, preferences, editor, UiPreferences.appVersion, Preferences.Deleted.appVersion);
            version.putDefault(editor);
            editor.apply();
        }
    }

    private static <T> void migratePreference(@NonNull SharedPreferences uiPreferences, @Nonnull SharedPreferences preferences, @NonNull SharedPreferences.Editor uiEditor, @NonNull Preference<T> uiPreference, @NonNull Preference<T> preference) {
        if (!preference.isSet(preferences)) {
            return;
        }
        if (!uiPreference.isSet(uiPreferences)) {
            uiPreference.putPreference(uiEditor, preference.getPreferenceNoError(preferences));
        }
    }

    private static int getVersion(@NonNull SharedPreferences uiPreferences) {
        if (version.isSet(uiPreferences)) {
            return version.getPreference(uiPreferences);
        }
        return 0;
    }

    public boolean isShowFixableErrorDialog() {
        return showFixableErrorDialog;
    }

    public void setShowFixableErrorDialog(boolean showFixableErrorDialog) {
        this.showFixableErrorDialog = showFixableErrorDialog;
    }

    public static final class Converter {
        @NonNull
        public static final Preference<Integer> lastDimension = IntegerPreference.of("converter.lastDimension", -1);
        @NonNull
        public static final Preference<Integer> lastUnitsFrom = IntegerPreference.of("converter.lastUnitsFrom", -1);
        @NonNull
        public static final Preference<Integer> lastUnitsTo = IntegerPreference.of("converter.lastUnitsTo", -1);
    }
}
