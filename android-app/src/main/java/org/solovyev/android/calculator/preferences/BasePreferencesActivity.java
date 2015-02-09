package org.solovyev.android.calculator.preferences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import org.solovyev.android.Activities;
import org.solovyev.android.Views;
import org.solovyev.android.calculator.*;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;

import javax.annotation.Nonnull;

public abstract class BasePreferencesActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

	private static boolean SUPPORT_HEADERS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	private final ActivityCheckout checkout = Checkout.forActivity(this, App.getBilling(), App.getProducts());
	private Inventory inventory;
	private AdView adView;
	private Toolbar actionBar;
	private Preferences.Gui.Theme theme;
	private boolean paused = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final SharedPreferences preferences = App.getPreferences();
		preferences.registerOnSharedPreferenceChangeListener(this);
		theme = Preferences.Gui.getTheme(preferences);
		setTheme(theme.getThemeId(this));

		super.onCreate(savedInstanceState);

		actionBar.setTitle(getTitle());

		checkout.start();
		inventory = checkout.loadInventory();
	}

	@Override
	public void setContentView(int layout) {
		final LayoutInflater inflater = LayoutInflater.from(this);
		final ViewGroup contentView = (ViewGroup) inflater.inflate(R.layout.cpp_activity_settings, new LinearLayout(this), true);

		actionBar = (Toolbar) contentView.findViewById(R.id.action_bar);
		actionBar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		final ViewGroup contentWrapper = (ViewGroup) contentView.findViewById(R.id.content_wrapper);
		inflater.inflate(layout, contentWrapper, true);

		// let's fix padding for parent view of list view
		Views.processViewsOfType(contentWrapper, ViewGroup.class, new Views.ViewProcessor<ViewGroup>() {
			@Override
			public void process(@Nonnull ViewGroup view) {
				for (int i = 0; i < view.getChildCount(); i++) {
					final View child = view.getChildAt(i);
					if (child.getId() == android.R.id.list) {
						view.setPadding(0, 0, 0, 0);
					}
				}
			}
		});

		getWindow().setContentView(contentView);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
		if (!paused && Preferences.Gui.theme.isSameKey(key)) {
			ActivityUi.restartIfThemeChanged(this, theme);
		}
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
		if (!supportsHeaders()) {
			return;
		}

		final ListView listView = getListView();
		if (show) {
			if (adView != null) {
				return;
			}
			adView = (AdView) LayoutInflater.from(this).inflate(R.layout.ad, null);
			adView.show();
			try {
				listView.addHeaderView(adView);
			} catch (IllegalStateException e) {
				// doesn't support header views
				SUPPORT_HEADERS = false;
				adView.hide();
				adView = null;
			}
		} else {
			if (adView == null) {
				return;
			}
			listView.removeHeaderView(adView);
			adView.hide();
			adView = null;
		}
	}

	private boolean supportsHeaders() {
		return SUPPORT_HEADERS;
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
		paused = false;
		if (adView != null) {
			adView.resume();
		}
		inventory.whenLoaded(new InventoryListener());

		final Preferences.Gui.Theme newTheme = Preferences.Gui.theme.getPreference(App.getPreferences());
		if (!theme.equals(newTheme)) {
			Activities.restartActivity(this);
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

	@Override
	protected void onPause() {
		if (adView != null) {
			adView.pause();
		}
		paused = true;
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}
		checkout.stop();
		App.getPreferences().unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
	}
}
