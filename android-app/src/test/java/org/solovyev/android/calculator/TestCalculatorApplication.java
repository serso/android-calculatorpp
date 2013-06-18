package org.solovyev.android.calculator;

/**
 * User: serso
 * Date: 6/18/13
 * Time: 8:45 PM
 */
public class TestCalculatorApplication extends CalculatorApplication {

	private static volatile boolean initialized = false;

	@Override
	public void onCreate() {
		synchronized (TestCalculatorApplication.class) {
			if (!initialized) {
				super.onCreate();
				initialized = true;
			}
		}
	}
}
