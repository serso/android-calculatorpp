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

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.squareup.otto.Bus;

import org.solovyev.android.Check;
import org.solovyev.android.calculator.buttons.CppSpecialButton;
import org.solovyev.android.calculator.math.MathType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;

@Singleton
public class Keyboard {

    @Nonnull
    private final MathType.Result mathType = new MathType.Result();

    @Inject
    Editor editor;
    @Inject
    Display display;
    @Inject
    Calculator calculator;
    @Inject
    Lazy<Clipboard> clipboard;
    @Inject
    Lazy<Bus> bus;

    @Inject
    public Keyboard() {
    }

    public boolean buttonPressed(@Nullable final String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        App.getGa().onButtonPressed(text);
        if (!processSpecialAction(text)) {
            processText(prepareText(text));
        }
        return true;
    }

    private void processText(@Nonnull String text) {
        int cursorPositionOffset = 0;
        final StringBuilder textToBeInserted = new StringBuilder(text);

        MathType.getType(text, 0, false, mathType);
        switch (mathType.type) {
            case function:
                textToBeInserted.append("()");
                cursorPositionOffset = -1;
                break;
            case operator:
                textToBeInserted.append("()");
                cursorPositionOffset = -1;
                break;
            case comma:
                textToBeInserted.append(" ");
                break;
        }

        if (cursorPositionOffset == 0) {
            if (MathType.groupSymbols.contains(text)) {
                cursorPositionOffset = -1;
            }
        }

        editor.insert(textToBeInserted.toString(), cursorPositionOffset);
    }

    @Nonnull
    private String prepareText(@Nonnull String text) {
        if ("(  )".equals(text) || "( )".equals(text)) {
            return "()";
        } else {
            return text;
        }
    }

    private boolean processSpecialAction(@Nonnull String action) {
        final CppSpecialButton button = CppSpecialButton.getByAction(action);
        if (button == null) {
            return false;
        }
        onSpecialButtonPressed(button);
        return true;
    }

    private void onSpecialButtonPressed(@NonNull CppSpecialButton button) {
        switch (button) {
            case history:
                calculator.fireCalculatorEvent(CalculatorEventType.show_history, null);
                break;
            case history_detached:
                calculator.fireCalculatorEvent(CalculatorEventType.show_history_detached, null);
                break;
            case cursor_right:
                moveCursorRight();
                break;
            case cursor_left:
                moveCursorLeft();
                break;
            case settings:
                calculator.fireCalculatorEvent(CalculatorEventType.show_settings, null);
                break;
            case settings_detached:
                calculator.fireCalculatorEvent(CalculatorEventType.show_settings_detached, null);
                break;
            case settings_widget:
                calculator.fireCalculatorEvent(CalculatorEventType.show_settings_widget, null);
                break;
            case like:
                calculator.fireCalculatorEvent(CalculatorEventType.show_like_dialog, null);
                break;
            case erase:
                editor.erase();
                break;
            case paste:
                pasteButtonPressed();
                break;
            case copy:
                copyButtonPressed();
                break;
            case equals:
                equalsButtonPressed();
                break;
            case clear:
                clearButtonPressed();
                break;
            case functions:
                calculator.fireCalculatorEvent(CalculatorEventType.show_functions, null);
                break;
            case functions_detached:
                calculator.fireCalculatorEvent(CalculatorEventType.show_functions_detached, null);
                break;
            case open_app:
                calculator.fireCalculatorEvent(CalculatorEventType.open_app, null);
                break;
            case vars:
                calculator.fireCalculatorEvent(CalculatorEventType.show_vars, null);
                break;
            case vars_detached:
                calculator.fireCalculatorEvent(CalculatorEventType.show_vars_detached, null);
                break;
            case operators:
                calculator.fireCalculatorEvent(CalculatorEventType.show_operators, null);
                break;
            case operators_detached:
                calculator.fireCalculatorEvent(CalculatorEventType.show_operators_detached, null);
                break;
            default:
                Check.shouldNotHappen();
        }
    }

    private void equalsButtonPressed() {
        if (!calculator.isCalculateOnFly()) {
            // no automatic calculations are => equals button must be used to calculate
            calculator.evaluate();
            return;
        }

        final DisplayState state = display.getState();
        if (!state.valid) {
            return;
        }
        editor.setText(state.text);
    }

    public void roundBracketsButtonPressed() {
        EditorState viewState = editor.getState();

        final int cursorPosition = viewState.selection;
        final CharSequence oldText = viewState.text;

        editor.setText("(" + oldText.subSequence(0, cursorPosition) + ")" + oldText.subSequence(cursorPosition, oldText.length()), cursorPosition + 2);
    }

    public void pasteButtonPressed() {
        final String text = clipboard.get().getText();
        if (!TextUtils.isEmpty(text)) {
            editor.insert(text);
        }
    }

    public void clearButtonPressed() {
        editor.clear();
    }

    public void copyButtonPressed() {
        bus.get().post(new Display.CopyOperation());
    }

    public void moveCursorLeft() {
        editor.moveCursorLeft();
    }

    public void moveCursorRight() {
        editor.moveCursorRight();
    }
}
