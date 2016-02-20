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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import org.solovyev.android.calculator.ActivityUi;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.BaseDialogFragment;
import org.solovyev.android.calculator.R;
import org.solovyev.android.checkout.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static org.solovyev.android.calculator.App.cast;

public class PurchaseDialogActivity extends AppCompatActivity implements RequestListener<Purchase> {

    @Inject
    Billing billing;
    @Inject
    Products products;
    ActivityCheckout checkout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cast(getApplication()).getComponent().inject(this);

        if (savedInstanceState == null) {
            App.showDialog(new PurchaseDialogFragment(), PurchaseDialogFragment.FRAGMENT_TAG, getSupportFragmentManager());
        }

        checkout = Checkout.forActivity(this, billing, products);
        checkout.start();
        checkout.createPurchaseFlow(this);
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

    public void onDialogClosed() {
        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(PurchaseDialogFragment.FRAGMENT_TAG);
        if (fragment == null) {
            // activity is closing
            return;
        }
        finish();
    }

    @Override
    public void onSuccess(@Nonnull Purchase result) {
        finish();
    }

    @Override
    public void onError(int response, @Nonnull Exception e) {
        finish();
    }

    public static class PurchaseDialogFragment extends BaseDialogFragment {

        public static final String FRAGMENT_TAG = "purchase-dialog";
        @Nullable
        private PurchaseDialogActivity activity;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            this.activity = (PurchaseDialogActivity) activity;
        }

        @Override
        protected void onPrepareDialog(@NonNull AlertDialog.Builder builder) {
            super.onPrepareDialog(builder);
            builder.setTitle(R.string.cpp_purchase_title);
            builder.setMessage(R.string.cpp_purchase_text);
            builder.setPositiveButton(R.string.cpp_continue, null);
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (activity != null) {
                        activity.purchase();
                    }
                    break;
                default:
                    super.onClick(dialog, which);
                    break;
            }
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            if (activity != null) {
                activity.onDialogClosed();
                activity = null;
            }
        }
    }
}

