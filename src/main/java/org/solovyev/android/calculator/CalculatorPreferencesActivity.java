/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.view.widgets.VibratorContainer;

/**
 * User: serso
 * Date: 7/16/11
 * Time: 6:37 PM
 */
public class CalculatorPreferencesActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.main_preferences);

		final SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
		preferences.registerOnSharedPreferenceChangeListener(this);
		onSharedPreferenceChanged(preferences, CalculatorEngine.Preferences.roundResult.getKey());
		onSharedPreferenceChanged(preferences, VibratorContainer.HAPTIC_FEEDBACK_P_KEY);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
		if (CalculatorEngine.Preferences.roundResult.getKey().equals(key)) {
			findPreference(CalculatorEngine.Preferences.roundResult.getKey()).setEnabled(preferences.getBoolean(key, CalculatorEngine.Preferences.roundResult.getDefaultValue()));
		} else if (VibratorContainer.HAPTIC_FEEDBACK_P_KEY.equals(key)) {
			findPreference(VibratorContainer.HAPTIC_FEEDBACK_DURATION_P_KEY).setEnabled(preferences.getBoolean(key, VibratorContainer.HAPTIC_FEEDBACK_DEFAULT));
		}
	}
}
