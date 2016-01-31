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

import android.text.TextUtils;
import com.squareup.otto.Bus;
import dagger.Lazy;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Keyboard {

    @Nonnull
    private final MathType.Result mathType = new MathType.Result();

    @Inject
    Editor editor;
    @Inject
    Lazy<Clipboard> clipboard;
    @Inject
    Lazy<Bus> bus;

    @Inject
    public Keyboard() {
    }

    public boolean buttonPressed(@Nullable final String text) {
        App.getGa().onButtonPressed(text);
        if (!Strings.isEmpty(text)) {
            // process special buttons
            boolean processed = processSpecialButtons(text);

            if (!processed) {
                processText(prepareText(text));
            }
            return true;
        }
        return false;
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

    private boolean processSpecialButtons(@Nonnull String text) {
        boolean result = false;

        final CalculatorSpecialButton button = CalculatorSpecialButton.getByActionCode(text);
        if (button != null) {
            button.onClick(this);
            result = true;
        }

        return result;
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
