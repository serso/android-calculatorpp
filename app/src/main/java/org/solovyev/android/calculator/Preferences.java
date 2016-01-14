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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StyleRes;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;

import org.solovyev.android.Check;
import org.solovyev.android.calculator.language.Languages;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;
import org.solovyev.android.calculator.preferences.PurchaseDialogActivity;
import org.solovyev.android.calculator.wizard.WizardActivity;
import org.solovyev.android.prefs.BooleanPreference;
import org.solovyev.android.prefs.IntegerPreference;
import org.solovyev.android.prefs.LongPreference;
import org.solovyev.android.prefs.NumberToStringPreference;
import org.solovyev.android.prefs.Preference;
import org.solovyev.android.prefs.StringPreference;

import java.text.DecimalFormatSymbols;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import jscl.AngleUnit;
import jscl.NumeralBase;

import static org.solovyev.android.Android.isPhoneModel;
import static org.solovyev.android.DeviceModel.samsung_galaxy_s;
import static org.solovyev.android.DeviceModel.samsung_galaxy_s_2;

public final class Preferences {

    public static final Preference<Integer> appVersion = IntegerPreference.of("application.version", -1);
    public static final Preference<Integer> appOpenedCounter = IntegerPreference.of("app_opened_counter", 0);
    private Preferences() {
        throw new AssertionError();
    }

    static void setDefaultValues(@Nonnull SharedPreferences preferences) {

        if (!AndroidCalculatorEngine.Preferences.groupingSeparator.isSet(preferences)) {
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

                AndroidCalculatorEngine.Preferences.groupingSeparator.putPreference(preferences, groupingSeparator);
            }
        }

        if (!AndroidCalculatorEngine.Preferences.angleUnit.isSet(preferences)) {
            AndroidCalculatorEngine.Preferences.angleUnit.putDefault(preferences);
        }

        if (!AndroidCalculatorEngine.Preferences.numeralBase.isSet(preferences)) {
            AndroidCalculatorEngine.Preferences.numeralBase.putDefault(preferences);
        }

        if (!AndroidCalculatorEngine.Preferences.multiplicationSign.isSet(preferences)) {
            if (isPhoneModel(samsung_galaxy_s) || isPhoneModel(samsung_galaxy_s_2)) {
                // workaround ofr samsung galaxy s phones
                AndroidCalculatorEngine.Preferences.multiplicationSign.putPreference(preferences, "*");
            }
        }

        applyDefaultPreference(preferences, Gui.theme);
        applyDefaultPreference(preferences, Gui.layout);
        if (Gui.layout.getPreference(preferences) == Gui.Layout.main_cellphone) {
            Gui.layout.putDefault(preferences);
        }
        applyDefaultPreference(preferences, Gui.feedbackWindowShown);
        applyDefaultPreference(preferences, Gui.showReleaseNotes);
        applyDefaultPreference(preferences, Gui.usePrevAsBack);
        applyDefaultPreference(preferences, Gui.showEqualsButton);
        applyDefaultPreference(preferences, Gui.autoOrientation);
        applyDefaultPreference(preferences, Gui.hideNumeralBaseDigits);
        applyDefaultPreference(preferences, Gui.preventScreenFromFading);
        applyDefaultPreference(preferences, Gui.language);

        applyDefaultPreference(preferences, Graph.plotImag);
        applyDefaultPreference(preferences, Calculations.calculateOnFly);
        applyDefaultPreference(preferences, Calculations.preferredAngleUnits);
        applyDefaultPreference(preferences, Calculations.preferredNumeralBase);

        applyDefaultPreference(preferences, Onscreen.showAppIcon);
        applyDefaultPreference(preferences, Onscreen.startOnBoot);
        applyDefaultPreference(preferences, Onscreen.theme);

        applyDefaultPreference(preferences, Widget.theme);

