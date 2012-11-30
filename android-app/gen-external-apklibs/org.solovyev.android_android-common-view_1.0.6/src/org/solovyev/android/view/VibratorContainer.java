/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.view;

import android.content.SharedPreferences;
import android.os.Vibrator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.prefs.BooleanPreference;
import org.solovyev.android.prefs.NumberToStringPreference;
import org.solovyev.android.prefs.Preference;

/**
 * User: serso
 * Date: 10/26/11
 * Time: 11:40 PM
 */
public class VibratorContainer implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static class Preferences {
        public static final Preference<Boolean> hapticFeedbackEnabled = new BooleanPreference("org.solovyev.android.calculator.CalculatorModel_haptic_feedback", false);
        public static final Preference<Long> hapticFeedbackDuration = new NumberToStringPreference<Long>("org.solovyev.android.calculator.CalculatorActivity_calc_haptic_feedback_duration_key", 60L, Long.class);
    }

	private final float vibrationTimeScale;

	@Nullable
	private final Vibrator vibrator;

	private long time = 0;

	public VibratorContainer(@Nullable Vibrator vibrator, @NotNull SharedPreferences preferences, float vibrationTimeScale) {
		this.vibrator = vibrator;
		this.vibrationTimeScale = vibrationTimeScale;

		preferences.registerOnSharedPreferenceChangeListener(this);
		onSharedPreferenceChanged(preferences, null);

	}

	public void vibrate() {
		if (time > 0 && vibrator != null) {
			vibrator.vibrate(time);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences preferences, @Nullable String key) {
		if ( Preferences.hapticFeedbackEnabled.getPreference(preferences)) {
			//noinspection ConstantConditions
			this.time = getScaledValue(Preferences.hapticFeedbackDuration.getPreference(preferences));
		} else {
			this.time = 0;
		}
	}

	private long getScaledValue(long vibrationTime) {
		return (long) (vibrationTime * vibrationTimeScale);
	}
}
