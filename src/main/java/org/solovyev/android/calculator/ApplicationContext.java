package org.solovyev.android.calculator;

import net.robotmedia.billing.BillingController;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 12/1/11
 * Time: 1:21 PM
 */
public class ApplicationContext extends android.app.Application {

	public static final String AD_FREE_APPLICATION = "ad_free_application";
	public static final String AD_FREE_APPLICATION_P_KEY = "org.solovyev.android.calculator_ad_free_application";

	@NotNull
	private static ApplicationContext instance;

	public ApplicationContext() {
		instance = this;
	}

	@NotNull
	public static ApplicationContext getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		/*BillingController.setDebug(true);
		BillingController.setConfiguration(new BillingController.IConfiguration() {

			@Override
			public byte[] getObfuscationSalt() {
				return new byte[]{81, -114, 32, -127, -32, -104, -40, -15, -47, 57, -13, -41, -33, 67, -114, 7, -11, 53, 126, 82};
			}

			@Override
			public String getPublicKey() {
				return "org.solovyev.android.calculator";
			}
		});*/
	}
}
