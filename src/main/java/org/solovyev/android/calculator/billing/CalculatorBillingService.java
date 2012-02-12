package org.solovyev.android.calculator.billing;

import net.robotmedia.billing.BillingService;
import org.solovyev.android.calculator.CalculatorApplication;

/**
 * User: serso
 * Date: 2/12/12
 * Time: 1:45 PM
 */
public class CalculatorBillingService extends BillingService {
	@Override
	public void onCreate() {
		super.onCreate();
		CalculatorApplication.registerOnRemoteStackTrace();
	}
}
