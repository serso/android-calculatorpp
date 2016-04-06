package org.solovyev.android.calculator.ga;

import android.app.Application;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import org.solovyev.android.calculator.Preferences;
import org.solovyev.android.calculator.R;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class Ga implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int LAYOUT = 1;
    private static final int THEME = 2;

    @Nonnull
    private final GoogleAnalytics analytics;

    @Nonnull
    private final Tracker tracker;

    @Inject
    public Ga(@Nonnull Application application, @Nonnull SharedPreferences preferences) {
        analytics = GoogleAnalytics.getInstance(application);
        tracker = analytics.newTracker(R.xml.ga);
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void reportLayout(@Nonnull Preferences.Gui.Mode mode) {
        tracker.send(new HitBuilders.EventBuilder().setCustomDimension(LAYOUT, mode.name()).build());
    }

    private void reportTheme(@Nonnull Preferences.Gui.Theme theme) {
        tracker.send(new HitBuilders.EventBuilder().setCustomDimension(THEME, theme.name()).build());
    }

    @Nonnull
    public GoogleAnalytics getAnalytics() {
        return analytics;
    }

    @Nonnull
    public Tracker getTracker() {
        return tracker;
    }

    public void onButtonPressed(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }

        final HitBuilders.EventBuilder b = new HitBuilders.EventBuilder();
        b.setCategory("ui");
        b.setAction("click");
        b.setLabel(text);
        tracker.send(b.build());
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
        final HitBuilders.EventBuilder b = new HitBuilders.EventBuilder();
        b.setCategory("lifecycle");
        b.setAction("floating_calculator");
        b.setLabel("start");
        tracker.send(b.build());
    }
}
