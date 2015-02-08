/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator.preferences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.util.SparseArray;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.CalculatorApplication;
import org.solovyev.android.calculator.R;

import javax.annotation.Nonnull;

import static org.solovyev.android.calculator.CalculatorApplication.AD_FREE_P_KEY;
import static org.solovyev.android.calculator.model.AndroidCalculatorEngine.Preferences.precision;
import static org.solovyev.android.calculator.model.AndroidCalculatorEngine.Preferences.roundResult;
import static org.solovyev.android.calculator.wizard.CalculatorWizards.DEFAULT_WIZARD_FLOW;
import static org.solovyev.android.view.VibratorContainer.Preferences.hapticFeedbackDuration;
import static org.solovyev.android.view.VibratorContainer.Preferences.hapticFeedbackEnabled;
import static org.solovyev.android.wizard.WizardUi.startWizard;

@SuppressWarnings("deprecation")
public class PreferencesActivity extends BasePreferencesActivity {

	@Nonnull
	private static final SparseArray<String> preferences = new SparseArray<String>();

	static {
		preferences.append(R.xml.preferences, "screen-main");
		preferences.append(R.xml.preferences_calculations, "screen-calculations");
		preferences.append(R.xml.preferences_appearance, "screen-appearance");
		preferences.append(R.xml.preferences_plot, "screen-plot");
		preferences.append(R.xml.preferences_other, "screen-other");
		preferences.append(R.xml.preferences_onscreen, "screen-onscreen");
	}

	private Preference adFreePreference;

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = getIntent();
		final int preference = intent.getIntExtra("preference", R.xml.preferences);
		final String title = intent.getStringExtra("preference-title");
		setPreference(preference, preferences.get(preference));
		if (preference == R.xml.preferences) {
			for (int i = 0; i < preferences.size(); i++) {
				final int xml = preferences.keyAt(i);
				final String name = preferences.valueAt(i);
				setPreferenceIntent(xml, name);
			}
			final Preference restartWizardPreference = findPreference("restart_wizard");
			restartWizardPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					startWizard(CalculatorApplication.getInstance().getWizards(), DEFAULT_WIZARD_FLOW, PreferencesActivity.this);
					return true;
				}
			});

			adFreePreference = findPreference(AD_FREE_P_KEY);
			adFreePreference.setEnabled(false);
			adFreePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					startActivity(new Intent(PreferencesActivity.this, PurchaseDialogActivity.class));
					return true;
				}
			});
		}
		if (title != null) {
			setTitle(title);
		}

		final SharedPreferences preferences = App.getPreferences();
		onSharedPreferenceChanged(preferences, roundResult.getKey());
		onSharedPreferenceChanged(preferences, hapticFeedbackEnabled.getKey());
	}

	private void setPreference(int xml, @Nonnull String name) {
		addPreferencesFromResource(xml);
	}

	private void setPreferenceIntent(int xml, @Nonnull String name) {
		final Preference preference = findPreference(name);
		if (preference != null) {
			final Intent intent = new Intent(getApplicationContext(), PreferencesActivity.class);
			intent.putExtra("preference", xml);
			intent.putExtra("preference-title", preference.getTitle());
			preference.setIntent(intent);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
		super.onSharedPreferenceChanged(preferences, key);
		if (roundResult.getKey().equals(key)) {
			final Preference preference = findPreference(precision.getKey());
			if (preference != null) {
				preference.setEnabled(preferences.getBoolean(key, roundResult.getDefaultValue()));
			}
		} else if (hapticFeedbackEnabled.getKey().equals(key)) {
			final Preference preference = findPreference(hapticFeedbackDuration.getKey());
			if (preference != null) {
				preference.setEnabled(hapticFeedbackEnabled.getPreference(preferences));
			}
		}
	}

	@Override
	protected void onShowAd(boolean show) {
		super.onShowAd(show);
		if (adFreePreference != null) {
			adFreePreference.setEnabled(show);
		}
	}
}
