package org.solovyev.android.calculator;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import org.solovyev.android.prefs.BooleanPreference;
import org.solovyev.android.prefs.IntegerPreference;
import org.solovyev.android.prefs.Preference;

import javax.annotation.Nonnull;

final class UiPreferences {
    @NonNull
    public static final Preference<Integer> opened = IntegerPreference.of("opened", 0);
    @NonNull
    public static final Preference<Integer> version = IntegerPreference.of("version", 1);
    @NonNull
    public static final Preference<Integer> appVersion = IntegerPreference.of("appVersion", IntegerPreference.DEF_VALUE);
    @NonNull
    public static final Preference<Boolean> rateUsShown = BooleanPreference.of("rateUsShown", false);

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
}
