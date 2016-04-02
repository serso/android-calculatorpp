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

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.provider.Settings;
import android.support.annotation.*;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.util.SparseArray;
import jscl.AngleUnit;
import jscl.NumeralBase;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.about.AboutActivity;
import org.solovyev.android.calculator.functions.FunctionsActivity;
import org.solovyev.android.calculator.history.HistoryActivity;
import org.solovyev.android.calculator.language.Languages;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.operators.OperatorsActivity;
import org.solovyev.android.calculator.preferences.PreferencesActivity;
import org.solovyev.android.calculator.variables.VariablesActivity;
import org.solovyev.android.calculator.wizard.WizardActivity;
import org.solovyev.android.prefs.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormatSymbols;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import static org.solovyev.android.prefs.IntegerPreference.DEF_VALUE;

public final class Preferences {

    private static final Preference<Integer> version = IntegerPreference.of("version", 2);

    private Preferences() {
        throw new AssertionError();
    }

    static void init(@Nonnull Application application, @Nonnull SharedPreferences preferences) {
        final int currentVersion = getVersion(preferences);
        if (currentVersion == 0) {
            final SharedPreferences.Editor editor = preferences.edit();
            setInitialDefaultValues(application, preferences, editor);
            editor.apply();
        } else if (currentVersion == 1) {
            final SharedPreferences.Editor editor = preferences.edit();
            if (!Gui.vibrateOnKeypress.isSet(preferences)) {
                Gui.vibrateOnKeypress.putPreference(editor, Deleted.hapticFeedback.getPreference(preferences) > 0);
            }
            migratePreference(preferences, editor, Gui.keepScreenOn, Deleted.preventScreenFromFading);
            migratePreference(preferences, editor, Gui.theme, Deleted.theme);
            migratePreference(preferences, editor, Gui.useBackAsPrevious, Deleted.usePrevAsBack);
            migratePreference(preferences, editor, Gui.showReleaseNotes, Deleted.showReleaseNotes);
            migratePreference(preferences, editor, Gui.rotateScreen, Deleted.autoOrientation);
            final String layout = Deleted.layout.getPreference(preferences);
            if (TextUtils.equals(layout, "main_calculator")) {
                Gui.mode.putPreference(editor, Gui.Mode.engineer);
            } else if (TextUtils.equals(layout, "simple")) {
                Gui.mode.putPreference(editor, Gui.Mode.simple);
            } else if (!Gui.mode.isSet(preferences)) {
                Gui.mode.putDefault(editor);
            }
            version.putDefault(editor);
            editor.apply();
        }
    }

    private static int getVersion(@Nonnull SharedPreferences preferences) {
        if (version.isSet(preferences)) {
            return version.getPreference(preferences);
        } else if (Deleted.appVersion.isSet(preferences)) {
            return  1;
        }
        return 0;
    }

    private static <T> void migratePreference(@Nonnull SharedPreferences preferences, @NonNull SharedPreferences.Editor editor, @NonNull Preference<T> to, @NonNull Preference<T> from) {
        if (!to.isSet(preferences)) {
            to.putPreference(editor, from.getPreferenceNoError(preferences));
        }
    }

    private static void setInitialDefaultValues(@Nonnull Application application, @Nonnull SharedPreferences preferences, @Nonnull SharedPreferences.Editor editor) {
        if (!Engine.Preferences.groupingSeparator.isSet(preferences)) {
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

                Engine.Preferences.groupingSeparator.putPreference(editor, groupingSeparator);
            }
        }

        Engine.Preferences.angleUnit.tryPutDefault(preferences, editor);
        Engine.Preferences.numeralBase.tryPutDefault(preferences, editor);

        Gui.theme.tryPutDefault(preferences, editor);
        Gui.mode.tryPutDefault(preferences, editor);
        Gui.showReleaseNotes.tryPutDefault(preferences, editor);
        Gui.useBackAsPrevious.tryPutDefault(preferences, editor);
        Gui.rotateScreen.tryPutDefault(preferences, editor);
        Gui.keepScreenOn.tryPutDefault(preferences, editor);
        Gui.language.tryPutDefault(preferences, editor);

        Calculations.calculateOnFly.tryPutDefault(preferences, editor);

