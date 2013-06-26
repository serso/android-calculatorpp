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
