/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;
import net.robotmedia.billing.BillingController;
import net.robotmedia.billing.BillingRequest;
import net.robotmedia.billing.IBillingObserver;
import net.robotmedia.billing.model.Transaction;
import org.solovyev.android.AndroidUtils;
import org.solovyev.android.ads.AdsController;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.view.VibratorContainer;

/**
 * User: serso
 * Date: 7/16/11
 * Time: 6:37 PM
 */
public class CalculatorPreferencesActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener, IBillingObserver {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.main_preferences);

		final Preference adFreePreference = findPreference(CalculatorApplication.AD_FREE_P_KEY);
		adFreePreference.setEnabled(false);

		// observer must be set before net.robotmedia.billing.BillingController.checkBillingSupported()
		BillingController.registerObserver(this);

		// check billing support one more time as user can turn on internet while he is in current activity
		switch (BillingController.checkBillingSupported(CalculatorPreferencesActivity.this)) {
			case UNKNOWN:
				// unknown => will wait the invocation of onBillingChecked()
				Log.d(CalculatorPreferencesActivity.class.getName(), "Billing state in unknown - waiting!");
				break;
			case SUPPORTED:
				onBillingChecked(true);
				break;
			case UNSUPPORTED:
				 onBillingChecked(false);
				break;
		}

		final SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
		preferences.registerOnSharedPreferenceChangeListener(this);
		onSharedPreferenceChanged(preferences, CalculatorEngine.Preferences.roundResult.getKey());
		onSharedPreferenceChanged(preferences, VibratorContainer.HAPTIC_FEEDBACK_P_KEY);
	}

	private void setAdFreeAction() {
		final Preference adFreePreference = findPreference(CalculatorApplication.AD_FREE_P_KEY);

		if (!AdsController.getInstance().isAdFree(this)) {
			Log.d(CalculatorPreferencesActivity.class.getName(), "Ad free is not purchased - enable preference!");

			adFreePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				public boolean onPreferenceClick(Preference preference) {

					// check billing availability
					if (BillingController.checkBillingSupported(CalculatorPreferencesActivity.this) != BillingController.BillingStatus.SUPPORTED) {
						Log.d(CalculatorPreferencesActivity.class.getName(), "Billing is not supported - warn user!");
						// warn about not supported billing
						new AlertDialog.Builder(CalculatorPreferencesActivity.this).setTitle(R.string.c_error).setMessage(R.string.c_billing_error).create().show();
					} else {
						Log.d(CalculatorPreferencesActivity.class.getName(), "Billing is supported - continue!");
						if (!AdsController.getInstance().isAdFree(CalculatorPreferencesActivity.this)) {
							Log.d(CalculatorPreferencesActivity.class.getName(), "Item not purchased - try to purchase!");

							// not purchased => purchasing
							Toast.makeText(CalculatorPreferencesActivity.this, R.string.c_calc_purchasing, Toast.LENGTH_SHORT).show();

							// show purchase window for user
							BillingController.requestPurchase(CalculatorPreferencesActivity.this, CalculatorApplication.AD_FREE_PRODUCT_ID, true);
						} else {
							// disable preference
							adFreePreference.setEnabled(false);
							// and show message to user
							Toast.makeText(CalculatorPreferencesActivity.this, R.string.c_calc_already_purchased, Toast.LENGTH_SHORT).show();
						}
					}

					return true;
				}
			});
			adFreePreference.setEnabled(true);
		} else {
			Log.d(CalculatorPreferencesActivity.class.getName(), "Ad free is not purchased - disable preference!");
			adFreePreference.setEnabled(false);
		}
	}

	@Override
	protected void onDestroy() {
		BillingController.unregisterObserver(this);
		super.onDestroy();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
		if (CalculatorEngine.Preferences.roundResult.getKey().equals(key)) {
			findPreference(CalculatorEngine.Preferences.precision.getKey()).setEnabled(preferences.getBoolean(key, CalculatorEngine.Preferences.roundResult.getDefaultValue()));
		} else if (VibratorContainer.HAPTIC_FEEDBACK_P_KEY.equals(key)) {
			findPreference(VibratorContainer.HAPTIC_FEEDBACK_DURATION_P_KEY).setEnabled(preferences.getBoolean(key, VibratorContainer.HAPTIC_FEEDBACK_DEFAULT));
		}
	}

	@Override
	public void onBillingChecked(boolean supported) {
		if ( supported ) {
			setAdFreeAction();
		} else {
			final Preference adFreePreference = findPreference(CalculatorApplication.AD_FREE_P_KEY);
			adFreePreference.setEnabled(false);
			Log.d(CalculatorPreferencesActivity.class.getName(), "Billing is not supported!");
		}
	}

	@Override
	public void onPurchaseIntent(String itemId, PendingIntent purchaseIntent) {
		// do nothing
	}

	@Override
	public void onPurchaseStateChanged(String itemId, Transaction.PurchaseState state) {
		if (CalculatorApplication.AD_FREE_PRODUCT_ID.equals(itemId)) {
			final Preference adFreePreference = findPreference(CalculatorApplication.AD_FREE_P_KEY);
			if (adFreePreference != null) {
				switch (state) {
					case PURCHASED:
						adFreePreference.setEnabled(false);
						// restart activity to disable ads
						AndroidUtils.restartActivity(this);
						break;
					case CANCELLED:
						adFreePreference.setEnabled(true);
						break;
					case REFUNDED:
						adFreePreference.setEnabled(true);
						break;
				}
			} else {
			}
		}
	}

	@Override
	public void onRequestPurchaseResponse(String itemId, BillingRequest.ResponseCode response) {
		// do nothing
	}

	@Override
	public void onTransactionsRestored() {
		// do nothing
	}
}
