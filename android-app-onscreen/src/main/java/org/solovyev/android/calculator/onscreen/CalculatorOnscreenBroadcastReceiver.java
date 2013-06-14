package org.solovyev.android.calculator.onscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.CalculatorPreferences;

/**
 * User: serso
 * Date: 11/20/12
 * Time: 11:05 PM
 */
public final class CalculatorOnscreenBroadcastReceiver extends BroadcastReceiver {

	public CalculatorOnscreenBroadcastReceiver() {
	}

	@Override
	public void onReceive(@NotNull Context context,
						  @NotNull Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			if (CalculatorPreferences.OnscreenCalculator.startOnBoot.getPreferenceNoError(preferences)) {
				CalculatorOnscreenService.showNotification(context);
			}
		} else {
			final Intent newIntent = new Intent(intent);
			newIntent.setClass(context, CalculatorOnscreenService.class);
			context.startService(newIntent);
		}
	}
}
