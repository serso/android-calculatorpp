package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import org.solovyev.android.calculator.onscreen.CalculatorOnscreenService;

public class CalculatorOnScreenStartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		startService(new Intent(this, CalculatorOnscreenService.class));

		this.finish();
	}
}
