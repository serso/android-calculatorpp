/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.view.widgets;

import android.content.SharedPreferences;
import android.os.Vibrator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 10/26/11
 * Time: 11:40 PM
 */
public class VibratorContainer implements SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String HAPTIC_FEEDBACK_PREFERENCE = "org.solovyev.android.calculator.CalculatorModel_haptic_feedback";

	private final long defaultVibrationTime;

	@Nullable
	private final Vibrator vibrator;

	private long time = 0;

	public VibratorContainer(@Nullable Vibrator vibrator, @NotNull SharedPreferences preferences, long defaultVibrationTime) {
		this.vibrator = vibrator;
		this.defaultVibrationTime = defaultVibrationTime;

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
		if (preferences.getBoolean(HAPTIC_FEEDBACK_PREFERENCE, false)) {
			this.time = defaultVibrationTime;
		} else {
			this.time = 0;
		}
	}
}
