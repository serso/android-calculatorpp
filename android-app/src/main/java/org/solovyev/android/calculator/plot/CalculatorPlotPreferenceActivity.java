package org.solovyev.android.calculator.plot;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import org.solovyev.android.calculator.R;

/**
 * User: serso
 * Date: 10/4/12
 * Time: 9:01 PM
 */
public class CalculatorPlotPreferenceActivity extends SherlockPreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//noinspection deprecation
		addPreferencesFromResource(R.xml.preferences_plot);
	}
}
