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

package org.solovyev.android.calculator;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import org.solovyev.android.checkout.CppCheckout;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static org.solovyev.android.calculator.App.cast;

public class FragmentUi {
    private final int layoutId;
    @Nullable
    private Boolean adFree = null;
    @Inject
    CppCheckout checkout;
    @Nullable
    @Bind(R.id.admob)
    AdView adView;

    public FragmentUi(int layoutId) {
        this.layoutId = layoutId;
    }

    public void onCreate(@Nonnull Fragment fragment) {
        cast(fragment).getComponent().inject(this);
        checkout.start();
    }

    public void onResume() {
        if (adView != null) {
            adView.resume();
        }
        checkout.loadInventory().whenLoaded(new Inventory.Listener() {
            @Override
            public void onLoaded(@Nonnull Inventory.Products products) {
                adFree = products.get(ProductTypes.IN_APP).isPurchased("ad_free");
                updateAdView();
            }
        });
    }

    private void updateAdView() {
        if (adFree == null || adView == null) {
            return;
        }

        if (adFree) {
            adView.hide();
        } else {
            adView.show();
        }
    }

    public void onPause() {
        adFree = null;
        if (adView != null) {
            adView.pause();
        }
    }

    public void onCreateView(@Nonnull Fragment fragment, @Nonnull View root) {
        ButterKnife.bind(this, root);
        if (fragment instanceof DisplayFragment || fragment instanceof EditorFragment || fragment instanceof KeyboardFragment) {
            // no ads in those fragments
        } else if (adView != null) {
            updateAdView();
        }
    }

    public void onDestroy() {
        checkout.stop();
    }

    @Nonnull
    public View onCreateView(@Nonnull LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(layoutId, container, false);
    }

    public void onDestroyView() {
        if (adView == null) {
            return;
        }
        adView.destroy();
        adView = null;
    }
}
