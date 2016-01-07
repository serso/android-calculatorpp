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

import org.solovyev.android.calculator.history.CalculatorHistoryState;
import org.solovyev.android.calculator.history.EditorHistoryState;
import org.solovyev.android.calculator.text.TextProcessor;
import org.solovyev.android.calculator.text.TextProcessorEditorResult;
import org.solovyev.common.gui.CursorControl;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.lang.Math.min;
import static org.solovyev.android.calculator.CalculatorEditorChangeEventData.newChangeEventData;
import static org.solovyev.android.calculator.CalculatorEventType.editor_state_changed;
import static org.solovyev.android.calculator.CalculatorEventType.editor_state_changed_light;

/**
 * User: Solovyev_S
 * Date: 21.09.12
 * Time: 11:53
 */
public class CalculatorEditorImpl implements CalculatorEditor {

    @Nonnull
    private final Object viewLock = new Object();
    @Nonnull
    private final Calculator calculator;
    @Nonnull
    private final CalculatorEventHolder lastEventHolder;
    @Nonnull
    private final CursorControlAdapter cursorControlAdapter = new CursorControlAdapter(this);
    @Nullable
    private final TextProcessor<TextProcessorEditorResult, String> textProcessor;
    @Nullable
    private EditorView view;
    @Nonnull
    private EditorState lastViewState = EditorState.empty();

    public CalculatorEditorImpl(@Nonnull Calculator calculator, @Nullable TextProcessor<TextProcessorEditorResult, String> textProcessor) {
        this.calculator = calculator;
        this.textProcessor = textProcessor;
        this.calculator.addCalculatorEventListener(this);
        this.lastEventHolder = new CalculatorEventHolder(CalculatorUtils.createFirstEventDataId());
    }

    public static int clamp(int selection, @Nonnull CharSequence text) {
        return clamp(selection, text.length());
    }

    public static int clamp(int selection, int max) {
        return min(Math.max(selection, 0), max);
    }

    @Override
    public void setView(@Nonnull EditorView view) {
        synchronized (viewLock) {
            this.view = view;
            this.view.setState(lastViewState);
        }
    }

    @Override
    public void clearView(@Nonnull EditorView view) {
        synchronized (viewLock) {
            if (this.view == view) {
                this.view = null;
            }
        }
    }

    @Nonnull
    @Override
    public EditorState getViewState() {
        return lastViewState;
    }

    @Override
    public void setViewState(@Nonnull EditorState newViewState) {
        setViewState(newViewState, true);
    }

    @Override
    public void updateViewState() {
        setViewState(this.lastViewState, false);
    }

    private void setViewState(@Nonnull EditorState newViewState, boolean majorChanges) {
        if (textProcessor != null) {
            try {
                final TextProcessorEditorResult result = textProcessor.process(newViewState.getText());
                newViewState = EditorState.create(result.getCharSequence(), newViewState.getSelection() + result.getOffset());
            } catch (CalculatorParseException e) {
                Locator.getInstance().getLogger().error(TAG, e.getMessage(), e);
            }
        }
        synchronized (viewLock) {
            final EditorState oldViewState = this.lastViewState;

            this.lastViewState = newViewState;
            if (this.view != null) {
                this.view.setState(newViewState);
            }

            fireStateChangedEvent(majorChanges, oldViewState, newViewState);
        }
    }

	/*
    **********************************************************************
	*
	*                           SELECTION
	*
	**********************************************************************
	*/

    private void fireStateChangedEvent(boolean majorChanges, @Nonnull EditorState oldViewState, @Nonnull EditorState newViewState) {
        if (!Thread.holdsLock(viewLock)) throw new AssertionError();

        if (majorChanges) {
            calculator.fireCalculatorEvent(editor_state_changed, newChangeEventData(oldViewState, newViewState));
        } else {
            calculator.fireCalculatorEvent(editor_state_changed_light, newChangeEventData(oldViewState, newViewState));
        }
    }

