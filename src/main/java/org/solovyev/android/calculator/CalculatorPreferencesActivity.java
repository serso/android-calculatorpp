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

		/*final Preference buyPref = findPreference(ApplicationContext.AD_FREE_APPLICATION_P_KEY);
		buyPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			// при нажатии на кнопку Убрать рекламу в настройках
			public boolean onPreferenceClick(Preference preference) {
				// проверяем поддерживается ли покупка в приложениях
				if (BillingController.checkBillingSupported(CalculatorPreferencesActivity.this) != BillingController.BillingStatus.SUPPORTED) {
					// показываем сообщение, что покупка не поддерживается
					new AlertDialog.Builder(CalculatorPreferencesActivity.this).setTitle(R.string.c_error).setMessage(R.string.c_billing_error).create().show();
				} else {
					// проверяем не купил ли пользователь уже нашу опцию
					boolean purchased = BillingController.isPurchased(getApplicationContext(), ApplicationContext.AD_FREE_APPLICATION);
					if (!purchased) {
						// если не купил (или мы просто об этом пока не знаем? пользователь удалял
						// приложение со всем данными?), то пытаемся восстановить транзакции
						BillingController.restoreTransactions(CalculatorPreferencesActivity.this);
						// следующая строка (проверка еще раз не купил ли пользователь приложение) -
						// не очень правильный подход - вызвав restoreTransactions,
						// ответ мы получим не сразу
						purchased = BillingController.isPurchased(getApplicationContext(), ApplicationContext.AD_FREE_APPLICATION);
						if (!purchased) {
							// наконец, показываем пользователю стандартное окно для покупки опции
							BillingController.requestPurchase(CalculatorPreferencesActivity.this, ApplicationContext.AD_FREE_APPLICATION);
						}
					}
				}
				return true;
			}
		});*/

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
