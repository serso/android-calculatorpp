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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import org.solovyev.android.calculator.*;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.BillingRequests;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.RequestListener;
import org.solovyev.android.fragments.FragmentUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.solovyev.android.calculator.FragmentTab.purchase_dialog;

public class PurchaseDialogActivity extends BaseActivity {

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

    public PurchaseDialogActivity() {
        super(R.layout.cpp_dialog);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentUtils.createFragment(this, PurchaseDialogFragment.class, R.id.dialog_layout, "purchase-dialog");

        checkout.start();
        checkout.createPurchaseFlow(purchaseListener);
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

    public static class PurchaseDialogFragment extends BaseFragment {

        @Bind(R.id.cpp_purchase_text)
        TextView purchaseText;
        @Bind(R.id.cpp_continue_button)
        View continueButton;

        @Nonnull
        @Override
        protected FragmentUi createUi() {
            return createUi(purchase_dialog);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View view = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, view);
            purchaseText.setMovementMethod(ScrollingMovementMethod.getInstance());
            continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Activity activity = getActivity();
                    if (activity != null) {
                        ((PurchaseDialogActivity) activity).purchase();
                    }
                }
            });
            return view;
        }
    }
}

