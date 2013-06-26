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
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import net.robotmedia.billing.BillingController;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.solovyev.android.ads.AdsController;
import org.solovyev.android.calculator.CalculatorApplication;
import org.solovyev.android.calculator.CalculatorFragment;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.R;
import org.solovyev.android.fragments.FragmentUtils;

/**
 * User: serso
 * Date: 1/20/13
 * Time: 2:36 PM
 */
public class CalculatorPurchaseDialogActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.cpp_dialog);

		FragmentUtils.createFragment(this, PurchaseDialogFragment.class, R.id.dialog_layout, "purchase-dialog");
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
						// check billing availability
						if (BillingController.checkBillingSupported(activity) != BillingController.BillingStatus.SUPPORTED) {
							Log.d(CalculatorPreferencesActivity.class.getName(), "Billing is not supported - warn user!");
							// warn about not supported billing
							new AlertDialog.Builder(activity).setTitle(R.string.c_error).setMessage(R.string.c_billing_error).create().show();
						} else {
							Log.d(CalculatorPreferencesActivity.class.getName(), "Billing is supported - continue!");
							if (!AdsController.getInstance().isAdFree(activity)) {
								Log.d(CalculatorPreferencesActivity.class.getName(), "Item not purchased - try to purchase!");

								// not purchased => purchasing
								Toast.makeText(activity, R.string.c_calc_purchasing, Toast.LENGTH_SHORT).show();

								// show purchase window for user
								BillingController.requestPurchase(activity, CalculatorApplication.AD_FREE_PRODUCT_ID, true);
							} else {
								// and show message to user
								Toast.makeText(activity, R.string.c_calc_already_purchased, Toast.LENGTH_SHORT).show();
							}
						}

						activity.finish();
					}
				}
			});
		}
	}
}

