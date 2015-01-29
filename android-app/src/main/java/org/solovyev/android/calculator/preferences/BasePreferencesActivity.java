package org.solovyev.android.calculator.preferences;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import org.solovyev.android.calculator.ActivityUi;
import org.solovyev.android.calculator.AdView;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.R;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;

import javax.annotation.Nonnull;

public abstract class BasePreferencesActivity extends PreferenceActivity {

	private final ActivityCheckout checkout = Checkout.forActivity(this, App.getBilling(), App.getProducts());
	private Inventory inventory;
	private AdView adView;
	private Toolbar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		actionBar.setTitle(getTitle());

		checkout.start();
		inventory = checkout.loadInventory();
	}

	@Override
	public void setContentView(int layout) {
		final ViewGroup contentView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.cpp_activity_settings, new LinearLayout(this), true);

		actionBar = (Toolbar) contentView.findViewById(R.id.action_bar);
		actionBar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		final ViewGroup contentWrapper = (ViewGroup) contentView.findViewById(R.id.content_wrapper);
		LayoutInflater.from(this).inflate(layout, contentWrapper, true);

		getWindow().setContentView(contentView);
	}


	private class InventoryListener implements Inventory.Listener {
		@Override
		public void onLoaded(@Nonnull Inventory.Products products) {
			final Inventory.Product product = products.get(ProductTypes.IN_APP);
			final boolean adFree = product.isPurchased("ad_free");
			onShowAd(!adFree);
		}
	}

	protected void onShowAd(boolean show) {
		if(!supportsAds()) {
			return;
		}
		if (show) {
			if (adView != null) {
				return;
			}
			adView = (AdView) LayoutInflater.from(this).inflate(R.layout.ad, null);
			adView.show();
			getListView().addHeaderView(adView);
		} else {
			if (adView == null) {
				return;
			}
			getListView().removeHeaderView(adView);
			adView.hide();
			adView = null;
		}
	}

	private boolean supportsAds() {
		// on Android 2.3 the headers in the list view are not supported
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (checkout.onActivityResult(requestCode, resultCode, data)) {
			return;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (adView != null) {
			adView.resume();
		}
		inventory.whenLoaded(new InventoryListener());
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

	@Override
	protected void onPause() {
		if (adView != null) {
			adView.pause();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}
		checkout.stop();
		super.onDestroy();
	}
}
