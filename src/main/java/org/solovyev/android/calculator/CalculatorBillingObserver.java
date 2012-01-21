/*
 * Copyright (c) 2009-2012. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import android.app.Activity;
import net.robotmedia.billing.helper.AbstractBillingObserver;
import net.robotmedia.billing.model.Transaction;
import net.robotmedia.billing.requests.ResponseCode;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 1/5/12
 * Time: 4:51 PM
 */
public class CalculatorBillingObserver extends AbstractBillingObserver {

	public CalculatorBillingObserver(@NotNull Activity activity) {
		super(activity);
	}

	@Override
	public void onBillingChecked(boolean supported) {
		// do nothing
	}

	@Override
	public void onPurchaseIntentFailure(@NotNull String s, @NotNull ResponseCode responseCode) {
		// do nothing
	}

	@Override
	public void onPurchaseStateChanged(@NotNull String itemId, @NotNull Transaction.PurchaseState state) {
		// do nothing
	}

	@Override
	public void onRequestPurchaseResponse(@NotNull String itemId, @NotNull ResponseCode response) {
		// do nothing
	}
}