        // renew value after each application start
        Calculations.showCalculationMessagesDialog.putDefault(preferences);
        Calculations.lastPreferredPreferencesCheck.putDefault(preferences);
    }

    private static void applyDefaultPreference(@Nonnull SharedPreferences preferences, @Nonnull Preference<?> preference) {
        preference.tryPutDefault(preferences);
    }

    public enum SimpleTheme {

        default_theme(0, 0, null),
        metro_blue_theme(R.layout.onscreen_layout, R.layout.widget_layout, Gui.Theme.metro_blue_theme),
        material_theme(R.layout.onscreen_layout_material, R.layout.widget_layout_material, Gui.Theme.material_theme),
        material_light_theme(R.layout.onscreen_layout_material_light, R.layout.widget_layout_material_light, Gui.Theme.material_light_theme, true);

        @LayoutRes
        private final int onscreenLayout;

        @LayoutRes
        private final int widgetLayout;

        @Nullable
        private final Gui.Theme appTheme;

        public final boolean light;

        @Nonnull
        private final Map<Gui.Theme, SimpleTheme> cache = new EnumMap<>(Gui.Theme.class);

        SimpleTheme(int onscreenLayout, int widgetLayout, @Nullable Gui.Theme appTheme) {
            this(onscreenLayout, widgetLayout, appTheme, false);
        }

        SimpleTheme(int onscreenLayout, int widgetLayout, @Nullable Gui.Theme appTheme, boolean light) {
            this.onscreenLayout = onscreenLayout;
            this.widgetLayout = widgetLayout;
            this.appTheme = appTheme;
            this.light = light;
        }

        public int getOnscreenLayout(@Nonnull Gui.Theme appTheme) {
            return resolveThemeFor(appTheme).onscreenLayout;
        }

        public int getWidgetLayout(@Nonnull Gui.Theme appTheme) {
            return resolveThemeFor(appTheme).widgetLayout;
        }

        @Nonnull
        public SimpleTheme resolveThemeFor(@Nonnull Gui.Theme appTheme) {
            if (this == default_theme) {
                SimpleTheme theme = cache.get(appTheme);
                if (theme == null) {
                    theme = lookUpThemeFor(appTheme);
                    cache.put(appTheme, theme);
                }
                return theme;
            }
            return this;
        }

        @Nonnull
        private SimpleTheme lookUpThemeFor(@Nonnull Gui.Theme appTheme) {
            Check.isTrue(this == default_theme);
            // find direct match
            for (SimpleTheme theme : values()) {
                if (theme.appTheme == appTheme) {
                    return theme;
                }
            }

            // for metro themes return metro theme
            if (appTheme == Gui.Theme.metro_green_theme || appTheme == Gui.Theme.metro_purple_theme) {
                return metro_blue_theme;
            }

            // for old themes return dark material
            return material_theme;
        }

        @Nullable
        public Gui.Theme getAppTheme() {
            return appTheme;
        }

        @ColorRes
        public int getDisplayTextColor(boolean error) {
            if (error) {
                return light ? R.color.cpp_text_inverse_error : R.color.cpp_text_error;
            }
            return light ? R.color.cpp_text_inverse : R.color.cpp_text;
        }
    }

    public static class Widget {
        public static final Preference<SimpleTheme> theme = StringPreference.ofEnum("widget.theme", SimpleTheme.default_theme, SimpleTheme.class);

        @Nonnull
        public static SimpleTheme getTheme(@Nonnull SharedPreferences preferences) {
            return theme.getPreferenceNoError(preferences);
        }

    }

    public static class Onscreen {
        public static final Preference<Boolean> startOnBoot = BooleanPreference.of("onscreen_start_on_boot", false);
        public static final Preference<Boolean> showAppIcon = BooleanPreference.of("onscreen_show_app_icon", true);
        public static final Preference<SimpleTheme> theme = StringPreference.ofEnum("onscreen.theme", SimpleTheme.default_theme, SimpleTheme.class);

        @Nonnull
        public static SimpleTheme getTheme(@Nonnull SharedPreferences preferences) {
            return theme.getPreferenceNoError(preferences);
        }
    }

    public static class Calculations {

        public static final Preference<Boolean> calculateOnFly = BooleanPreference.of("calculations_calculate_on_fly", true);
        public static final Preference<Boolean> showCalculationMessagesDialog = BooleanPreference.of("show_calculation_messages_dialog", true);

        public static final Preference<NumeralBase> preferredNumeralBase = StringPreference.ofEnum("preferred_numeral_base", AndroidCalculatorEngine.Preferences.numeralBase.getDefaultValue(), NumeralBase.class);
        public static final Preference<AngleUnit> preferredAngleUnits = StringPreference.ofEnum("preferred_angle_units", AndroidCalculatorEngine.Preferences.angleUnit.getDefaultValue(), AngleUnit.class);
        public static final Preference<Long> lastPreferredPreferencesCheck = LongPreference.of("preferred_preferences_check_time", 0L);

    }

    public static class Gui {

        public static final Preference<Theme> theme = StringPreference.ofEnum("org.solovyev.android.calculator.CalculatorActivity_calc_theme", Theme.material_theme, Theme.class);
        public static final Preference<Layout> layout = StringPreference.ofEnum("org.solovyev.android.calculator.CalculatorActivity_calc_layout", Layout.simple, Layout.class);
        public static final Preference<String> language = StringPreference.of("gui.language", Languages.SYSTEM_LANGUAGE_CODE);
        public static final Preference<Boolean> feedbackWindowShown = BooleanPreference.of("feedback_window_shown", false);
        public static final Preference<Boolean> showReleaseNotes = BooleanPreference.of("org.solovyev.android.calculator.CalculatorActivity_show_release_notes", true);
        public static final Preference<Boolean> usePrevAsBack = BooleanPreference.of("org.solovyev.android.calculator.CalculatorActivity_use_back_button_as_prev", false);
        public static final Preference<Boolean> showEqualsButton = BooleanPreference.of("showEqualsButton", true);
        public static final Preference<Boolean> autoOrientation = BooleanPreference.of("autoOrientation", true);
        public static final Preference<Boolean> hideNumeralBaseDigits = BooleanPreference.of("hideNumeralBaseDigits", true);
        public static final Preference<Boolean> preventScreenFromFading = BooleanPreference.of("preventScreenFromFading", true);
        public static final Preference<Boolean> colorDisplay = BooleanPreference.of("org.solovyev.android.calculator.CalculatorModel_color_display", true);
        public static final Preference<Long> hapticFeedback = NumberToStringPreference.of("hapticFeedback", 60L, Long.class);

        @Nonnull
        public static Theme getTheme(@Nonnull SharedPreferences preferences) {
            return theme.getPreferenceNoError(preferences);
        }

        @Nonnull
        public static Layout getLayout(@Nonnull SharedPreferences preferences) {
            return layout.getPreferenceNoError(preferences);
        }

        public enum Theme {

            default_theme(R.style.Cpp_Theme_Gray),
            violet_theme(R.style.Cpp_Theme_Violet),
            light_blue_theme(R.style.Cpp_Theme_Blue),
            metro_blue_theme(R.style.Cpp_Theme_Metro_Blue),
            metro_purple_theme(R.style.Cpp_Theme_Metro_Purple),
            metro_green_theme(R.style.Cpp_Theme_Metro_Green),
            material_theme(R.style.Cpp_Theme_Material),
            material_light_theme(R.style.Cpp_Theme_Material_Light, R.style.Cpp_Theme_Wizard_Light, R.style.Cpp_Theme_Material_Light_Dialog, R.style.Cpp_Theme_Material_Light_Dialog_Alert);

            private static final SparseArray<TextColor> textColors = new SparseArray<>();

            @StyleRes
            public final int theme;
            @StyleRes
            public final int wizardTheme;
            @StyleRes
            public final int dialogTheme;
            @StyleRes
            public final int alertDialogTheme;
            public final boolean light;

            Theme(@StyleRes int theme) {
                this(theme, R.style.Cpp_Theme_Wizard, R.style.Cpp_Theme_Material_Dialog, R.style.Cpp_Theme_Material_Dialog_Alert);
            }

            Theme(@StyleRes int theme, @StyleRes int wizardTheme, @StyleRes int dialogTheme, @StyleRes int alertDialogTheme) {
                this.theme = theme;
                this.wizardTheme = wizardTheme;
                this.dialogTheme = dialogTheme;
                this.alertDialogTheme = alertDialogTheme;
                this.light = theme == R.style.Cpp_Theme_Material_Light;
            }

            public int getThemeFor(@Nullable Context context) {
                if (context instanceof WizardActivity) {
                    return wizardTheme;
                }
                if (context instanceof PurchaseDialogActivity) {
                    return dialogTheme;
                }
                return theme;
            }

            @Nonnull
            public TextColor getTextColorFor(@Nonnull Context context) {
                final int themeId = getThemeFor(context);
                TextColor textColor = textColors.get(themeId);
                if (textColor == null) {
                    final ContextThemeWrapper themeContext = new ContextThemeWrapper(context, themeId);
                    final TypedArray a = themeContext.obtainStyledAttributes(themeId, new int[]{R.attr.cpp_text_color, R.attr.cpp_text_color_error});
                    final int normal = a.getColor(0, Color.BLACK);
                    final int error = a.getColor(1, Color.WHITE);
                    a.recycle();
                    textColor = new TextColor(normal, error);
                    textColors.append(themeId, textColor);
                }
                return textColor;
            }
        }

        public enum Layout {
            main_calculator(R.layout.main_calculator, R.string.p_layout_calculator, true),
            main_calculator_mobile(R.layout.main_calculator_mobile, R.string.p_layout_calculator_mobile, false),

            // not used anymore
            @Deprecated
            main_cellphone(R.layout.main_calculator, 0, true),

            simple(R.layout.main_calculator, R.string.p_layout_simple, true),
            simple_mobile(R.layout.main_calculator_mobile, R.string.p_layout_simple_mobile, false);

            private final int layoutId;
            private final int nameResId;
            private final boolean optimized;

            Layout(int layoutId, int nameResId, boolean optimized) {
                this.layoutId = layoutId;
                this.nameResId = nameResId;
                this.optimized = optimized;
            }

            public int getLayoutId() {
                return layoutId;
            }

            public int getNameResId() {
                return nameResId;
            }

            public boolean isOptimized() {
                return optimized;
            }
        }

        public static final class TextColor {
            public final int normal;
            public final int error;

            TextColor(int normal, int error) {
                this.normal = normal;
                this.error = error;
            }
        }
    }

    public static class Graph {
        public static final Preference<Boolean> plotImag = BooleanPreference.of("graph_plot_imag", false);
    }
}
