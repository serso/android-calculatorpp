package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.SharedPreferences;
import jscl.NumeralBase;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;

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
        if (CalculatorPreferences.Gui.hideNumeralBaseDigits.getPreference(preferences)) {
            final NumeralBase nb = AndroidCalculatorEngine.Preferences.numeralBase.getPreference(preferences);
            this.toggleNumericDigits(activity, nb);
        } else {
            // set HEX to show all digits
            AndroidNumeralBase.valueOf(NumeralBase.hex).toggleButtons(true, activity);
        }
    }
}
