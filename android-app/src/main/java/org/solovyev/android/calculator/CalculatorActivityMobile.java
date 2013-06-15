package org.solovyev.android.calculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 11/25/12
 * Time: 2:34 PM
 */
public class CalculatorActivityMobile extends CalculatorActivity {

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		CalculatorPreferences.Gui.layout.putPreference(prefs, CalculatorPreferences.Gui.Layout.main_calculator_mobile);

		super.onCreate(savedInstanceState);

		if (!CalculatorApplication.isMonkeyRunner(this)) {
			this.finish();
		}
	}
}
