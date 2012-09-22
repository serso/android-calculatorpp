/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import net.robotmedia.billing.BillingController;
import net.robotmedia.billing.IBillingObserver;
import net.robotmedia.billing.ResponseCode;
import net.robotmedia.billing.helper.AbstractBillingObserver;
import net.robotmedia.billing.model.Transaction;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.AndroidUtils;
import org.solovyev.android.ads.AdsController;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;
import org.solovyev.android.view.VibratorContainer;

/**
 * User: serso
 * Date: 7/16/11
 * Time: 6:37 PM
 */
public class CalculatorPreferencesActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener, IBillingObserver {

	public static final String CLEAR_BILLING_INFO = "clear_billing_info";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);

		final Preference adFreePreference = findPreference(CalculatorApplication.AD_FREE_P_KEY);
		adFreePreference.setEnabled(false);

		// observer must be set before net.robotmedia.billing.BillingController.checkBillingSupported()
		BillingController.registerObserver(this);

		BillingController.checkBillingSupported(CalculatorPreferencesActivity.this);

		final SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
		preferences.registerOnSharedPreferenceChangeListener(this);
		onSharedPreferenceChanged(preferences, AndroidCalculatorEngine.Preferences.roundResult.getKey());
		onSharedPreferenceChanged(preferences, VibratorContainer.Preferences.hapticFeedbackEnabled.getKey());

		final Preference clearBillingInfoPreference = findPreference(CLEAR_BILLING_INFO);
		if (clearBillingInfoPreference != null) {
			clearBillingInfoPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {

					Toast.makeText(CalculatorPreferencesActivity.this, R.string.c_calc_clearing, Toast.LENGTH_SHORT).show();

					removeBillingInformation(CalculatorPreferencesActivity.this, PreferenceManager.getDefaultSharedPreferences(CalculatorPreferencesActivity.this));

					return true;
				}
			});
		}
	}

	public static void removeBillingInformation(@NotNull Context context, @NotNull SharedPreferences preferences) {
		final SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(AbstractBillingObserver.KEY_TRANSACTIONS_RESTORED, false);
		editor.commit();

		BillingController.dropBillingData(context);
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
		if (AndroidCalculatorEngine.Preferences.roundResult.getKey().equals(key)) {
			findPreference(AndroidCalculatorEngine.Preferences.precision.getKey()).setEnabled(preferences.getBoolean(key, AndroidCalculatorEngine.Preferences.roundResult.getDefaultValue()));
		} else if (VibratorContainer.Preferences.hapticFeedbackEnabled.getKey().equals(key)) {
			findPreference(VibratorContainer.Preferences.hapticFeedbackDuration.getKey()).setEnabled(VibratorContainer.Preferences.hapticFeedbackEnabled.getPreference(preferences));
		}
	}

	@Override
	public void onCheckBillingSupportedResponse(boolean supported) {
		if (supported) {
			setAdFreeAction();
		} else {
			final Preference adFreePreference = findPreference(CalculatorApplication.AD_FREE_P_KEY);
			adFreePreference.setEnabled(false);
			Log.d(CalculatorPreferencesActivity.class.getName(), "Billing is not supported!");
		}
	}

	@Override
	public void onPurchaseIntentOK(@NotNull String productId, @NotNull PendingIntent purchaseIntent) {
		// do nothing
	}

	@Override
	public void onPurchaseIntentFailure(@NotNull String productId, @NotNull ResponseCode responseCode) {
		// do nothing
	}

	@Override
	public void onPurchaseStateChanged(@NotNull String itemId, @NotNull Transaction.PurchaseState state) {
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
	public void onRequestPurchaseResponse(@NotNull String itemId, @NotNull ResponseCode response) {
		// do nothing
	}

	@Override
	public void onTransactionsRestored() {
		// do nothing
	}

	@Override
	public void onErrorRestoreTransactions(@NotNull ResponseCode responseCode) {
		// do nothing
	}
}
