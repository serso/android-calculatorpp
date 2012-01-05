/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import net.robotmedia.billing.BillingController;
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

		final Preference addFreePreference = findPreference(CalculatorApplication.AD_FREE_P_KEY);

		if (!CalculatorApplication.isAdFree(this)) {
			addFreePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				public boolean onPreferenceClick(Preference preference) {

					// check billing availability
					if (BillingController.checkBillingSupported(CalculatorPreferencesActivity.this) != BillingController.BillingStatus.SUPPORTED) {
						// warn about not supported billing
						new AlertDialog.Builder(CalculatorPreferencesActivity.this).setTitle(R.string.c_error).setMessage(R.string.c_billing_error).create().show();
					} else {
						if (!CalculatorApplication.isAdFree(CalculatorPreferencesActivity.this)) {
							// not purchased => show purchase window for user
							BillingController.requestPurchase(CalculatorPreferencesActivity.this, CalculatorApplication.AD_FREE_PRODUCT_ID, true);
						}
					}

					return true;
				}
			});
		} else {
			addFreePreference.setEnabled(false);
		}

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
