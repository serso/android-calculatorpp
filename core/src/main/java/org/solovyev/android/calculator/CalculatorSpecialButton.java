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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * User: serso
 * Date: 10/20/12
 * Time: 2:05 PM
 */
public enum CalculatorSpecialButton {

	history("history") {
		@Override
		public void onClick(@Nonnull CalculatorKeyboard keyboard) {
			Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_history, null);
		}
	},
	history_detached("history_detached") {
		@Override
		public void onClick(@Nonnull CalculatorKeyboard keyboard) {
			Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_history_detached, null);
		}
	},
	cursor_right("▶") {
		@Override
		public void onClick(@Nonnull CalculatorKeyboard keyboard) {
			keyboard.moveCursorRight();
		}
	},
	cursor_left("◀") {
		@Override
		public void onClick(@Nonnull CalculatorKeyboard keyboard) {
			keyboard.moveCursorLeft();
		}
	},
	settings("settings") {
		@Override
		public void onClick(@Nonnull CalculatorKeyboard keyboard) {
			Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_settings, null);
		}
	},

	settings_detached("settings_detached") {
		@Override
		public void onClick(@Nonnull CalculatorKeyboard keyboard) {
			Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_settings_detached, null);
		}
	},

	like("like") {
		@Override
		public void onClick(@Nonnull CalculatorKeyboard keyboard) {
			Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_like_dialog, null);
		}
	},
	erase("erase") {
		@Override
		public void onClick(@Nonnull CalculatorKeyboard keyboard) {
			Locator.getInstance().getEditor().erase();
		}
	},
	paste("paste") {
		@Override
		public void onClick(@Nonnull CalculatorKeyboard keyboard) {
			keyboard.pasteButtonPressed();
		}
	},
	copy("copy") {
		@Override
		public void onClick(@Nonnull CalculatorKeyboard keyboard) {
			keyboard.copyButtonPressed();
		}
	},
	equals("=") {
		@Override
		public void onClick(@Nonnull CalculatorKeyboard keyboard) {
			Locator.getInstance().getCalculator().evaluate();
		}
	},
	clear("clear") {
		@Override
		public void onClick(@Nonnull CalculatorKeyboard keyboard) {
			keyboard.clearButtonPressed();
		}
	},
	functions("functions") {
		@Override
		public void onClick(@Nonnull CalculatorKeyboard keyboard) {
			Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_functions, null);
		}
	},
	functions_detached("functions_detached") {
		@Override
		public void onClick(@Nonnull CalculatorKeyboard keyboard) {
			Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_functions_detached, null);
		}
	},
	open_app("open_app") {
		@Override
		public void onClick(@Nonnull CalculatorKeyboard keyboard) {
			Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.open_app, null);
		}
	},
	vars("vars") {
		@Override
		public void onClick(@Nonnull CalculatorKeyboard keyboard) {
			Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_vars, null);
		}
	},
	vars_detached("vars_detached") {
		@Override
		public void onClick(@Nonnull CalculatorKeyboard keyboard) {
			Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_vars_detached, null);
		}
	},
	operators("operators") {
		@Override
		public void onClick(@Nonnull CalculatorKeyboard keyboard) {
			Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_operators, null);
		}
	},

	operators_detached("operators_detached") {
		@Override
		public void onClick(@Nonnull CalculatorKeyboard keyboard) {
			Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_operators_detached, null);
		}
	};

	@Nonnull
	private static Map<String, CalculatorSpecialButton> buttonsByActionCodes = new HashMap<String, CalculatorSpecialButton>();

	@Nonnull
	private final String actionCode;

	CalculatorSpecialButton(@Nonnull String actionCode) {
		this.actionCode = actionCode;
	}

	@Nonnull
	public String getActionCode() {
		return actionCode;
	}

	@Nullable
	public static CalculatorSpecialButton getByActionCode(@Nonnull String actionCode) {
		initButtonsByActionCodesMap();
		return buttonsByActionCodes.get(actionCode);
	}

	public abstract void onClick(@Nonnull CalculatorKeyboard keyboard);

	private static void initButtonsByActionCodesMap() {
		if (buttonsByActionCodes.isEmpty()) {
			// if not initialized

			final CalculatorSpecialButton[] specialButtons = values();

			final Map<String, CalculatorSpecialButton> localButtonsByActionCodes = new HashMap<String, CalculatorSpecialButton>(specialButtons.length);
			for (CalculatorSpecialButton specialButton : specialButtons) {
				localButtonsByActionCodes.put(specialButton.getActionCode(), specialButton);
			}

			buttonsByActionCodes = localButtonsByActionCodes;
		}
	}
}
