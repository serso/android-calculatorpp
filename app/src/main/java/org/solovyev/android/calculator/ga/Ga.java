package org.solovyev.android.calculator.ga;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.solovyev.android.calculator.Preferences;

@Singleton
public final class Ga implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Nonnull
    private final FirebaseAnalytics analytics;

    @Inject
    public Ga(@Nonnull Application application, @Nonnull SharedPreferences preferences) {
        analytics = FirebaseAnalytics.getInstance(application);
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void reportLayout(@Nonnull Preferences.Gui.Mode mode) {
        final Bundle params = new Bundle();
        params.putString("name", mode.name());
        analytics.logEvent("layout", params);
    }

    private void reportTheme(@Nonnull Preferences.Gui.Theme theme) {
        final Bundle params = new Bundle();
        params.putString("name", theme.name());
        analytics.logEvent("theme", params);
    }

    public void onButtonPressed(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }

        final Bundle params = new Bundle();
        params.putString("text", text);
        analytics.logEvent("click", params);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (TextUtils.equals(key, Preferences.Gui.mode.getKey())) {
            reportLayout(Preferences.Gui.mode.getPreferenceNoError(preferences));
        } else if (TextUtils.equals(key, Preferences.Gui.theme.getKey())) {
            reportTheme(Preferences.Gui.theme.getPreferenceNoError(preferences));
        }
    }

    public void reportInitially(@Nonnull SharedPreferences preferences) {
        reportLayout(Preferences.Gui.mode.getPreferenceNoError(preferences));
        reportTheme(Preferences.Gui.theme.getPreferenceNoError(preferences));
    }

    public void onFloatingCalculatorOpened() {
        analytics.logEvent("floating_calculator_open", null);
    }
}
