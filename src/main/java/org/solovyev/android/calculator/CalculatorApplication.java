package org.solovyev.android.calculator;

import net.robotmedia.billing.BillingController;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.ads.AdsController;

/**
 * User: serso
 * Date: 12/1/11
 * Time: 1:21 PM
 */
public class CalculatorApplication extends android.app.Application {

	public static final String AD_FREE_PRODUCT_ID = "ad_free";
	public static final String AD_FREE_P_KEY = "org.solovyev.android.calculator_ad_free";

	public static final String ADMOB_USER_ID = "a14f02cf9c80cbc";

	@NotNull
	private static CalculatorApplication instance;

	public CalculatorApplication() {
		instance = this;
	}

	@NotNull
	public static CalculatorApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		AdsController.getInstance().init(ADMOB_USER_ID, AD_FREE_PRODUCT_ID, new BillingController.IConfiguration() {

			@Override
			public byte[] getObfuscationSalt() {
				return new byte[]{81, -114, 32, -127, -32, -104, -40, -15, -47, 57, -13, -41, -33, 67, -114, 7, -11, 53, 126, 82};
			}

			@Override
			public String getPublicKey() {
				return CalculatorSecurity.getPK();
			}
		});
	}
}
