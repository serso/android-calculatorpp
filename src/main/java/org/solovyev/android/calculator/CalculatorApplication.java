package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.google.ads.AdView;
import net.robotmedia.billing.BillingController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.AndroidUtils;

import java.util.Collections;
import java.util.List;

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

	private static boolean isAdFreePurchased(@NotNull Context context) {
		return BillingController.isPurchased(context.getApplicationContext(), AD_FREE_PRODUCT_ID);
	}

	private static boolean transactionsRestored = false;

	public static boolean isAdFree(@NotNull Context context) {
		// check if user already bought this product
		boolean purchased = isAdFreePurchased(context);
		if (!purchased && !transactionsRestored) {
			// we must to restore all transactions done by user to guarantee that product was purchased or not
			BillingController.restoreTransactions(context);

			transactionsRestored = true;

			// todo serso: may be call net.robotmedia.billing.BillingController.restoreTransactions() always before first check and get rid of second check
			// check the billing one more time
			purchased = isAdFreePurchased(context);
		}
		return purchased;
	}

	@Nullable
	public static AdView inflateAd(@NotNull Activity activity) {
		return inflateAd(activity, R.id.ad_parent_view);
	}

	@Nullable
	public static AdView inflateAd(@NotNull Activity activity, int parentViewId) {
		AdView result = null;
		if ( !isAdFree(activity) ) {
			Log.d(activity.getClass().getName(), "Application is not ad free - inflating ad!");
			final List<String> keywords = Collections.emptyList();
			result = AndroidUtils.createAndInflateAdView(activity, ADMOB_USER_ID, parentViewId, keywords);
		} else {
			Log.d(activity.getClass().getName(), "Application is ad free - no ads!");
		}

		return result;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		//BillingController.setDebug(true);
		BillingController.setConfiguration(new BillingController.IConfiguration() {

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
