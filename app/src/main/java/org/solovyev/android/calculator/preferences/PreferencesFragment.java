package org.solovyev.android.calculator.preferences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import org.solovyev.android.calculator.AdView;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.Engine;
import org.solovyev.android.calculator.Preferences;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.language.Language;
import org.solovyev.android.calculator.language.Languages;
import org.solovyev.android.checkout.BillingRequests;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.RequestListener;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.solovyev.android.calculator.wizard.CalculatorWizards.DEFAULT_WIZARD_FLOW;
import static org.solovyev.android.wizard.WizardUi.startWizard;

public class PreferencesFragment extends org.solovyev.android.material.preferences.PreferencesFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static boolean SUPPORT_HEADERS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    @Nullable
    private Preference buyPremiumPreference;
    @Nullable
    private AdView adView;

    @Nonnull
    public static PreferencesFragment create(int preferencesResId, int layoutResId) {
        final PreferencesFragment fragment = new PreferencesFragment();
        fragment.setArguments(createArguments(preferencesResId, layoutResId, NO_THEME));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.getPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    private void setPreferenceIntent(int xml, @Nonnull PreferencesActivity.PrefDef def) {
        final Preference preference = findPreference(def.id);
        if (preference != null) {
            final Intent intent = new Intent(getActivity(), PreferencesActivity.class);
            intent.putExtra(PreferencesActivity.EXTRA_PREFERENCE, xml);
            intent.putExtra(PreferencesActivity.EXTRA_PREFERENCE_TITLE, def.title);
            preference.setIntent(intent);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final int preference = getPreferencesResId();
        if (preference == R.xml.preferences) {
            final SparseArray<PreferencesActivity.PrefDef> preferences = PreferencesActivity.getPreferences();
            for (int i = 0; i < preferences.size(); i++) {
                final int xml = preferences.keyAt(i);
                final PreferencesActivity.PrefDef def = preferences.valueAt(i);
                setPreferenceIntent(xml, def);
            }
            final Preference restartWizardPreference = findPreference("restart_wizard");
            restartWizardPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startWizard(App.getWizards(), DEFAULT_WIZARD_FLOW, getActivity());
                    return true;
                }
            });

            buyPremiumPreference = findPreference("buy_premium");
            if (buyPremiumPreference != null) {
                buyPremiumPreference.setEnabled(false);
                buyPremiumPreference.setSelectable(false);
                buyPremiumPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        startActivity(new Intent(getActivity(), PurchaseDialogActivity.class));
                        return true;
                    }
                });
            }
        }

        prepareLanguagePreference(preference);

        getCheckout().whenReady(new Checkout.ListenerAdapter() {
            @Override
            public void onReady(@Nonnull BillingRequests requests) {
                requests.isPurchased(ProductTypes.IN_APP, "ad_free", new RequestListener<Boolean>() {
                    @Override
                    public void onSuccess(@Nonnull Boolean purchased) {
                        if (buyPremiumPreference != null) {
                            buyPremiumPreference.setEnabled(!purchased);
                            buyPremiumPreference.setSelectable(!purchased);
                        }
                        onShowAd(!purchased);
                    }

                    @Override
                    public void onError(int i, @Nonnull Exception e) {
                        onShowAd(false);
                    }
                });
            }
        });

        final SharedPreferences preferences = App.getPreferences();
        onSharedPreferenceChanged(preferences, Engine.Preferences.Output.round.getKey());
    }

    private void prepareLanguagePreference(int preference) {
        if (preference != R.xml.preferences_appearance) {
            return;
        }

        final ListPreference language = (ListPreference) preferenceManager.findPreference(Preferences.Gui.language.getKey());
        final Languages languages = App.getLanguages();
        final List<Language> languagesList = languages.getList();
        final CharSequence[] entries = new CharSequence[languagesList.size()];
        final CharSequence[] entryValues = new CharSequence[languagesList.size()];
        for (int i = 0; i < languagesList.size(); i++) {
            final Language l = languagesList.get(i);
            entries[i] = l.getName(getActivity());
            entryValues[i] = l.code;
        }
        language.setEntries(entries);
        language.setEntryValues(entryValues);
        language.setSummary(languages.getCurrent().getName(getActivity()));
        language.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final Language l = languages.get((String) newValue);
                language.setSummary(l.getName(getActivity()));
                return true;
            }
        });
    }

    @Nonnull
    private Checkout getCheckout() {
        return ((PreferencesActivity) getActivity()).getCheckout();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (Engine.Preferences.Output.round.getKey().equals(key)) {
            final Preference preference = findPreference(Engine.Preferences.Output.precision.getKey());
            if (preference != null) {
                preference.setEnabled(preferences.getBoolean(key, Engine.Preferences.Output.round.getDefaultValue()));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        App.getPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    private boolean supportsHeaders() {
        return SUPPORT_HEADERS;
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
            adView = (AdView) LayoutInflater.from(getActivity()).inflate(R.layout.ad, null);
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

}
