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
import org.solovyev.android.msg.AndroidMessage;
import org.solovyev.common.msg.MessageType;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;

import static org.solovyev.android.calculator.Preferences.Gui.lastPreferredPreferencesCheck;

@Singleton
public class PreferredPreferences {

    // one hour
    private static final Long PREFERRED_PREFS_INTERVAL_TIME = 1000L * 60L * 60L;

    @Inject
    Application application;
    @Inject
    SharedPreferences preferences;

    @Inject
    public PreferredPreferences() {
    }

    public void check(boolean force) {
        check(application, force);
    }

    public void check(@Nonnull Context context, boolean force) {
        final long now = System.currentTimeMillis();

        if (!force) {
            if (!Preferences.Gui.showFixableErrorDialog.getPreference(preferences)) {
                // user has disabled calculation message dialogs until the next session
                return;
            }
            if (!shouldCheck(now)) {
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

        lastPreferredPreferencesCheck.putPreference(preferences, now);
    }

    private boolean shouldCheck(long now) {
        final long lastCheckTime = lastPreferredPreferencesCheck.getPreference(preferences);
        return now - lastCheckTime > PREFERRED_PREFS_INTERVAL_TIME;
    }

    public void setPreferredAngleUnits() {
        setAngleUnits(Preferences.Calculations.preferredAngleUnits.getPreference(preferences));
    }

    public void setAngleUnits(@Nonnull AngleUnit angleUnit) {
        Engine.Preferences.angleUnit.putPreference(preferences, angleUnit);
        Locator.getInstance().getNotifier().showMessage(new AndroidMessage(R.string.c_angle_units_changed_to, MessageType.info, application, angleUnit.name()));
    }

    public void setPreferredNumeralBase() {
        setNumeralBase(Preferences.Calculations.preferredNumeralBase.getPreference(preferences));
    }

    public void setNumeralBase(@Nonnull NumeralBase numeralBase) {
        Engine.Preferences.numeralBase.putPreference(preferences, numeralBase);
        Locator.getInstance().getNotifier().showMessage(new AndroidMessage(R.string.c_numeral_base_changed_to, MessageType.info, application, numeralBase.name()));
    }
}
