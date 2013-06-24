package org.solovyev.android.calculator.wizard;

import android.content.SharedPreferences;
import jscl.AngleUnit;
import org.solovyev.android.calculator.CalculatorPreferences;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;

import javax.annotation.Nonnull;

import static org.solovyev.android.calculator.CalculatorPreferences.Gui.Layout.main_calculator;
import static org.solovyev.android.calculator.CalculatorPreferences.Gui.Layout.main_calculator_mobile;

/**
* User: serso
* Date: 6/17/13
* Time: 9:30 PM
*/
enum CalculatorMode {

	simple(R.string.cpp_wizard_mode_simple) {
		@Override
		protected void apply(@Nonnull SharedPreferences preferences) {
			final CalculatorPreferences.Gui.Layout layout = CalculatorPreferences.Gui.layout.getPreference(preferences);
			if (layout.isOptimized()) {
				CalculatorPreferences.Gui.layout.putPreference(preferences, CalculatorPreferences.Gui.Layout.simple);
			} else {
				CalculatorPreferences.Gui.layout.putPreference(preferences, CalculatorPreferences.Gui.Layout.simple_mobile);
			}
			CalculatorPreferences.Calculations.preferredAngleUnits.putPreference(preferences, AngleUnit.deg);
			AndroidCalculatorEngine.Preferences.angleUnit.putPreference(preferences, AngleUnit.deg);
			AndroidCalculatorEngine.Preferences.scienceNotation.putPreference(preferences, false);
			AndroidCalculatorEngine.Preferences.roundResult.putPreference(preferences, true);
		}
	},

	engineer(R.string.cpp_wizard_mode_engineer) {
		@Override
		protected void apply(@Nonnull SharedPreferences preferences) {
			final CalculatorPreferences.Gui.Layout layout = CalculatorPreferences.Gui.layout.getPreference(preferences);
			if (layout.isOptimized()) {
				CalculatorPreferences.Gui.layout.putPreference(preferences, main_calculator);
			} else {
				CalculatorPreferences.Gui.layout.putPreference(preferences, main_calculator_mobile);
			}
			CalculatorPreferences.Calculations.preferredAngleUnits.putPreference(preferences, AngleUnit.rad);
			AndroidCalculatorEngine.Preferences.angleUnit.putPreference(preferences, AngleUnit.rad);
			AndroidCalculatorEngine.Preferences.scienceNotation.putPreference(preferences, true);
			AndroidCalculatorEngine.Preferences.roundResult.putPreference(preferences, false);
		}
	};

	private final int nameResId;

	CalculatorMode(int nameResId) {
		this.nameResId = nameResId;
	}

	int getNameResId() {
		return nameResId;
	}

	protected abstract void apply(@Nonnull SharedPreferences preferences);

	@Nonnull
	static CalculatorMode getDefaultMode(){
		return engineer;
	}

	@Nonnull
	static CalculatorMode fromGuiLayout(@Nonnull CalculatorPreferences.Gui.Layout layout) {
		switch (layout) {
			case main_calculator:
			case main_cellphone:
			case main_calculator_mobile:
				return engineer;
			case simple:
			case simple_mobile:
				return simple;
			default:
				return getDefaultMode();
		}
	}
}
