package org.solovyev.android.calculator.onscreen;

import android.app.Activity;
import android.os.Bundle;

public class CalculatorOnscreenStartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        CalculatorOnscreenService.showOnscreenView(this);

		this.finish();
	}
}
