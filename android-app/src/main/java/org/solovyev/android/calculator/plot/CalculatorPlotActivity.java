package org.solovyev.android.calculator.plot;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.android.calculator.CalculatorFragmentActivity;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.R;

/**
 * User: serso
 * Date: 9/30/12
 * Time: 4:56 PM
 */
public class CalculatorPlotActivity extends CalculatorFragmentActivity {

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = getIntent();

		final Bundle arguments;
		if (intent != null) {
			arguments = intent.getExtras();
		} else {
			arguments = null;
		}

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getActivityHelper().setFragment(this, getPlotterFragmentType(), arguments, R.id.main_layout);
	}

	@Nonnull
	public static CalculatorFragmentType getPlotterFragmentType() {
		return CalculatorFragmentType.plotter;
	}
}