        Onscreen.showAppIcon.tryPutDefault(preferences, editor);
        Onscreen.theme.tryPutDefault(preferences, editor);

        Widget.theme.tryPutDefault(preferences, editor);
        version.putDefault(editor);

        final ContentResolver cr = application.getContentResolver();
        if (cr != null) {
            final boolean vibrateOnKeyPress = Settings.System.getInt(cr, Settings.System.HAPTIC_FEEDBACK_ENABLED, 0) != 0;
            Gui.vibrateOnKeypress.putPreference(editor, vibrateOnKeyPress);
        }
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
        public static final Preference<Boolean> showAppIcon = BooleanPreference.of("onscreen_show_app_icon", true);
        public static final Preference<SimpleTheme> theme = StringPreference.ofEnum("onscreen.theme", SimpleTheme.default_theme, SimpleTheme.class);

        @Nonnull
        public static SimpleTheme getTheme(@Nonnull SharedPreferences preferences) {
            return theme.getPreferenceNoError(preferences);
        }
    }

    public static class Calculations {
        public static final Preference<Boolean> calculateOnFly = BooleanPreference.of("calculations_calculate_on_fly", true);
    }

    public static class App {
    }

    public static class Gui {

        public static final Preference<Theme> theme = StringPreference.ofEnum("gui.theme", Theme.material_theme, Theme.class);
        public static final Preference<Mode> mode = StringPreference.ofEnum("gui.mode", Mode.simple, Mode.class);
        public static final Preference<String> language = StringPreference.of("gui.language", Languages.SYSTEM_LANGUAGE_CODE);
        public static final Preference<Boolean> showReleaseNotes = BooleanPreference.of("gui.showReleaseNotes", true);
        public static final Preference<Boolean> useBackAsPrevious = BooleanPreference.of("gui.useBackAsPrevious", false);
        public static final Preference<Boolean> rotateScreen = BooleanPreference.of("gui.rotateScreen", true);
        public static final Preference<Boolean> keepScreenOn = BooleanPreference.of("gui.keepScreenOn", true);
        public static final Preference<Boolean> vibrateOnKeypress = BooleanPreference.of("gui.vibrateOnKeypress", true);

        @Nonnull
        public static Theme getTheme(@Nonnull SharedPreferences preferences) {
            return theme.getPreferenceNoError(preferences);
        }

        @Nonnull
        public static Mode getMode(@Nonnull SharedPreferences preferences) {
            return mode.getPreferenceNoError(preferences);
        }

        public enum Theme {

            default_theme(R.style.Cpp_Theme_Gray),
            violet_theme(R.style.Cpp_Theme_Violet),
            light_blue_theme(R.style.Cpp_Theme_Blue),
            metro_blue_theme(R.string.p_metro_blue_theme, R.style.Cpp_Theme_Metro_Blue, R.style.Cpp_Theme_Metro_Blue_Calculator, R.style.Cpp_Theme_Wizard, R.style.Cpp_Theme_Metro_Blue_Dialog, R.style.Cpp_Theme_Material_Dialog_Alert),
            metro_purple_theme(R.string.p_metro_purple_theme, R.style.Cpp_Theme_Metro_Purple, R.style.Cpp_Theme_Metro_Purple_Calculator, R.style.Cpp_Theme_Wizard, R.style.Cpp_Theme_Metro_Purple_Dialog, R.style.Cpp_Theme_Material_Dialog_Alert),
            metro_green_theme(R.string.p_metro_green_theme, R.style.Cpp_Theme_Metro_Green, R.style.Cpp_Theme_Metro_Green_Calculator, R.style.Cpp_Theme_Wizard, R.style.Cpp_Theme_Metro_Green_Dialog, R.style.Cpp_Theme_Material_Dialog_Alert),
            material_theme(R.string.cpp_theme_dark, R.style.Cpp_Theme_Material, R.style.Cpp_Theme_Material_Calculator),
            material_light_theme(R.string.cpp_theme_light, R.style.Cpp_Theme_Material_Light, R.style.Cpp_Theme_Material_Light_Calculator, R.style.Cpp_Theme_Wizard_Light, R.style.Cpp_Theme_Material_Light_Dialog, R.style.Cpp_Theme_Material_Light_Dialog_Alert);

            private static final SparseArray<TextColor> textColors = new SparseArray<>();

            @StringRes
            public final int name;
            @StyleRes
            public final int theme;
            @StyleRes
            public final int calculatorTheme;
            @StyleRes
            public final int wizardTheme;
            @StyleRes
            public final int dialogTheme;
            @StyleRes
            public final int alertDialogTheme;
            public final boolean light;

            Theme(@StyleRes int theme) {
                this(R.string.cpp_theme_dark, theme, theme);
            }

            Theme(@StringRes int name, @StyleRes int theme, @StyleRes int calculatorTheme) {
                this(name, theme, calculatorTheme, R.style.Cpp_Theme_Wizard, R.style.Cpp_Theme_Material_Dialog, R.style.Cpp_Theme_Material_Dialog_Alert);
            }

            Theme(@StringRes int name, @StyleRes int theme, @StyleRes int calculatorTheme, @StyleRes int wizardTheme, @StyleRes int dialogTheme, @StyleRes int alertDialogTheme) {
                this.name = name;
                this.theme = theme;
                this.calculatorTheme = calculatorTheme;
                this.wizardTheme = wizardTheme;
                this.dialogTheme = dialogTheme;
                this.alertDialogTheme = alertDialogTheme;
                this.light = theme == R.style.Cpp_Theme_Material_Light;
            }

            public int getThemeFor(@Nonnull Context context) {
                if (context instanceof CalculatorActivity) {
                    return calculatorTheme;
                }
                if (context instanceof WizardActivity) {
                    return wizardTheme;
                }
                if (context instanceof FunctionsActivity.Dialog) {
                    return dialogTheme;
                }
                if (context instanceof PreferencesActivity.Dialog) {
                    return dialogTheme;
                }
                if (context instanceof VariablesActivity.Dialog) {
                    return dialogTheme;
                }
                if (context instanceof OperatorsActivity.Dialog) {
                    return dialogTheme;
                }
                if (context instanceof HistoryActivity.Dialog) {
                    return dialogTheme;
                }
                if (context instanceof AboutActivity.Dialog) {
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

        public enum Mode {
            engineer(R.string.cpp_wizard_mode_engineer),
            simple(R.string.cpp_wizard_mode_simple);

            @StringRes
            public final int name;

            Mode(@StringRes int name) {
                this.name = name;
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

    @SuppressWarnings("unused")
    static class Deleted {
        static final Preference<Integer> appVersion = IntegerPreference.of("application.version", DEF_VALUE);
        static final Preference<Boolean> feedbackWindowShown = BooleanPreference.of("feedback_window_shown", false);
        static final Preference<Integer> appOpenedCounter = IntegerPreference.of("app_opened_counter", 0);
        static final Preference<Long> hapticFeedback = NumberToStringPreference.of("hapticFeedback", 60L, Long.class);
        static final Preference<Boolean> colorDisplay = BooleanPreference.of("org.solovyev.android.calculator.CalculatorModel_color_display", true);
        static final Preference<Boolean> preventScreenFromFading = BooleanPreference.of("preventScreenFromFading", true);
        static final Preference<Gui.Theme> theme = StringPreference.ofEnum("org.solovyev.android.calculator.CalculatorActivity_calc_theme", Gui.Theme.material_theme, Gui.Theme.class);
        static final StringPreference<String> layout = StringPreference.of("org.solovyev.android.calculator.CalculatorActivity_calc_layout", "simple");
        static final Preference<Boolean> showReleaseNotes = BooleanPreference.of("org.solovyev.android.calculator.CalculatorActivity_show_release_notes", true);
        static final Preference<Boolean> usePrevAsBack = BooleanPreference.of("org.solovyev.android.calculator.CalculatorActivity_use_back_button_as_prev", false);
        static final Preference<Boolean> showEqualsButton = BooleanPreference.of("showEqualsButton", true);
        static final Preference<Boolean> autoOrientation = BooleanPreference.of("autoOrientation", true);
        static final Preference<Boolean> startOnBoot = BooleanPreference.of("onscreen_start_on_boot", false);
        static final Preference<Boolean> plotImag = BooleanPreference.of("graph_plot_imag", false);
    }
}
