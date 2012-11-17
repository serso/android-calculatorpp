package org.solovyev.android.calculator;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import jscl.AngleUnit;
import jscl.NumeralBase;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;
import org.solovyev.android.msg.AndroidMessage;
import org.solovyev.common.msg.MessageType;

import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 11/17/12
 * Time: 7:46 PM
 */
public class AndroidCalculatorPreferenceService implements CalculatorPreferenceService {

    // ont hour
    private static final Long PREFERRED_PREFS_INTERVAL_TIME = 1000L * 60L * 60L;

    @NotNull
    private final Application application;

    public AndroidCalculatorPreferenceService(@NotNull Application application) {
        this.application = application;
    }

    @Override
    public void checkPreferredPreferences(boolean force) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application);

        final Long currentTime = System.currentTimeMillis();

        if ( force || ( CalculatorPreferences.Calculations.showCalculationMessagesDialog.getPreference(prefs) && isTimeForCheck(currentTime, prefs))) {
            final NumeralBase preferredNumeralBase = CalculatorPreferences.Calculations.preferredNumeralBase.getPreference(prefs);
            final NumeralBase numeralBase = AndroidCalculatorEngine.Preferences.numeralBase.getPreference(prefs);

            final AngleUnit preferredAngleUnits = CalculatorPreferences.Calculations.preferredAngleUnits.getPreference(prefs);
            final AngleUnit angleUnits = AndroidCalculatorEngine.Preferences.angleUnit.getPreference(prefs);

            final List<CalculatorFixableMessage> messages = new ArrayList<CalculatorFixableMessage>(2);
            if ( numeralBase != preferredNumeralBase ) {
                messages.add(new CalculatorFixableMessage(application.getString(R.string.preferred_numeral_base_message, preferredNumeralBase.name(), numeralBase.name()), MessageType.warning, CalculatorFixableError.preferred_numeral_base));
            }

            if ( angleUnits != preferredAngleUnits ) {
                messages.add(new CalculatorFixableMessage(application.getString(R.string.preferred_angle_units_message, preferredAngleUnits.name(), angleUnits.name()), MessageType.warning, CalculatorFixableError.preferred_angle_units));
            }

            CalculatorMessagesDialog.showDialog(messages, application);

            CalculatorPreferences.Calculations.lastPreferredPreferencesCheck.putPreference(prefs, currentTime);
        }
    }

    private boolean isTimeForCheck(@NotNull Long currentTime, @NotNull SharedPreferences preferences) {
        final Long lastPreferredPreferencesCheckTime = CalculatorPreferences.Calculations.lastPreferredPreferencesCheck.getPreference(preferences);

        return currentTime - lastPreferredPreferencesCheckTime > PREFERRED_PREFS_INTERVAL_TIME;
    }

    @Override
    public void setPreferredAngleUnits() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(application);
        setAngleUnits(CalculatorPreferences.Calculations.preferredAngleUnits.getPreference(preferences));
    }

    @Override
    public void setAngleUnits(@NotNull AngleUnit angleUnit) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(application);
        AndroidCalculatorEngine.Preferences.angleUnit.putPreference(preferences, angleUnit);

        CalculatorLocatorImpl.getInstance().getNotifier().showMessage(new AndroidMessage(R.string.c_angle_units_changed_to, MessageType.info, application, angleUnit.name()));
    }

    @Override
    public void setPreferredNumeralBase() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(application);
        setNumeralBase(CalculatorPreferences.Calculations.preferredNumeralBase.getPreference(preferences));
    }

    @Override
    public void setNumeralBase(@NotNull NumeralBase numeralBase) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(application);
        AndroidCalculatorEngine.Preferences.numeralBase.putPreference(preferences, numeralBase);

        CalculatorLocatorImpl.getInstance().getNotifier().showMessage(new AndroidMessage(R.string.c_numeral_base_changed_to, MessageType.info, application, numeralBase.name()));
    }
}
