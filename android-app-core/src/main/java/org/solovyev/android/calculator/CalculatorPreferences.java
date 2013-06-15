package org.solovyev.android.calculator;

import android.content.SharedPreferences;
import jscl.AngleUnit;
import jscl.NumeralBase;
import javax.annotation.Nonnull;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;
import org.solovyev.android.prefs.*;
import org.solovyev.android.view.VibratorContainer;

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
public final class CalculatorPreferences {

	private CalculatorPreferences() {
		throw new AssertionError();
	}

	public static final Preference<Integer> appVersion = IntegerPreference.of("application.version", -1);
	public static final Preference<Integer> appOpenedCounter = IntegerPreference.of("app_opened_counter", 0);

	public static class OnscreenCalculator {
		public static final Preference<Boolean> startOnBoot = BooleanPreference.of("onscreen_start_on_boot", false);
		public static final Preference<Boolean> showAppIcon = BooleanPreference.of("onscreen_show_app_icon", true);
		public static final Preference<Boolean> removeIconDialogShown = BooleanPreference.of("onscreen_remove_icon_dialog_shown", false);
	}

	public static class Calculations {

		public static final Preference<Boolean> calculateOnFly = BooleanPreference.of("calculations_calculate_on_fly", true);
		public static final Preference<Boolean> showCalculationMessagesDialog = BooleanPreference.of("show_calculation_messages_dialog", true);

		public static final Preference<NumeralBase> preferredNumeralBase = StringPreference.ofEnum("preferred_numeral_base", AndroidCalculatorEngine.Preferences.numeralBase.getDefaultValue(), NumeralBase.class);
		public static final Preference<AngleUnit> preferredAngleUnits = StringPreference.ofEnum("preferred_angle_units", AndroidCalculatorEngine.Preferences.angleUnit.getDefaultValue(), AngleUnit.class);
		public static final Preference<Long> lastPreferredPreferencesCheck = LongPreference.of("preferred_preferences_check_time", 0L);

	}

	public static class Gui {

		public static final Preference<Theme> theme = StringPreference.ofEnum("org.solovyev.android.calculator.CalculatorActivity_calc_theme", Theme.metro_blue_theme, Theme.class);
		public static final Preference<Layout> layout = StringPreference.ofEnum("org.solovyev.android.calculator.CalculatorActivity_calc_layout", Layout.main_calculator, Layout.class);
		public static final Preference<Boolean> feedbackWindowShown = BooleanPreference.of("feedback_window_shown", false);
		public static final Preference<Boolean> notesppAnnounceShown = BooleanPreference.of("notespp_announce_shown", false);
		public static final Preference<Boolean> showReleaseNotes = BooleanPreference.of("org.solovyev.android.calculator.CalculatorActivity_show_release_notes", true);
		public static final Preference<Boolean> usePrevAsBack = BooleanPreference.of("org.solovyev.android.calculator.CalculatorActivity_use_back_button_as_prev", false);
		public static final Preference<Boolean> showEqualsButton = BooleanPreference.of("showEqualsButton", true);
		public static final Preference<Boolean> autoOrientation = BooleanPreference.of("autoOrientation", true);
		public static final Preference<Boolean> hideNumeralBaseDigits = BooleanPreference.of("hideNumeralBaseDigits", true);

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
			main_calculator(R.layout.main_calculator),
			main_calculator_mobile(R.layout.main_calculator_mobile),

			// not used anymore
			@Deprecated
			main_cellphone(R.layout.main_calculator),

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

	public static class Graph {
		public static final Preference<Boolean> plotImag = BooleanPreference.of("graph_plot_imag", false);
	}

	public static class History {
		public static final Preference<Boolean> showIntermediateCalculations = BooleanPreference.of("history_show_intermediate_calculations", false);
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

		applyDefaultPreference(preferences, Graph.plotImag);
		applyDefaultPreference(preferences, History.showIntermediateCalculations);
		applyDefaultPreference(preferences, Calculations.calculateOnFly);
		applyDefaultPreference(preferences, Calculations.preferredAngleUnits);
		applyDefaultPreference(preferences, Calculations.preferredNumeralBase);

		applyDefaultPreference(preferences, OnscreenCalculator.showAppIcon);
		applyDefaultPreference(preferences, OnscreenCalculator.startOnBoot);


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
