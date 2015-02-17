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

package org.solovyev.android.calculator.onscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.annotation.Nonnull;

import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.Preferences;

/**
 * User: serso
 * Date: 11/20/12
 * Time: 11:05 PM
 */
public final class CalculatorOnscreenBroadcastReceiver extends BroadcastReceiver {

	public CalculatorOnscreenBroadcastReceiver() {
	}

	@Override
	public void onReceive(@Nonnull Context context,
						  @Nonnull Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			if (Preferences.OnscreenCalculator.startOnBoot.getPreferenceNoError(preferences)) {
				CalculatorOnscreenService.showNotification(context);
				App.getGa().onBootStart();
			}
		} else {
			final Intent newIntent = new Intent(intent);
			newIntent.setClass(context, CalculatorOnscreenService.class);
			context.startService(newIntent);
		}
	}
}