    @Override
    public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData,
                                  @Nonnull CalculatorEventType calculatorEventType,
                                  @Nullable Object data) {
        final CalculatorEventHolder.Result result = lastEventHolder.apply(calculatorEventData);

        if (result.isNewAfter()) {
            switch (calculatorEventType) {
                case use_history_state:
                    final CalculatorHistoryState calculatorHistoryState = (CalculatorHistoryState) data;
                    final EditorHistoryState editorState = calculatorHistoryState.getEditorState();
                    this.setText(Strings.getNotEmpty(editorState.getText(), ""), editorState.getCursorPosition());
                    break;
            }
        }
    }

    @Nonnull
    private EditorState newSelectionViewState(int newSelection) {
        if (this.lastViewState.getSelection() != newSelection) {
            final EditorState result = EditorState.newSelection(this.lastViewState, newSelection);
            setViewState(result, false);
            return result;
        } else {
            return this.lastViewState;
        }
    }

    @Nonnull
    public EditorState setCursorOnStart() {
        synchronized (viewLock) {
            return newSelectionViewState(0);
        }
    }

    @Nonnull
    public EditorState setCursorOnEnd() {
        synchronized (viewLock) {
            return newSelectionViewState(this.lastViewState.getText().length());
        }
    }

    @Nonnull
    public EditorState moveCursorLeft() {
        synchronized (viewLock) {
            if (this.lastViewState.getSelection() > 0) {
                return newSelectionViewState(this.lastViewState.getSelection() - 1);
            } else {
                return this.lastViewState;
            }
        }
    }

	/*
	**********************************************************************
	*
	*                           EDITOR ACTIONS
	*
	**********************************************************************
	*/

    @Nonnull
    public EditorState moveCursorRight() {
        synchronized (viewLock) {
            if (this.lastViewState.getSelection() < this.lastViewState.getText().length()) {
                return newSelectionViewState(this.lastViewState.getSelection() + 1);
            } else {
                return this.lastViewState;
            }
        }
    }

    @Nonnull
    @Override
    public CursorControl asCursorControl() {
        return cursorControlAdapter;
    }

    @Nonnull
    @Override
    public EditorState erase() {
        synchronized (viewLock) {
            int selection = this.lastViewState.getSelection();
            final String text = this.lastViewState.getText();
            if (selection > 0 && text.length() > 0 && selection <= text.length()) {
                final StringBuilder newText = new StringBuilder(text.length() - 1);
                newText.append(text.substring(0, selection - 1)).append(text.substring(selection, text.length()));

                final EditorState result = EditorState.create(newText.toString(), selection - 1);
                setViewState(result);
                return result;
            } else {
                return this.lastViewState;
            }
        }
    }

    @Nonnull
    @Override
    public EditorState clear() {
        synchronized (viewLock) {
            return setText("");
        }
    }

    @Nonnull
    @Override
    public EditorState setText(@Nonnull String text) {
        synchronized (viewLock) {
            final EditorState result = EditorState.create(text, text.length());
            setViewState(result);
            return result;
        }
    }

    @Nonnull
    @Override
    public EditorState setText(@Nonnull String text, int selection) {
        synchronized (viewLock) {
            selection = clamp(selection, text);

            final EditorState result = EditorState.create(text, selection);
            setViewState(result);
            return result;
        }
    }

    @Nonnull
    @Override
    public EditorState insert(@Nonnull String text) {
        synchronized (viewLock) {
            return insert(text, 0);
        }
    }

    @Nonnull
    @Override
    public EditorState insert(@Nonnull String text, int selectionOffset) {
        synchronized (viewLock) {
            final String oldText = lastViewState.getText();
            final int selection = clamp(lastViewState.getSelection(), oldText);

            int newTextLength = text.length() + oldText.length();
            final StringBuilder newText = new StringBuilder(newTextLength);

            newText.append(oldText.substring(0, selection));
            newText.append(text);
            newText.append(oldText.substring(selection));

            int newSelection = clamp(text.length() + selection + selectionOffset, newTextLength);
            final EditorState result = EditorState.create(newText.toString(), newSelection);
            setViewState(result);
            return result;
        }
    }

    @Nonnull
    @Override
    public EditorState moveSelection(int offset) {
        synchronized (viewLock) {
            int selection = this.lastViewState.getSelection() + offset;

            return setSelection(selection);
        }
    }

    @Nonnull
    @Override
    public EditorState setSelection(int selection) {
        synchronized (viewLock) {
            selection = clamp(selection, this.lastViewState.getText());

            final EditorState result = EditorState.newSelection(this.lastViewState, selection);
            setViewState(result, false);
            return result;
        }
    }

    private static final class CursorControlAdapter implements CursorControl {

        @Nonnull
        private final CalculatorEditor calculatorEditor;

        private CursorControlAdapter(@Nonnull CalculatorEditor calculatorEditor) {
            this.calculatorEditor = calculatorEditor;
        }

        @Override
        public void setCursorOnStart() {
            this.calculatorEditor.setCursorOnStart();
        }

        @Override
        public void setCursorOnEnd() {
            this.calculatorEditor.setCursorOnEnd();
        }

        @Override
        public void moveCursorLeft() {
            this.calculatorEditor.moveCursorLeft();
        }

        @Override
        public void moveCursorRight() {
            this.calculatorEditor.moveCursorRight();
        }
    }
}
