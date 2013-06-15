/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.preferences;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import net.robotmedia.billing.BillingController;
import net.robotmedia.billing.IBillingObserver;
import net.robotmedia.billing.ResponseCode;
import net.robotmedia.billing.helper.AbstractBillingObserver;
import net.robotmedia.billing.model.Transaction;
import javax.annotation.Nonnull;
import org.solovyev.android.Activities;
import org.solovyev.android.App;
import org.solovyev.android.ads.AdsController;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;
import org.solovyev.android.msg.AndroidMessage;
import org.solovyev.android.view.VibratorContainer;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageType;

/**
 * User: serso
 * Date: 7/16/11
 * Time: 6:37 PM
 */
public class CalculatorPreferencesActivity extends SherlockPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener, IBillingObserver {

	public static final String CLEAR_BILLING_INFO = "clear_billing_info";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//noinspection deprecation
		addPreferencesFromResource(R.xml.preferences);
		//noinspection deprecation
		addPreferencesFromResource(R.xml.preferences_calculations);
		addPreferencesFromResource(R.xml.preferences_appearance);
		addPreferencesFromResource(R.xml.preferences_plot);
		addPreferencesFromResource(R.xml.preferences_other);
		addPreferencesFromResource(R.xml.preferences_onscreen);

		final Preference adFreePreference = findPreference(CalculatorApplication.AD_FREE_P_KEY);
		adFreePreference.setEnabled(false);

		// observer must be set before net.robotmedia.billing.BillingController.checkBillingSupported()
		BillingController.registerObserver(this);

		BillingController.checkBillingSupported(CalculatorPreferencesActivity.this);

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
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

	public static void removeBillingInformation(@Nonnull Context context, @Nonnull SharedPreferences preferences) {
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
					final Context context = CalculatorPreferencesActivity.this;
					context.startActivity(new Intent(context, CalculatorPurchaseDialogActivity.class));
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
		PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
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
	public void onPurchaseIntentOK(@Nonnull String productId, @Nonnull PendingIntent purchaseIntent) {
		// do nothing
	}

	@Override
	public void onPurchaseIntentFailure(@Nonnull String productId, @Nonnull ResponseCode responseCode) {
		// do nothing
	}

	@Override
	public void onPurchaseStateChanged(@Nonnull String itemId, @Nonnull Transaction.PurchaseState state) {
		if (CalculatorApplication.AD_FREE_PRODUCT_ID.equals(itemId)) {
			final Preference adFreePreference = findPreference(CalculatorApplication.AD_FREE_P_KEY);
			if (adFreePreference != null) {
				switch (state) {
					case PURCHASED:
						adFreePreference.setEnabled(false);
						// restart activity to disable ads
						Activities.restartActivity(this);
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
	public void onRequestPurchaseResponse(@Nonnull String itemId, @Nonnull ResponseCode response) {
		final Preference adFreePreference = findPreference(CalculatorApplication.AD_FREE_P_KEY);
		if (adFreePreference != null) {
			if (response == ResponseCode.RESULT_OK) {
				adFreePreference.setEnabled(false);

				final Message message = new AndroidMessage(R.string.cpp_purchase_thank_you_text, MessageType.info, App.getApplication());
				Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_message_dialog, MessageDialogData.newInstance(message, null));
			}
		}
	}

	@Override
	public void onTransactionsRestored() {
		// do nothing
	}

	@Override
	public void onErrorRestoreTransactions(@Nonnull ResponseCode responseCode) {
		// do nothing
	}
}
