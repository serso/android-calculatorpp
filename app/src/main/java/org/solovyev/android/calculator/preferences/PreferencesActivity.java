package org.solovyev.android.calculator.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.XmlRes;
import android.util.SparseArray;

import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.AppComponent;
import org.solovyev.android.calculator.BaseActivity;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.language.Languages;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.Checkout;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class PreferencesActivity extends BaseActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    static final String EXTRA_PREFERENCE = "preference";
    static final String EXTRA_PREFERENCE_TITLE = "preference-title";

    @Nonnull
    private static final SparseArray<PrefDef> preferenceDefs = new SparseArray<>();

    public static Class<? extends PreferencesActivity> getClass(@NonNull Context context) {
        return App.isTablet(context) ? Dialog.class : PreferencesActivity.class;
    }

    static {
        preferenceDefs.append(R.xml.preferences, new PrefDef("screen-main", R.string.cpp_settings));
        preferenceDefs.append(R.xml.preferences_number_format, new PrefDef("screen-number-format", R.string.cpp_number_format));
        preferenceDefs.append(R.xml.preferences_appearance, new PrefDef("screen-appearance", R.string.cpp_appearance));
        preferenceDefs.append(R.xml.preferences_other, new PrefDef("screen-other", R.string.cpp_other));
        preferenceDefs.append(R.xml.preferences_onscreen, new PrefDef("screen-onscreen", R.string.cpp_floating_calculator));
        preferenceDefs.append(R.xml.preferences_widget, new PrefDef("screen-widget", R.string.cpp_widget));
    }

    ActivityCheckout checkout;

    @Inject
    Billing billing;
    @Inject
    Languages languages;

    public PreferencesActivity() {
        super(R.layout.activity_empty, R.string.cpp_settings);
    }

    @Nonnull
    static SparseArray<PrefDef> getPreferenceDefs() {
        return preferenceDefs;
    }

    @Nonnull
    public static Intent makeIntent(@Nonnull Context context, @XmlRes int preference, @StringRes int title) {
        final Intent intent = new Intent(context, getClass(context));
        intent.putExtra(EXTRA_PREFERENCE, preference);
        if (title != 0) {
            intent.putExtra(EXTRA_PREFERENCE_TITLE, title);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        final int preferenceTitle = intent.getIntExtra(EXTRA_PREFERENCE_TITLE, 0);
        if (preferenceTitle != 0) {
            setTitle(preferenceTitle);
        }

        if (savedInstanceState == null) {
            final int preference = intent.getIntExtra(EXTRA_PREFERENCE, R.xml.preferences);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main, PreferencesFragment.create(preference))
                    .commit();
        }

        checkout = Checkout.forActivity(this, billing);
        checkout.start();
    }

    @Override
    protected void inject(@Nonnull AppComponent component) {
        super.inject(component);
        component.inject(this);
    }

    @Override
    protected void onDestroy() {
        checkout.stop();
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

    public static final class Dialog extends PreferencesActivity {
    }
}
