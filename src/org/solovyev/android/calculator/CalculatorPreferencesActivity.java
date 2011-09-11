package org.solovyev.android.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * User: serso
 * Date: 7/16/11
 * Time: 6:37 PM
 */
public class CalculatorPreferencesActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);

		final Preference dragButtonCalibration = findPreference("dragButtonCalibration");
		dragButtonCalibration.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(CalculatorPreferencesActivity.this, DragButtonCalibrationActivity.class));
				return true;
			}
		});
	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
	}
}
