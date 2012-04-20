package org.solovyev.android.calculator;

import android.content.SharedPreferences;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.AndroidUtils;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.prefs.BooleanPreference;
import org.solovyev.android.prefs.IntegerPreference;
import org.solovyev.android.prefs.Preference;
import org.solovyev.android.prefs.StringPreference;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * User: serso
 * Date: 4/20/12
 * Time: 12:42 PM
 */
public final class CalculatorPreferences {

    private CalculatorPreferences() {
        throw new AssertionError();
    }

    public static final Preference<Integer> appVersion = new IntegerPreference("application.version", -1);
    public static final Preference<Integer> appOpenedCounter = new IntegerPreference("app_opened_counter", 0);

    public static class Gui {

        public static final Preference<Theme> theme = StringPreference.newInstance("org.solovyev.android.calculator.CalculatorActivity_calc_theme", Theme.metro_blue_theme, Theme.class);
        public static final Preference<Layout> layout = StringPreference.newInstance("org.solovyev.android.calculator.CalculatorActivity_calc_layout", Layout.main_calculator, Layout.class);
        public static final Preference<Boolean> feedbackWindowShown = new BooleanPreference("feedback_window_shown", false);
        public static final Preference<Boolean> showReleaseNotes = new BooleanPreference("org.solovyev.android.calculator.CalculatorActivity_show_release_notes", true);
        public static final Preference<Boolean> usePrevAsBack = new BooleanPreference("org.solovyev.android.calculator.CalculatorActivity_use_back_button_as_prev", false);
        public static final Preference<Boolean> showEqualsButton = new BooleanPreference("showEqualsButton", true);

        public static enum Theme {

            default_theme(ThemeType.other, R.style.default_theme),
            violet_theme(ThemeType.other, R.style.violet_theme),
            light_blue_theme(ThemeType.other, R.style.light_blue_theme),
            metro_blue_theme(ThemeType.metro, R.style.metro_blue_theme),
            metro_purple_theme(ThemeType.metro, R.style.metro_purple_theme),
            metro_green_theme(ThemeType.metro, R.style.metro_green_theme);

            @NotNull
            private final ThemeType themeType;

            @NotNull
            private final Integer themeId;

            Theme(@NotNull ThemeType themeType, Integer themeId) {
                this.themeType = themeType;
                this.themeId = themeId;
            }

            @NotNull
            public ThemeType getThemeType() {
                return themeType;
            }

            @NotNull
            public Integer getThemeId() {
                return themeId;
            }
        }

        public static enum ThemeType {
            metro,
            other
        }

        public static enum Layout {
            main_calculator(R.layout.main_calculator),
            main_cellphone(R.layout.main_cellphone),
            simple(R.layout.main_calculator);

            private final int layoutId;

            Layout(int layoutId) {
                this.layoutId = layoutId;
            }

            public int getLayoutId() {
                return layoutId;
            }
        }
    }

    static void setDefaultValues(@NotNull SharedPreferences preferences) {
        if (!CalculatorEngine.Preferences.groupingSeparator.isSet(preferences)) {
            final Locale locale = Locale.getDefault();
            if (locale != null) {
                final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(locale);
                int index = MathType.grouping_separator.getTokens().indexOf(String.valueOf(decimalFormatSymbols.getGroupingSeparator()));
                final String groupingSeparator;
                if (index >= 0) {
                    groupingSeparator = MathType.grouping_separator.getTokens().get(index);
                } else {
                    groupingSeparator = " ";
                }

                CalculatorEngine.Preferences.groupingSeparator.putPreference(preferences, groupingSeparator);
            }
        }

        if (!CalculatorEngine.Preferences.angleUnit.isSet(preferences)) {
            CalculatorEngine.Preferences.angleUnit.putDefault(preferences);
        }

        if (!CalculatorEngine.Preferences.numeralBase.isSet(preferences)) {
            CalculatorEngine.Preferences.numeralBase.putDefault(preferences);
        }

        if (!CalculatorEngine.Preferences.multiplicationSign.isSet(preferences)) {
            if ( AndroidUtils.isPhoneModel(AndroidUtils.PhoneModel.samsung_galaxy_s) || AndroidUtils.isPhoneModel(AndroidUtils.PhoneModel.samsung_galaxy_s_2) ) {
                // workaround ofr samsung galaxy s phones
                CalculatorEngine.Preferences.multiplicationSign.putPreference(preferences, "*");
            }
        }
    }

}
