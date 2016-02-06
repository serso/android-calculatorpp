package org.solovyev.android.calculator.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.annotation.XmlRes;
import android.util.SparseArray;
import org.solovyev.android.calculator.*;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.Products;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static android.support.v7.app.ActionBar.NAVIGATION_MODE_STANDARD;

public class PreferencesActivity extends BaseActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    static final String EXTRA_PREFERENCE = "preference";
    static final String EXTRA_PREFERENCE_TITLE = "preference-title";

    @Nonnull
    private static final SparseArray<PrefDef> preferences = new SparseArray<>();

    static {
        preferences.append(R.xml.preferences, new PrefDef("screen-main", R.string.c_app_settings));
        preferences.append(R.xml.preferences_calculations, new PrefDef("screen-calculations", R.string.c_prefs_calculations_category));
        preferences.append(R.xml.preferences_appearance, new PrefDef("screen-appearance", R.string.c_prefs_appearance_category));
        preferences.append(R.xml.preferences_plot, new PrefDef("screen-plot", R.string.prefs_graph_screen_title));
        preferences.append(R.xml.preferences_other, new PrefDef("screen-other", R.string.c_prefs_other_category));
        preferences.append(R.xml.preferences_onscreen, new PrefDef("screen-onscreen", R.string.prefs_onscreen_title));
        preferences.append(R.xml.preferences_widget, new PrefDef("screen-widget", R.string.prefs_widget_title));
    }

    ActivityCheckout checkout;
    private boolean paused = true;

    @Inject
    Billing billing;
    @Inject
    Products products;

    public PreferencesActivity() {
        super(R.layout.main_empty);
    }

    @Nonnull
    static SparseArray<PrefDef> getPreferences() {
        return preferences;
    }

    public static void showPlotPreferences(@Nonnull Context context) {
        start(context, R.xml.preferences_plot, R.string.prefs_graph_screen_title);
    }

    private static void start(@Nonnull Context context, @XmlRes int preference, @StringRes int title) {
        final Intent intent = makeIntent(context, preference, title);
        context.startActivity(intent);
    }

    @Nonnull
    public static Intent makeIntent(@Nonnull Context context, @XmlRes int preference, @StringRes int title) {
        final Intent intent = new Intent(context, PreferencesActivity.class);
        intent.putExtra(EXTRA_PREFERENCE, preference);
        if (title != 0) {
            intent.putExtra(EXTRA_PREFERENCE_TITLE, title);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getPreferences().registerOnSharedPreferenceChangeListener(this);

        final Intent intent = getIntent();
        final int preferenceTitle = intent.getIntExtra(EXTRA_PREFERENCE_TITLE, 0);
        if (preferenceTitle != 0) {
            setTitle(preferenceTitle);
        }

        if (savedInstanceState == null) {
            final int preference = intent.getIntExtra(EXTRA_PREFERENCE, R.xml.preferences);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main, PreferencesFragment.create(preference, R.layout.fragment_preferences))
                    .commit();
        }

        getSupportActionBar().setNavigationMode(NAVIGATION_MODE_STANDARD);

        checkout = Checkout.forActivity(this, billing, products);
        checkout.start();
    }

    @Override
    protected void inject(@Nonnull AppComponent component) {
        super.inject(component);
        component.inject(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (!paused) {
            if (Preferences.Gui.theme.isSameKey(key)) {
                ActivityUi.restartIfThemeChanged(this, ui.getTheme());
            } else if (Preferences.Gui.language.isSameKey(key)) {
                ActivityUi.restartIfLanguageChanged(this, ui.getLanguage());
            }
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
    ActivityCheckout getCheckout() {
        return checkout;
    }

    static class PrefDef {
        @Nonnull
        public final String id;
        @StringRes
        public final int title;

        PrefDef(@Nonnull String id, int title) {
            this.id = id;
            this.title = title;
        }
    }
}
