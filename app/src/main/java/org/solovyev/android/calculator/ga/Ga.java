package org.solovyev.android.calculator.ga;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.solovyev.android.calculator.Preferences;
import org.solovyev.android.calculator.R;
import org.solovyev.common.listeners.JEvent;
import org.solovyev.common.listeners.JEventListener;
import org.solovyev.common.listeners.JEventListeners;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Ga implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int LAYOUT = 1;
    private static final int THEME = 2;

    @Nonnull
    private final GoogleAnalytics analytics;

    @Nonnull
    private final Tracker tracker;

    public Ga(@Nonnull Context context, @Nonnull SharedPreferences preferences, @Nonnull JEventListeners<JEventListener<? extends JEvent>, JEvent> bus) {
        analytics = GoogleAnalytics.getInstance(context);
        tracker = analytics.newTracker(R.xml.ga);
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Nonnull
    private String getStackTrace(@Nonnull Exception e) {
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(out));
            return new String(out.toByteArray());
        } catch (Exception e1) {
            Log.e("Ga", e1.getMessage(), e1);
        }
        return "";
    }

    private void reportLayout(@Nonnull Preferences.Gui.Layout layout) {
        tracker.send(new HitBuilders.EventBuilder().setCustomDimension(LAYOUT, layout.name()).build());
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
        if (TextUtils.equals(key, Preferences.Gui.layout.getKey())) {
            reportLayout(Preferences.Gui.layout.getPreferenceNoError(preferences));
        } else if (TextUtils.equals(key, Preferences.Gui.theme.getKey())) {
            reportTheme(Preferences.Gui.theme.getPreferenceNoError(preferences));
        }
    }

    public void reportInitially(@Nonnull SharedPreferences preferences) {
        reportLayout(Preferences.Gui.layout.getPreferenceNoError(preferences));
        reportTheme(Preferences.Gui.theme.getPreferenceNoError(preferences));
    }

    public void onBootStart() {
        final HitBuilders.EventBuilder b = new HitBuilders.EventBuilder();
        b.setCategory("lifecycle");
        b.setAction("boot");
        tracker.send(b.build());
    }

    public void onFloatingCalculatorOpened() {
        final HitBuilders.EventBuilder b = new HitBuilders.EventBuilder();
        b.setCategory("lifecycle");
        b.setAction("floating_calculator");
        b.setLabel("start");
        tracker.send(b.build());
    }
}
