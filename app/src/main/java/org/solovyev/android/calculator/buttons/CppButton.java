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

package org.solovyev.android.calculator.buttons;

import android.util.SparseArray;

import org.solovyev.android.Check;
import org.solovyev.android.calculator.R;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.solovyev.android.calculator.buttons.CppSpecialButton.cursor_left;
import static org.solovyev.android.calculator.buttons.CppSpecialButton.cursor_right;
import static org.solovyev.android.calculator.buttons.CppSpecialButton.functions_detached;
import static org.solovyev.android.calculator.buttons.CppSpecialButton.history_detached;
import static org.solovyev.android.calculator.buttons.CppSpecialButton.open_app;
import static org.solovyev.android.calculator.buttons.CppSpecialButton.operators_detached;
import static org.solovyev.android.calculator.buttons.CppSpecialButton.settings_detached;
import static org.solovyev.android.calculator.buttons.CppSpecialButton.vars_detached;

public enum CppButton {

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
    settings_widget(R.id.cpp_button_settings_widget, CppSpecialButton.settings_widget),
    like(R.id.cpp_button_like, CppSpecialButton.like),

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
    clear(R.id.cpp_button_clear, CppSpecialButton.clear),
    erase(R.id.cpp_button_erase, CppSpecialButton.erase, CppSpecialButton.clear),
    copy(R.id.cpp_button_copy, CppSpecialButton.copy),
    paste(R.id.cpp_button_paste, CppSpecialButton.paste),

    /*equals*/
    equals(R.id.cpp_button_equals, CppSpecialButton.equals);

    @Nonnull
    private static SparseArray<CppButton> buttonsByIds = new SparseArray<>();
    public final int id;
    @Nonnull
    public final String action;
    @Nullable
    public final String actionLong;

    CppButton(int id, @Nonnull CppSpecialButton button, @Nullable CppSpecialButton buttonLong) {
        this(id, button.action, buttonLong == null ? null : buttonLong.getAction());
    }

    CppButton(int id, @Nonnull CppSpecialButton onClickButton) {
        this(id, onClickButton, null);
    }

    CppButton(int id, @Nonnull String action, @Nullable String actionLong) {
        this.id = id;
        this.action = action;
        this.actionLong = actionLong;

    }

    CppButton(int id, @Nonnull String action) {
        this(id, action, null);
    }

    @Nullable
    public static CppButton getById(int buttonId) {
        initButtonsByIdsMap();
        return buttonsByIds.get(buttonId);
    }

    private static void initButtonsByIdsMap() {
        Check.isMainThread();
        if (buttonsByIds.size() != 0) {
            return;
        }
        for (CppButton button : values()) {
            buttonsByIds.append(button.id, button);
        }
    }
}
