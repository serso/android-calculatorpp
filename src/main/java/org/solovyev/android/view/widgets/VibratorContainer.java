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
import org.solovyev.common.NumberMapper;

/**
 * User: serso
 * Date: 10/26/11
 * Time: 11:40 PM
 */
public class VibratorContainer implements SharedPreferences.OnSharedPreferenceChangeListener {

	public static final String HAPTIC_FEEDBACK_P_KEY = "org.solovyev.android.calculator.CalculatorModel_haptic_feedback";
	public static final boolean HAPTIC_FEEDBACK_DEFAULT = false;
	public static final String HAPTIC_FEEDBACK_DURATION_P_KEY = "org.solovyev.android.calculator.CalculatorActivity_calc_haptic_feedback_duration_key";

	private final static NumberMapper<Long> mapper = new NumberMapper<Long>(Long.class);

	private static final long defaultVibrationTime = 100;

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
		if (preferences.getBoolean(HAPTIC_FEEDBACK_P_KEY, VibratorContainer.HAPTIC_FEEDBACK_DEFAULT)) {
			//noinspection ConstantConditions
			this.time = getScaledValue(mapper.parseValue(preferences.getString(HAPTIC_FEEDBACK_DURATION_P_KEY, mapper.formatValue(getScaledValue(defaultVibrationTime)))));
		} else {
			this.time = 0;
		}
	}

	private long getScaledValue(long vibrationTime) {
		return (long) (vibrationTime * vibrationTimeScale);
	}
}
