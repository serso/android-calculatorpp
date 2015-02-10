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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static org.solovyev.android.calculator.CalculatorSpecialButton.*;

/**
 * User: serso
 * Date: 10/20/12
 * Time: 12:05 AM
 */
public enum CalculatorButton {

	/*digits*/
	one(R.id.cpp_button_1, "1"),
	two(R.id.cpp_button_2, "2"),
	three(R.id.cpp_button_3, "3"),
	four(R.id.cpp_button_4, "4"),
	five(R.id.cpp_button_5, "5"),
	six(R.id.cpp_button_6, "6"),
	seven(R.id.cpp_button_7, "7"),
	eight(R.id.cpp_button_8, "8"),
	nine(R.id.cpp_button_9, "9"),
	zero(R.id.cpp_button_0, "0"),

	period(R.id.cpp_button_period, "."),
	brackets(R.id.cpp_button_round_brackets, "()"),

	settings(R.id.cpp_button_settings, settings_detached),
	like(R.id.cpp_button_like, CalculatorSpecialButton.like),

	/*last row*/
	left(R.id.cpp_button_left, cursor_left),
	right(R.id.cpp_button_right, cursor_right),
	vars(R.id.cpp_button_vars, vars_detached),
	functions(R.id.cpp_button_functions, functions_detached),
	operators(R.id.cpp_button_operators, operators_detached),
	app(R.id.cpp_button_app, open_app),
	history(R.id.cpp_button_history, history_detached),

	/*operations*/
	multiplication(R.id.cpp_button_multiplication, "*"),
	division(R.id.cpp_button_division, "/"),
	plus(R.id.cpp_button_plus, "+"),
	subtraction(R.id.cpp_button_subtraction, "−"),
	percent(R.id.cpp_button_percent, "%"),
	power(R.id.cpp_button_power, "^"),

	/*last column*/
	clear(R.id.cpp_button_clear, CalculatorSpecialButton.clear),
	erase(R.id.cpp_button_erase, CalculatorSpecialButton.erase, CalculatorSpecialButton.clear),
	copy(R.id.cpp_button_copy, CalculatorSpecialButton.copy),
	paste(R.id.cpp_button_paste, CalculatorSpecialButton.paste),

	/*equals*/
	equals(R.id.cpp_button_equals, CalculatorSpecialButton.equals);


	private final int buttonId;

	@Nonnull
	private final String onClickText;

	@Nullable
	private final String onLongClickText;

	@Nonnull
	private static Map<Integer, CalculatorButton> buttonsByIds = new HashMap<Integer, CalculatorButton>();

	CalculatorButton(int buttonId, @Nonnull CalculatorSpecialButton onClickButton, @Nullable CalculatorSpecialButton onLongClickButton) {
		this(buttonId, onClickButton.getActionCode(), onLongClickButton == null ? null : onLongClickButton.getActionCode());
	}

	CalculatorButton(int buttonId, @Nonnull CalculatorSpecialButton onClickButton) {
		this(buttonId, onClickButton, null);
	}

	CalculatorButton(int buttonId, @Nonnull String onClickText, @Nullable String onLongClickText) {
		this.buttonId = buttonId;
		this.onClickText = onClickText;
		this.onLongClickText = onLongClickText;

	}

	CalculatorButton(int buttonId, @Nonnull String onClickText) {
		this(buttonId, onClickText, null);
	}

	public void onLongClick(@Nonnull Context context) {
		Locator.getInstance().getNotifier().showDebugMessage("Calculator++ Widget", "Button pressed: " + onLongClickText);
		if (onLongClickText != null) {
			Locator.getInstance().getKeyboard().buttonPressed(onLongClickText);
		}
	}

	public void onClick(@Nonnull Context context) {
		Locator.getInstance().getNotifier().showDebugMessage("Calculator++ Widget", "Button pressed: " + onClickText);
		Locator.getInstance().getKeyboard().buttonPressed(onClickText);
	}

	@Nullable
	public static CalculatorButton getById(int buttonId) {
		initButtonsByIdsMap();

		return buttonsByIds.get(buttonId);
	}

	private static void initButtonsByIdsMap() {
		if (buttonsByIds.isEmpty()) {
			// if not initialized

			final CalculatorButton[] calculatorButtons = values();

			final Map<Integer, CalculatorButton> localButtonsByIds = new HashMap<Integer, CalculatorButton>(calculatorButtons.length);
			for (CalculatorButton calculatorButton : calculatorButtons) {
				localButtonsByIds.put(calculatorButton.getButtonId(), calculatorButton);
			}

			buttonsByIds = localButtonsByIds;
		}
	}

	public int getButtonId() {
		return buttonId;
	}
}
