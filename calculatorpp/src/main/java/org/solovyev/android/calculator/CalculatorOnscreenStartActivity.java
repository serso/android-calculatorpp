package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import org.solovyev.android.calculator.external.AndroidExternalListenersContainer;
import org.solovyev.android.calculator.onscreen.CalculatorOnscreenService;

public class CalculatorOnscreenStartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = new Intent(AndroidExternalListenersContainer.INIT_ACTION);
		intent.setClass(this, CalculatorOnscreenService.class);
		startService(intent);

		this.finish();
	}
}
