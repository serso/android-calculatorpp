package org.solovyev.android.calculator.preferences;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		checkout.start();
		inventory = checkout.loadInventory();
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
