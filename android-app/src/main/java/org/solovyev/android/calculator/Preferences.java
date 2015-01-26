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

import android.content.SharedPreferences;
import jscl.AngleUnit;
import jscl.NumeralBase;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;
import org.solovyev.android.prefs.*;
import org.solovyev.android.view.VibratorContainer;

import javax.annotation.Nonnull;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static org.solovyev.android.Android.isPhoneModel;
import static org.solovyev.android.DeviceModel.samsung_galaxy_s;
import static org.solovyev.android.DeviceModel.samsung_galaxy_s_2;

/**
 * User: serso
 * Date: 4/20/12
 * Time: 12:42 PM
 */
public final class Preferences {

	private Preferences() {
		throw new AssertionError();
	}

	public static final Preference<Integer> appVersion = IntegerPreference.of("application.version", -1);
	public static final Preference<Integer> appOpenedCounter = IntegerPreference.of("app_opened_counter", 0);

	public static class OnscreenCalculator {
		public static final Preference<Boolean> startOnBoot = BooleanPreference.of("onscreen_start_on_boot", false);
		public static final Preference<Boolean> showAppIcon = BooleanPreference.of("onscreen_show_app_icon", true);
	}

	public static class Calculations {

		public static final Preference<Boolean> calculateOnFly = BooleanPreference.of("calculations_calculate_on_fly", true);
		public static final Preference<Boolean> showCalculationMessagesDialog = BooleanPreference.of("show_calculation_messages_dialog", true);

		public static final Preference<NumeralBase> preferredNumeralBase = StringPreference.ofEnum("preferred_numeral_base", AndroidCalculatorEngine.Preferences.numeralBase.getDefaultValue(), NumeralBase.class);
		public static final Preference<AngleUnit> preferredAngleUnits = StringPreference.ofEnum("preferred_angle_units", AndroidCalculatorEngine.Preferences.angleUnit.getDefaultValue(), AngleUnit.class);
		public static final Preference<Long> lastPreferredPreferencesCheck = LongPreference.of("preferred_preferences_check_time", 0L);

	}

	public static class Ga {
		public static final Preference<Boolean> initialReportDone = BooleanPreference.of("ga.initial_report_done", false);
	}

	public static class Gui {

		public static final Preference<Theme> theme = StringPreference.ofEnum("org.solovyev.android.calculator.CalculatorActivity_calc_theme", Theme.metro_blue_theme, Theme.class);
		public static final Preference<Layout> layout = StringPreference.ofEnum("org.solovyev.android.calculator.CalculatorActivity_calc_layout", Layout.simple, Layout.class);
		public static final Preference<Boolean> feedbackWindowShown = BooleanPreference.of("feedback_window_shown", false);
		public static final Preference<Boolean> notesppAnnounceShown = BooleanPreference.of("notespp_announce_shown", false);
		public static final Preference<Boolean> showReleaseNotes = BooleanPreference.of("org.solovyev.android.calculator.CalculatorActivity_show_release_notes", true);
		public static final Preference<Boolean> usePrevAsBack = BooleanPreference.of("org.solovyev.android.calculator.CalculatorActivity_use_back_button_as_prev", false);
		public static final Preference<Boolean> showEqualsButton = BooleanPreference.of("showEqualsButton", true);
		public static final Preference<Boolean> autoOrientation = BooleanPreference.of("autoOrientation", true);
		public static final Preference<Boolean> hideNumeralBaseDigits = BooleanPreference.of("hideNumeralBaseDigits", true);
		public static final Preference<Boolean> preventScreenFromFading = BooleanPreference.of("preventScreenFromFading", true);
		public static final Preference<Boolean> colorDisplay = BooleanPreference.of("org.solovyev.android.calculator.CalculatorModel_color_display", true);

		@Nonnull
		public static Theme getTheme(@Nonnull SharedPreferences preferences) {
			return theme.getPreferenceNoError(preferences);
		}

		@Nonnull
		public static Layout getLayout(@Nonnull SharedPreferences preferences) {
			return layout.getPreferenceNoError(preferences);
		}

		public static enum Theme {

			default_theme(ThemeType.other, R.style.cpp_gray_theme),
			violet_theme(ThemeType.other, R.style.cpp_violet_theme),
			light_blue_theme(ThemeType.other, R.style.cpp_light_blue_theme),
			metro_blue_theme(ThemeType.metro, R.style.cpp_metro_blue_theme),
			metro_purple_theme(ThemeType.metro, R.style.cpp_metro_purple_theme),
			metro_green_theme(ThemeType.metro, R.style.cpp_metro_green_theme);

			@Nonnull
			private final ThemeType themeType;

			@Nonnull
			private final Integer themeId;

			Theme(@Nonnull ThemeType themeType, @Nonnull Integer themeId) {
				this.themeType = themeType;
				this.themeId = themeId;
			}

			@Nonnull
			public ThemeType getThemeType() {
				return themeType;
			}

			@Nonnull
			public Integer getThemeId() {
				return themeId;
			}
		}

		public static enum ThemeType {
			metro,
			other
		}

		public static enum Layout {
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
	}

	public static class Graph {
		public static final Preference<Boolean> plotImag = BooleanPreference.of("graph_plot_imag", false);
	}

	public static class History {
		public static final Preference<Boolean> showIntermediateCalculations = BooleanPreference.of("history_show_intermediate_calculations", false);
		public static final Preference<Boolean> showDatetime = BooleanPreference.of("history_show_datetime", true);
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
		applyDefaultPreference(preferences, Gui.notesppAnnounceShown);
		applyDefaultPreference(preferences, Gui.showReleaseNotes);
		applyDefaultPreference(preferences, Gui.usePrevAsBack);
		applyDefaultPreference(preferences, Gui.showEqualsButton);
		applyDefaultPreference(preferences, Gui.autoOrientation);
		applyDefaultPreference(preferences, Gui.hideNumeralBaseDigits);
		applyDefaultPreference(preferences, Gui.preventScreenFromFading);

		applyDefaultPreference(preferences, Graph.plotImag);
		applyDefaultPreference(preferences, History.showIntermediateCalculations);
		applyDefaultPreference(preferences, History.showDatetime);
		applyDefaultPreference(preferences, Calculations.calculateOnFly);
		applyDefaultPreference(preferences, Calculations.preferredAngleUnits);
		applyDefaultPreference(preferences, Calculations.preferredNumeralBase);

		applyDefaultPreference(preferences, OnscreenCalculator.showAppIcon);
		applyDefaultPreference(preferences, OnscreenCalculator.startOnBoot);

		applyDefaultPreference(preferences, Ga.initialReportDone);


		// renew value after each application start
		Calculations.showCalculationMessagesDialog.putDefault(preferences);
		Calculations.lastPreferredPreferencesCheck.putDefault(preferences);

		if (!VibratorContainer.Preferences.hapticFeedbackEnabled.isSet(preferences)) {
			VibratorContainer.Preferences.hapticFeedbackEnabled.putPreference(preferences, true);
		}

		if (!VibratorContainer.Preferences.hapticFeedbackDuration.isSet(preferences)) {
			VibratorContainer.Preferences.hapticFeedbackDuration.putPreference(preferences, 60L);
		}

	}

	private static void applyDefaultPreference(@Nonnull SharedPreferences preferences, @Nonnull Preference<?> preference) {
		preference.tryPutDefault(preferences);
	}

}
