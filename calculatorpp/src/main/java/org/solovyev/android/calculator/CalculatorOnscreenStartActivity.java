package org.solovyev.android.calculator;

import android.app.Activity;
import android.os.Bundle;
import org.solovyev.android.calculator.onscreen.CalculatorOnscreenService;

public class CalculatorOnscreenStartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        CalculatorOnscreenService.showOnscreenView(this);

		this.finish();
	}
}
