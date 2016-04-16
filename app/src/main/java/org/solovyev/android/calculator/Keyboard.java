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
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.squareup.otto.Bus;
import dagger.Lazy;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.buttons.CppSpecialButton;
import org.solovyev.android.calculator.ga.Ga;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.memory.Memory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Keyboard implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Nonnull
    private final MathType.Result mathType = new MathType.Result();
    @Nonnull
    private static final String GLYPH_PASTE = "\uE000";
    @Nonnull
    private static final String GLYPH_COPY = "\uE001";

    @Inject
    Editor editor;
    @Inject
    Display display;
    @Inject
    Lazy<Memory> memory;
    @Inject
    Calculator calculator;
    @Inject
    Engine engine;
    @Inject
    Ga ga;
    @Inject
    Lazy<Clipboard> clipboard;
    @Inject
    Lazy<Bus> bus;
    @Inject
    ActivityLauncher launcher;
    private boolean vibrateOnKeypress;

    @Inject
    public Keyboard(@Nonnull SharedPreferences preferences) {
        preferences.registerOnSharedPreferenceChangeListener(this);
        vibrateOnKeypress = Preferences.Gui.vibrateOnKeypress.getPreference(preferences);
    }

    public boolean isVibrateOnKeypress() {
        return vibrateOnKeypress;
    }

    public boolean buttonPressed(@Nullable String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }

        if (text.equals(GLYPH_COPY)) {
            text = CppSpecialButton.copy.action;
        } else if (text.equals(GLYPH_PASTE)) {
            text = CppSpecialButton.paste.action;
        }

        ga.onButtonPressed(text);
        if (!processSpecialAction(text)) {
            processText(prepareText(text));
        }
        return true;
    }

    private void processText(@Nonnull String text) {
        int cursorPositionOffset = 0;
        final StringBuilder textToBeInserted = new StringBuilder(text);

        MathType.getType(text, 0, false, mathType, engine);
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
                launcher.showHistory();
                break;
            case cursor_right:
                editor.moveCursorRight();
                break;
            case cursor_to_end:
                editor.setCursorOnEnd();
                break;
            case cursor_left:
                editor.moveCursorLeft();
                break;
            case cursor_to_start:
                editor.setCursorOnStart();
                break;
            case settings:
                launcher.showSettings();
                break;
            case settings_widget:
                launcher.showWidgetSettings();
                break;
            case like:
                launcher.openFacebook();
                break;
            case memory:
                memory.get().requestValue();
                break;
            case erase:
                editor.erase();
                break;
            case paste:
                final String text = clipboard.get().getText();
                if (!TextUtils.isEmpty(text)) {
                    editor.insert(text);
                }
                break;
            case copy:
                bus.get().post(new Display.CopyOperation());
                break;
            case equals:
                equalsButtonPressed();
                break;
            case clear:
                editor.clear();
                break;
            case functions:
                launcher.showFunctions();
                break;
            case open_app:
                launcher.openApp();
                break;
            case vars:
                launcher.showVariables();
                break;
            case operators:
                launcher.showOperators();
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (Preferences.Gui.vibrateOnKeypress.isSameKey(key)) {
            vibrateOnKeypress = Preferences.Gui.vibrateOnKeypress.getPreference(preferences);
        }
    }
}
