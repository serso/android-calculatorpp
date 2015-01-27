/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator.preferences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.support.v7.app.ActionBarActivity;
import org.solovyev.android.calculator.*;
import org.solovyev.android.checkout.*;
import org.solovyev.android.fragments.FragmentUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 1/20/13
 * Time: 2:36 PM
 */
public class PurchaseDialogActivity extends ActionBarActivity {

	@Nonnull
	private final ActivityCheckout checkout = Checkout.forActivity(this, App.getBilling(), App.getProducts());

	@Nonnull
	private final RequestListener<Purchase> purchaseListener = new RequestListener<Purchase>() {
		@Override
		public void onSuccess(@Nonnull Purchase purchase) {
			finish();
		}

		@Override
		public void onError(int i, @Nonnull Exception e) {
			finish();
		}
	};

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.cpp_dialog);

		FragmentUtils.createFragment(this, PurchaseDialogFragment.class, R.id.dialog_layout, "purchase-dialog");

		checkout.start();
		checkout.createPurchaseFlow(purchaseListener);
	}

	public static class PurchaseDialogFragment extends CalculatorFragment {

		public PurchaseDialogFragment() {
			super(CalculatorFragmentType.purchase_dialog);
		}

		@Override
		public void onViewCreated(@Nonnull View root, Bundle savedInstanceState) {
			super.onViewCreated(root, savedInstanceState);

			((TextView) root.findViewById(R.id.cpp_purchase_text)).setMovementMethod(ScrollingMovementMethod.getInstance());
			root.findViewById(R.id.cpp_continue_button).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					final Activity activity = getActivity();
					if (activity != null) {
						((PurchaseDialogActivity) activity).purchase();
					}
				}
			});
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		ActivityUi.reportActivityStart(this);
	}

	@Override
	protected void onStop() {
		ActivityUi.reportActivityStop(this);
		super.onStop();
	}

	private void purchase() {
		checkout.whenReady(new Checkout.ListenerAdapter() {
			@Override
			public void onReady(@Nonnull BillingRequests requests) {
				requests.purchase(ProductTypes.IN_APP, "ad_free", null, checkout.getPurchaseFlow());
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		checkout.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		checkout.destroyPurchaseFlow();
		checkout.stop();
		super.onDestroy();
	}
}

