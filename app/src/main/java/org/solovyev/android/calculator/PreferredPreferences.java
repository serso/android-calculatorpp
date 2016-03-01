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

package org.solovyev.android.calculator;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import jscl.AngleUnit;
import jscl.NumeralBase;
import org.solovyev.android.calculator.errors.FixableError;
import org.solovyev.android.calculator.errors.FixableErrorType;
import org.solovyev.android.calculator.errors.FixableErrorsActivity;
import org.solovyev.common.msg.MessageType;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Singleton
public class PreferredPreferences {

    private static final long PREFERRED_PREFS_INTERVAL_TIME = TimeUnit.MINUTES.toMillis(15);

    private long lastCheckTime;
    private boolean showWarningDialog = true;

    @Inject
    Application application;
    @Inject
    SharedPreferences preferences;
    @Inject
    Notifier notifier;

    @Inject
    public PreferredPreferences() {
    }

    public void check(boolean force) {
        check(application, force);
    }

    public void check(@Nonnull Context context, boolean force) {
        final long now = System.currentTimeMillis();

        if (!force) {
            if (!showWarningDialog) {
                // user has disabled calculation message dialogs until the next session
                return;
            }
            if (now - lastCheckTime < PREFERRED_PREFS_INTERVAL_TIME) {
                return;
            }
        }

        final NumeralBase preferredNumeralBase = Preferences.Calculations.preferredNumeralBase.getPreference(preferences);
        final NumeralBase numeralBase = Engine.Preferences.numeralBase.getPreference(preferences);

        final AngleUnit preferredAngleUnits = Preferences.Calculations.preferredAngleUnits.getPreference(preferences);
        final AngleUnit angleUnits = Engine.Preferences.angleUnit.getPreference(preferences);

        final ArrayList<FixableError> messages = new ArrayList<>(2);
        if (numeralBase != preferredNumeralBase) {
            messages.add(new FixableError(application.getString(R.string.preferred_numeral_base_message, preferredNumeralBase.name(), numeralBase.name()), MessageType.warning, FixableErrorType.preferred_numeral_base));
        }

        if (angleUnits != preferredAngleUnits) {
            messages.add(new FixableError(application.getString(R.string.preferred_angle_units_message, preferredAngleUnits.name(), angleUnits.name()), MessageType.warning, FixableErrorType.preferred_angle_units));
        }

        FixableErrorsActivity.show(context, messages);
        lastCheckTime = now;
    }

    public void setPreferredAngleUnits() {
        setAngleUnits(Preferences.Calculations.preferredAngleUnits.getPreference(preferences));
    }

    public void setAngleUnits(@Nonnull AngleUnit angleUnit) {
        Engine.Preferences.angleUnit.putPreference(preferences, angleUnit);
        notifier.showMessage(R.string.c_angle_units_changed_to, angleUnit.name());
    }

    public void setPreferredNumeralBase() {
        setNumeralBase(Preferences.Calculations.preferredNumeralBase.getPreference(preferences));
    }

    public void setNumeralBase(@Nonnull NumeralBase numeralBase) {
        Engine.Preferences.numeralBase.putPreference(preferences, numeralBase);
        notifier.showMessage(R.string.c_numeral_base_changed_to, numeralBase.name());
    }

    public boolean isShowWarningDialog() {
        return showWarningDialog;
    }

    public void dontShowWarningDialog() {
        showWarningDialog = false;
    }
}
