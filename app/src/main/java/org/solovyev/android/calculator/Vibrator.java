package org.solovyev.android.calculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import javax.annotation.Nonnull;

public final class Vibrator implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Nonnull
    private final android.os.Vibrator service;

    private long time = 0;

    public Vibrator(@Nonnull Context context, @Nonnull SharedPreferences preferences) {
        service = (android.os.Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        preferences.registerOnSharedPreferenceChangeListener(this);
        updateTime(preferences);
    }

    private void updateTime(@Nonnull SharedPreferences preferences) {
        time = Preferences.Gui.hapticFeedback.getPreference(preferences) / 2;
    }

    public void vibrate() {
        try {
            if (time > 0) {
                service.vibrate(time);
            }
        } catch (SecurityException e) {
            Log.e("Vibrator", e.getMessage(), e);
        }
    }

    @Override
    public void onSharedPreferenceChanged(@Nonnull SharedPreferences preferences, String key) {
        if (Preferences.Gui.hapticFeedback.isSameKey(key)) {
            updateTime(preferences);
        }
    }
}
