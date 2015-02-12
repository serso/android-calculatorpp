package org.solovyev.android.calculator.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.annotation.XmlRes;
import android.text.TextUtils;
import android.util.SparseArray;

import org.solovyev.android.calculator.ActivityUi;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.BaseActivity;
import org.solovyev.android.calculator.Preferences;
import org.solovyev.android.calculator.R;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Checkout;

import javax.annotation.Nonnull;

import static android.support.v7.app.ActionBar.NAVIGATION_MODE_STANDARD;

public class PreferencesActivity extends BaseActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

	static final String EXTRA_PREFERENCE = "preference";
	static final String EXTRA_PREFERENCE_TITLE = "preference-title";

	@Nonnull
	private static final SparseArray<String> preferences = new SparseArray<>();

	static {
		preferences.append(R.xml.preferences, "screen-main");
		preferences.append(R.xml.preferences_calculations, "screen-calculations");
		preferences.append(R.xml.preferences_appearance, "screen-appearance");
		preferences.append(R.xml.preferences_plot, "screen-plot");
		preferences.append(R.xml.preferences_other, "screen-other");
		preferences.append(R.xml.preferences_onscreen, "screen-onscreen");
	}

	@Nonnull
	private final ActivityCheckout checkout = Checkout.forActivity(this, App.getBilling(), App.getProducts());

	private boolean paused = true;

	public PreferencesActivity() {
		super(R.layout.main_empty);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.getPreferences().registerOnSharedPreferenceChangeListener(this);

		final Intent intent = getIntent();
		final String preferenceTitle = intent.getStringExtra(EXTRA_PREFERENCE_TITLE);
		if (!TextUtils.isEmpty(preferenceTitle)) {
			setTitle(preferenceTitle);
		}

		if (savedInstanceState == null) {
			final int preference = intent.getIntExtra(EXTRA_PREFERENCE, R.xml.preferences);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.main_layout, PreferencesFragment.create(preference, R.layout.fragment_preferences))
					.commit();
		}

		getSupportActionBar().setNavigationMode(NAVIGATION_MODE_STANDARD);

		checkout.start();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (!paused && Preferences.Gui.theme.isSameKey(key)) {
			ActivityUi.restartIfThemeChanged(this, ui.getTheme());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		paused = false;
	}

	@Override
	protected void onPause() {
		paused = true;
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		checkout.stop();
		App.getPreferences().unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
	}

	@Nonnull
	static SparseArray<String> getPreferences() {
		return preferences;
	}

	public static void start(@Nonnull Context context, @XmlRes int preference, @StringRes int title) {
		final Intent intent = makeIntent(context, preference, title);
		context.startActivity(intent);
	}

	@Nonnull
	static Intent makeIntent(@Nonnull Context context, int preference, int title) {
		final Intent intent = new Intent(context, PreferencesActivity.class);
		intent.putExtra(EXTRA_PREFERENCE, preference);
		if (title != 0) {
			intent.putExtra(EXTRA_PREFERENCE_TITLE, context.getString(title));
		}
		return intent;
	}

	@Nonnull
	ActivityCheckout getCheckout() {
		return checkout;
	}
}
