package org.solovyev.android.calculator.preferences;

import static org.solovyev.android.calculator.App.cast;
import static org.solovyev.android.calculator.Engine.Preferences.angleUnitName;
import static org.solovyev.android.calculator.Engine.Preferences.numeralBaseName;
import static org.solovyev.android.calculator.wizard.CalculatorWizards.DEFAULT_WIZARD_FLOW;
import static org.solovyev.android.wizard.WizardUi.startWizard;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceDialogFragmentCompat;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import jscl.AngleUnit;
import jscl.JsclMathEngine;
import jscl.NumeralBase;
import org.solovyev.android.calculator.ActivityLauncher;
import org.solovyev.android.calculator.AdView;
import org.solovyev.android.calculator.Engine;
import org.solovyev.android.calculator.Preferences;
import org.solovyev.android.calculator.Preferences.Gui.Theme;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.feedback.FeedbackReporter;
import org.solovyev.android.calculator.language.Language;
import org.solovyev.android.calculator.language.Languages;
import org.solovyev.android.checkout.BillingRequests;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.RequestListener;
import org.solovyev.android.prefs.StringPreference;
import org.solovyev.android.wizard.Wizards;
import org.solovyev.common.text.CharacterMapper;


public class PreferencesFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Nonnull
    private static String ARG_PREFERENCES = "preferences";

    @Nullable
    private AdView adView;
    @Inject
    SharedPreferences preferences;
    @Inject
    Languages languages;
    @Inject
    Wizards wizards;
    @Inject
    JsclMathEngine engine;
    @Inject
    FeedbackReporter feedbackReporter;
    @Inject
    ActivityLauncher launcher;
    @Inject
    Bus bus;

    @Nonnull
    public static PreferencesFragment create(int preferences) {
        final PreferencesFragment fragment = new PreferencesFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_PREFERENCES, preferences);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cast(this).getComponent().inject(this);

        preferences.registerOnSharedPreferenceChangeListener(this);
        bus.register(this);
    }

    @Override
    public void onCreatePreferences(@androidx.annotation.Nullable Bundle savedInstanceState, @androidx.annotation.Nullable String rootKey) {
        int preferencesResId = getArguments().getInt(ARG_PREFERENCES);
        addPreferencesFromResource(preferencesResId);
    }

    private void setPreferenceIntent(int xml, @Nonnull PreferencesActivity.PrefDef def) {
        final Preference preference = findPreference(def.id);
        if (preference != null) {
            final FragmentActivity context = getActivity();
            final Intent intent = new Intent(context, PreferencesActivity.getClass(context));
            intent.putExtra(PreferencesActivity.EXTRA_PREFERENCE, xml);
            intent.putExtra(PreferencesActivity.EXTRA_PREFERENCE_TITLE, def.title);
            preference.setIntent(intent);
        }
    }

    /** @noinspection deprecation*/
    @Override
    public void onDisplayPreferenceDialog(@NonNull Preference preference) {
        String fragmentTag = "fragment:" + preference.getKey();
        if (getParentFragmentManager().findFragmentByTag(fragmentTag) != null) return;

        if (preference instanceof PrecisionPreference) {
            final PreferenceDialogFragmentCompat f = new PrecisionPreference.Dialog();
            f.setTargetFragment(this, 0);
            f.show(getParentFragmentManager(), fragmentTag);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final int preference = getArguments().getInt(ARG_PREFERENCES);
        if (preference == R.xml.preferences) {
            prepareScreens();
            prepareIntroduction();
            prepareReportBug();
            prepareAbout();
            prepareSupportProject();
            prepareMode();
            prepareAngles();
            prepareRadix();
        } else if (preference == R.xml.preferences_number_format) {
            prepareListPreference(Engine.Preferences.Output.notation, Engine.Notation.class);
            preparePrecisionPreference();
            prepareSeparatorPreference();
            prepareNumberFormatExamplesPreference();
        } else if (preference == R.xml.preferences_onscreen) {
            updateFloatingCalculatorPreferences();
        }

        prepareLanguagePreference(preference);
        prepareThemePreference(preference);

        getCheckout().whenReady(new Checkout.EmptyListener() {
            @Override
            public void onReady(@Nonnull BillingRequests requests) {
                requests.isPurchased(ProductTypes.IN_APP, "ad_free", new RequestListener<Boolean>() {
                    @Override
                    public void onSuccess(@Nonnull Boolean purchased) {
                        final Preference supportProject = findPreference("prefs.supportProject");
                        if (supportProject != null) {
                            supportProject.setEnabled(!purchased);
                            supportProject.setSelectable(!purchased);
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
    }

    private void prepareReportBug() {
        final Preference reportBug = findPreference("prefs.reportBug");
        reportBug.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                feedbackReporter.report();
                return true;
            }
        });

    }

    private void prepareSupportProject() {
        final Preference supportProject = findPreference("prefs.supportProject");
        supportProject.setEnabled(false);
        supportProject.setSelectable(false);
        supportProject.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity(), PurchaseDialogActivity.class));
                return true;
            }
        });
    }

    private void prepareScreens() {
        final SparseArray<PreferencesActivity.PrefDef> preferences = PreferencesActivity.getPreferenceDefs();
        for (int i = 0; i < preferences.size(); i++) {
            setPreferenceIntent(preferences.keyAt(i), preferences.valueAt(i));
        }
    }

    private void prepareIntroduction() {
        final Preference introduction = findPreference("prefs.introduction");
        introduction.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startWizard(wizards, DEFAULT_WIZARD_FLOW, getActivity());
                return true;
            }
        });
    }

    private void prepareAbout() {
        final Preference about = findPreference("prefs.about");
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                launcher.showAbout();
                return true;
            }
        });
    }

    private void prepareNumberFormatExamplesPreference() {
        final NumberFormatExamplesPreference preference = (NumberFormatExamplesPreference) getPreferenceManager().findPreference("numberFormat.examples");
        if (preference == null) {
            return;
        }
        preference.update(engine);
    }

    private void prepareSeparatorPreference() {
        final ListPreference preference = (ListPreference) getPreferenceManager().findPreference(Engine.Preferences.Output.separator.getKey());
        preference.setSummary(separatorName(Engine.Preferences.Output.separator.getPreference(preferences)));
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference p, Object newValue) {
                preference.setSummary(separatorName(CharacterMapper.INSTANCE.parseValue(String.valueOf(newValue))));
                return true;
            }
        });
    }

    private int separatorName(char separator) {
        switch (separator) {
            case '\'':
                return R.string.cpp_thousands_separator_apostrophe;
            case ' ':
                return R.string.cpp_thousands_separator_space;
            case 0:
                return R.string.cpp_thousands_separator_no;
        }
        return R.string.cpp_thousands_separator_no;
    }

    private void preparePrecisionPreference() {
        final PrecisionPreference preference = (PrecisionPreference) getPreferenceManager().findPreference(Engine.Preferences.Output.precision.getKey());
        preference.setSummary(String.valueOf(Engine.Preferences.Output.precision.getPreference(preferences)));
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference p, Object newValue) {
                preference.setSummary(String.valueOf(newValue));
                return true;
            }
        });
    }

    private <E extends Enum<E> & PreferenceEntry> void prepareListPreference(@Nonnull final StringPreference<E> p, @Nonnull Class<E> type) {
        final ListPreference preference = (ListPreference) getPreferenceManager().findPreference(p.getKey());
        if (preference == null) {
            return;
        }
        final E[] entries = type.getEnumConstants();
        final FragmentActivity activity = getActivity();
        populate(preference, entries);
        preference.setSummary(p.getPreference(preferences).getName(activity));
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference p, Object newValue) {
                for (E entry : entries) {
                    if (entry.getId().equals(newValue)) {
                        preference.setSummary(entry.getName(activity));
                        break;
                    }
                }
                return true;
            }
        });
    }

    private void prepareMode() {
        final ListPreference mode = (ListPreference) getPreferenceManager().findPreference(Preferences.Gui.mode.getKey());
        mode.setSummary(Preferences.Gui.getMode(preferences).name);
        mode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mode.setSummary(Preferences.Gui.Mode.valueOf((String) newValue).name);
                return true;
            }
        });
    }

    private void prepareAngles() {
        final ListPreference angles = (ListPreference) getPreferenceManager().findPreference(Engine.Preferences.angleUnit.getKey());
        angles.setSummary(angleUnitName(Engine.Preferences.angleUnit.getPreference(preferences)));
        angles.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                angles.setSummary(angleUnitName(AngleUnit.valueOf((String) newValue)));
                return true;
            }
        });
    }

    private void prepareRadix() {
        final ListPreference radix = (ListPreference) getPreferenceManager().findPreference(Engine.Preferences.numeralBase.getKey());
        radix.setSummary(numeralBaseName(Engine.Preferences.numeralBase.getPreference(preferences)));
        radix.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                radix.setSummary(numeralBaseName(NumeralBase.valueOf((String) newValue)));
                return true;
            }
        });
    }

    private void prepareThemePreference(int preference) {
        if (preference != R.xml.preferences_appearance) {
            return;
        }
        final ListPreference theme = (ListPreference) getPreferenceManager().findPreference(Preferences.Gui.theme.getKey());
        final FragmentActivity context = getActivity();
        populate(theme,
                Theme.material_theme,
                Theme.material_black_theme,
                Theme.material_light_theme,
                Theme.metro_blue_theme,
                Theme.metro_green_theme,
                Theme.metro_purple_theme);
        theme.setSummary(Preferences.Gui.getTheme(preferences).getName(context));
        theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final Theme newTheme = Theme.valueOf((String) newValue);
                theme.setSummary(newTheme.getName(context));
                return true;
            }
        });
    }

    private static void populate(@Nonnull ListPreference preference, @Nonnull PreferenceEntry... entries) {
        populate(preference, Arrays.asList(entries));
    }

    private static void populate(@Nonnull ListPreference preference, @Nonnull List<? extends PreferenceEntry> entries) {
        final int size = entries.size();
        final CharSequence[] e = new CharSequence[size];
        final CharSequence[] v = new CharSequence[size];
        final Context context = preference.getContext();
        for (int i = 0; i < size; i++) {
            final PreferenceEntry entry = entries.get(i);
            e[i] = entry.getName(context);
            v[i] = entry.getId();
        }
        preference.setEntries(e);
        preference.setEntryValues(v);
    }

    private void prepareLanguagePreference(int preference) {
        if (preference != R.xml.preferences_appearance) {
            return;
        }

        final ListPreference language = (ListPreference) getPreferenceManager().findPreference(Preferences.Gui.language.getKey());
        populate(language, languages.getList());
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
        if (Preferences.Onscreen.showAppIcon.isSameKey(key)) {
            updateFloatingCalculatorPreferences();
        }
    }

    private void updateFloatingCalculatorPreferences() {
        final Preference theme = findPreference(Preferences.Onscreen.theme.getKey());
        if (theme != null) {
            theme.setEnabled(Preferences.Onscreen.showAppIcon.getPreference(preferences));
        }
    }

    @Subscribe
    public void onEngineChanged(Engine.ChangedEvent e) {
        prepareNumberFormatExamplesPreference();
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
        bus.unregister(this);
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }


    protected void onShowAd(boolean show) {
        if (getView() == null) {
            return;
        }

        final View root = getView();
        if (!(root instanceof ViewGroup)) return;

        final ViewGroup container = (ViewGroup) root;
        if (show) {
            if (adView != null) return;
            adView = new AdView(getActivity());
            adView.show();
            container.addView(adView);
        } else {
            if (adView == null) return;
            container.removeView(adView);
            adView.hide();
            adView = null;
        }
    }

}
