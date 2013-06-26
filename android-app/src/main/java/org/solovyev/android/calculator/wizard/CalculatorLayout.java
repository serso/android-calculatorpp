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

	big_buttons(R.string.cpp_wizard_layout_big_buttons) {
		@Override
		protected void apply(@Nonnull SharedPreferences preferences) {
			CalculatorPreferences.Gui.layout.putPreference(preferences, main_calculator_mobile);
		}
	},

	optimized(R.string.cpp_wizard_layout_optimized) {
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
	static CalculatorLayout getDefaultLayout() {
		return big_buttons;
	}

	@Nonnull
	static CalculatorLayout fromGuiLayout(@Nonnull CalculatorPreferences.Gui.Layout layout) {
		if (layout.isOptimized()) {
			return optimized;
		} else {
			return big_buttons;
		}
	}
}
