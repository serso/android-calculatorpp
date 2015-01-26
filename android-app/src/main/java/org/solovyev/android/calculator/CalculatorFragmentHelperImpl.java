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
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.solovyev.android.checkout.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Locale;

import static java.util.Arrays.asList;

/**
 * User: serso
 * Date: 9/26/12
 * Time: 10:14 PM
 */
public class CalculatorFragmentHelperImpl extends AbstractCalculatorHelper implements CalculatorFragmentHelper {

	private ActivityCheckout checkout;

	@Nullable
	private AdView adView;

	private int layoutId;

	private int titleResId = -1;

	private boolean listenersOnCreate = true;

	@Nullable
	private Boolean adFree = null;

	public CalculatorFragmentHelperImpl(int layoutId) {
		this.layoutId = layoutId;
	}

	public CalculatorFragmentHelperImpl(int layoutId, int titleResId) {
		this.layoutId = layoutId;
		this.titleResId = titleResId;
	}

	public CalculatorFragmentHelperImpl(int layoutId, int titleResId, boolean listenersOnCreate) {
		this.layoutId = layoutId;
		this.titleResId = titleResId;
		this.listenersOnCreate = listenersOnCreate;
	}

	@Override
	public boolean isPane(@Nonnull Fragment fragment) {
		return fragment.getActivity() instanceof CalculatorActivity;
	}

	public void setPaneTitle(@Nonnull Fragment fragment, int titleResId) {
		final TextView fragmentTitle = (TextView) fragment.getView().findViewById(R.id.fragment_title);
		if (fragmentTitle != null) {
			if (!isPane(fragment)) {
				fragmentTitle.setVisibility(View.GONE);
			} else {
				fragmentTitle.setText(fragment.getString(titleResId).toUpperCase(Locale.getDefault()));
			}
		}
	}

	@Override
	public void onCreate(@Nonnull Fragment fragment) {
		final FragmentActivity activity = fragment.getActivity();
		super.onCreate(activity);
		checkout = Checkout.forActivity(activity, CalculatorApplication.getInstance().getBilling(), Products.create().add(ProductTypes.IN_APP, asList("ad_free")));

		if (listenersOnCreate) {
			if (fragment instanceof CalculatorEventListener) {
				Locator.getInstance().getCalculator().addCalculatorEventListener((CalculatorEventListener) fragment);
			}
		}

		checkout.start();
	}

	@Override
	public void onResume(@Nonnull Fragment fragment) {
		if (adView != null) {
			adView.resume();
		}
		if (!listenersOnCreate) {
			if (fragment instanceof CalculatorEventListener) {
				Locator.getInstance().getCalculator().addCalculatorEventListener((CalculatorEventListener) fragment);
			}
		}

		checkout.loadInventory().whenLoaded(new Inventory.Listener() {
			@Override
			public void onLoaded(@Nonnull Inventory.Products products) {
				adFree = products.get(ProductTypes.IN_APP).isPurchased("ad_free");
				updateAdViewState();
			}
		});
	}

	private void updateAdViewState() {
		if(adFree == null || adView == null) {
			return;
		}

		if (adFree) {
			adView.hide();
		} else {
			adView.show();
		}
	}

	@Override
	public void onPause(@Nonnull Fragment fragment) {
		adFree = null;
		if (adView != null) {
			adView.pause();
		}
		if (!listenersOnCreate) {
			if (fragment instanceof CalculatorEventListener) {
				Locator.getInstance().getCalculator().removeCalculatorEventListener((CalculatorEventListener) fragment);
			}
		}
	}

	@Override
	public void onViewCreated(@Nonnull Fragment fragment, @Nonnull View root) {
		adView = (AdView) root.findViewById(R.id.ad);
		final ViewGroup mainFragmentLayout = (ViewGroup) root.findViewById(R.id.main_fragment_layout);

		if (fragment instanceof CalculatorDisplayFragment || fragment instanceof CalculatorEditorFragment || fragment instanceof CalculatorKeyboardFragment) {
			// no ads in those fragments
		} else {
			if (adView != null) {
				updateAdViewState();
			} else if (mainFragmentLayout != null) {
			}
		}

		processButtons(fragment.getActivity(), root);

		if (titleResId >= 0) {
			this.setPaneTitle(fragment, titleResId);
		}
	}

	@Override
	public void onDestroy(@Nonnull Fragment fragment) {
		if (adView != null) {
			adView.destroy();
			adView = null;
		}

		if (listenersOnCreate) {
			if (fragment instanceof CalculatorEventListener) {
				Locator.getInstance().getCalculator().removeCalculatorEventListener((CalculatorEventListener) fragment);
			}
		}

		checkout.stop();

		super.onDestroy(fragment.getActivity());
	}

	@Nonnull
	@Override
	public View onCreateView(@Nonnull Fragment fragment, @Nonnull LayoutInflater inflater, @Nullable ViewGroup container) {
		return inflater.inflate(layoutId, container, false);
	}
}
