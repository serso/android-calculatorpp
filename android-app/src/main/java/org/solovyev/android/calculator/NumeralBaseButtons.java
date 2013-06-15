package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.SharedPreferences;
import jscl.NumeralBase;
import javax.annotation.Nonnull;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;

/**
 * User: serso
 * Date: 4/20/12
 * Time: 5:03 PM
 */
public class NumeralBaseButtons {

	public static void toggleNumericDigits(@Nonnull Activity activity, @Nonnull NumeralBase currentNumeralBase) {
		for (NumeralBase numeralBase : NumeralBase.values()) {
			if (currentNumeralBase != numeralBase) {
				AndroidNumeralBase.valueOf(numeralBase).toggleButtons(false, activity);
			}
		}

		AndroidNumeralBase.valueOf(currentNumeralBase).toggleButtons(true, activity);
	}

	public static void toggleNumericDigits(@Nonnull Activity activity, @Nonnull SharedPreferences preferences) {
		if (CalculatorPreferences.Gui.hideNumeralBaseDigits.getPreference(preferences)) {
			final NumeralBase nb = AndroidCalculatorEngine.Preferences.numeralBase.getPreference(preferences);
			toggleNumericDigits(activity, nb);
		} else {
			// set HEX to show all digits
			AndroidNumeralBase.valueOf(NumeralBase.hex).toggleButtons(true, activity);
		}
	}
}
