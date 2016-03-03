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

package org.solovyev.android.calculator.floating;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.solovyev.android.calculator.Preferences;
import org.solovyev.android.calculator.ga.Ga;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static org.solovyev.android.calculator.App.cast;

public final class FloatingCalculatorBroadcastReceiver extends BroadcastReceiver {

    @Inject
    SharedPreferences preferences;
    @Inject
    Ga ga;

    public FloatingCalculatorBroadcastReceiver() {
    }

    @Override
    public void onReceive(@Nonnull Context context,
                          @Nonnull Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            cast(context).getComponent().inject(this);
            if (Preferences.Onscreen.startOnBoot.getPreferenceNoError(preferences)) {
                FloatingCalculatorService.showNotification(context);
                ga.onBootStart();
            }
        } else {
            final Intent newIntent = new Intent(intent);
            newIntent.setClass(context, FloatingCalculatorService.class);
            context.startService(newIntent);
        }
    }
}
