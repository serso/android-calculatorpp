package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.SharedPreferences;
import jscl.NumeralBase;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.model.CalculatorEngine;

/**
 * User: serso
 * Date: 4/20/12
 * Time: 5:03 PM
 */
public class NumeralBaseButtons {

    public synchronized void toggleNumericDigits(@NotNull Activity activity, @NotNull NumeralBase currentNumeralBase) {
        for (NumeralBase numeralBase : NumeralBase.values()) {
            if ( currentNumeralBase != numeralBase ) {
                AndroidNumeralBase.valueOf(numeralBase).toggleButtons(false, activity);
            }
        }

        AndroidNumeralBase.valueOf(currentNumeralBase).toggleButtons(true, activity);
    }

    public synchronized void toggleNumericDigits(@NotNull Activity activity, @NotNull SharedPreferences preferences) {
        final NumeralBase nb = CalculatorEngine.Preferences.numeralBase.getPreference(preferences);
        this.toggleNumericDigits(activity, nb);
    }
}
