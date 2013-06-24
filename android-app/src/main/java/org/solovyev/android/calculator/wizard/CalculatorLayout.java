package org.solovyev.android.calculator.wizard;

import android.content.SharedPreferences;
import org.solovyev.android.calculator.CalculatorPreferences;
import org.solovyev.android.calculator.R;

import javax.annotation.Nonnull;

import static org.solovyev.android.calculator.CalculatorPreferences.Gui.Layout.main_calculator;
import static org.solovyev.android.calculator.CalculatorPreferences.Gui.Layout.main_calculator_mobile;

/**
 * User: serso
 * Date: 6/19/13
 * Time: 12:39 AM
 */
enum CalculatorLayout {

	big_buttons(R.string.cpp_wizard_layout_big_buttons){
		@Override
		protected void apply(@Nonnull SharedPreferences preferences) {
			CalculatorPreferences.Gui.layout.putPreference(preferences, main_calculator_mobile);
		}
	},

	optimized(R.string.cpp_wizard_layout_optimized){
		@Override
		protected void apply(@Nonnull SharedPreferences preferences) {
			CalculatorPreferences.Gui.layout.putPreference(preferences, main_calculator);
		}
	};

	private final int nameResId;

	CalculatorLayout(int nameResId) {
		this.nameResId = nameResId;
	}

	int getNameResId() {
		return nameResId;
	}

	protected abstract void apply(@Nonnull SharedPreferences preferences);

	@Nonnull
	static CalculatorLayout getDefaultLayout(){
		return big_buttons;
	}

	@Nonnull
	static CalculatorLayout fromGuiLayout(@Nonnull CalculatorPreferences.Gui.Layout layout) {
		switch (layout) {
			case main_calculator:
			case main_cellphone:
			case simple:
				return optimized;
			case main_calculator_mobile:
			case simple_mobile:
				return big_buttons;
			default:
				return getDefaultLayout();
		}
	}
}
