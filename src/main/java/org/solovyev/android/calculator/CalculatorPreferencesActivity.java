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
import net.robotmedia.billing.BillingController;
import net.robotmedia.billing.BillingRequest;
import net.robotmedia.billing.IBillingObserver;
import net.robotmedia.billing.model.Transaction;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.view.widgets.VibratorContainer;

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

		final Preference addFreePreference = findPreference(CalculatorApplication.AD_FREE_P_KEY);

		if (!CalculatorApplication.isAdFree(this)) {
			addFreePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				public boolean onPreferenceClick(Preference preference) {

					// check billing availability
					if (BillingController.checkBillingSupported(CalculatorPreferencesActivity.this) != BillingController.BillingStatus.SUPPORTED) {
						Log.d(CalculatorPreferencesActivity.class.getName(), "Billing is not supported - warn user!");
						// warn about not supported billing
						new AlertDialog.Builder(CalculatorPreferencesActivity.this).setTitle(R.string.c_error).setMessage(R.string.c_billing_error).create().show();
					} else {
						Log.d(CalculatorPreferencesActivity.class.getName(), "Billing is supported - continue!");
						if (!CalculatorApplication.isAdFree(CalculatorPreferencesActivity.this)) {
							Log.d(CalculatorPreferencesActivity.class.getName(), "Item not purchased - try to purchase!");
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

		BillingController.registerObserver(this);

		final SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
		preferences.registerOnSharedPreferenceChangeListener(this);
		onSharedPreferenceChanged(preferences, CalculatorEngine.Preferences.roundResult.getKey());
		onSharedPreferenceChanged(preferences, VibratorContainer.HAPTIC_FEEDBACK_P_KEY);
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
		// do nothing
	}

	@Override
	public void onPurchaseIntent(String itemId, PendingIntent purchaseIntent) {
		// do nothing
	}

	@Override
	public void onPurchaseStateChanged(String itemId, Transaction.PurchaseState state) {
		if (CalculatorApplication.AD_FREE_PRODUCT_ID.equals(itemId)) {
			final Preference addFreePreference = findPreference(CalculatorApplication.AD_FREE_P_KEY);
			if (addFreePreference != null) {
				switch (state) {
					case PURCHASED:
						addFreePreference.setEnabled(false);
						break;
					case CANCELLED:
						addFreePreference.setEnabled(true);
						break;
					case REFUNDED:
						addFreePreference.setEnabled(true);
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
