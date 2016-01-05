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
    private CalculatorEditorView view;
    @Nonnull
    private CalculatorEditorViewState lastViewState = CalculatorEditorViewStateImpl.newDefaultInstance();

    public CalculatorEditorImpl(@Nonnull Calculator calculator, @Nullable TextProcessor<TextProcessorEditorResult, String> textProcessor) {
        this.calculator = calculator;
        this.textProcessor = textProcessor;
        this.calculator.addCalculatorEventListener(this);
        this.lastEventHolder = new CalculatorEventHolder(CalculatorUtils.createFirstEventDataId());
    }

    public static int correctSelection(int selection, @Nonnull CharSequence text) {
        return correctSelection(selection, text.length());
    }

    public static int correctSelection(int selection, int textLength) {
        int result = Math.max(selection, 0);
        result = min(result, textLength);
        return result;
    }

    @Override
    public void setView(@Nonnull CalculatorEditorView view) {
        synchronized (viewLock) {
            this.view = view;
            this.view.setState(lastViewState);
        }
    }

    @Override
    public void clearView(@Nonnull CalculatorEditorView view) {
        synchronized (viewLock) {
            if (this.view == view) {
                this.view = null;
            }
        }
    }

    @Nonnull
    @Override
    public CalculatorEditorViewState getViewState() {
        return lastViewState;
    }

    @Override
    public void setViewState(@Nonnull CalculatorEditorViewState newViewState) {
        setViewState(newViewState, true);
    }

    @Override
    public void updateViewState() {
        setViewState(this.lastViewState, false);
    }

    private void setViewState(@Nonnull CalculatorEditorViewState newViewState, boolean majorChanges) {
        if (textProcessor != null) {
            try {
                final TextProcessorEditorResult result = textProcessor.process(newViewState.getText());
                newViewState = CalculatorEditorViewStateImpl.newInstance(result.getCharSequence(), newViewState.getSelection() + result.getOffset());
            } catch (CalculatorParseException e) {
                Locator.getInstance().getLogger().error(TAG, e.getMessage(), e);
            }
        }
        synchronized (viewLock) {
            final CalculatorEditorViewState oldViewState = this.lastViewState;

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

    private void fireStateChangedEvent(boolean majorChanges, @Nonnull CalculatorEditorViewState oldViewState, @Nonnull CalculatorEditorViewState newViewState) {
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
    private CalculatorEditorViewState newSelectionViewState(int newSelection) {
        if (this.lastViewState.getSelection() != newSelection) {
            final CalculatorEditorViewState result = CalculatorEditorViewStateImpl.newSelection(this.lastViewState, newSelection);
            setViewState(result, false);
            return result;
        } else {
            return this.lastViewState;
        }
    }

    @Nonnull
    public CalculatorEditorViewState setCursorOnStart() {
        synchronized (viewLock) {
            return newSelectionViewState(0);
        }
    }

    @Nonnull
    public CalculatorEditorViewState setCursorOnEnd() {
        synchronized (viewLock) {
            return newSelectionViewState(this.lastViewState.getText().length());
        }
    }

    @Nonnull
    public CalculatorEditorViewState moveCursorLeft() {
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
    public CalculatorEditorViewState moveCursorRight() {
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
    public CalculatorEditorViewState erase() {
        synchronized (viewLock) {
            int selection = this.lastViewState.getSelection();
            final String text = this.lastViewState.getText();
            if (selection > 0 && text.length() > 0 && selection <= text.length()) {
                final StringBuilder newText = new StringBuilder(text.length() - 1);
                newText.append(text.substring(0, selection - 1)).append(text.substring(selection, text.length()));

                final CalculatorEditorViewState result = CalculatorEditorViewStateImpl.newInstance(newText.toString(), selection - 1);
                setViewState(result);
                return result;
            } else {
                return this.lastViewState;
            }
        }
    }

    @Nonnull
    @Override
    public CalculatorEditorViewState clear() {
        synchronized (viewLock) {
            return setText("");
        }
    }

    @Nonnull
    @Override
    public CalculatorEditorViewState setText(@Nonnull String text) {
        synchronized (viewLock) {
            final CalculatorEditorViewState result = CalculatorEditorViewStateImpl.newInstance(text, text.length());
            setViewState(result);
            return result;
        }
    }

    @Nonnull
    @Override
    public CalculatorEditorViewState setText(@Nonnull String text, int selection) {
        synchronized (viewLock) {
            selection = correctSelection(selection, text);

            final CalculatorEditorViewState result = CalculatorEditorViewStateImpl.newInstance(text, selection);
            setViewState(result);
            return result;
        }
    }

    @Nonnull
    @Override
    public CalculatorEditorViewState insert(@Nonnull String text) {
        synchronized (viewLock) {
            return insert(text, 0);
        }
    }

    @Nonnull
    @Override
    public CalculatorEditorViewState insert(@Nonnull String text, int selectionOffset) {
        synchronized (viewLock) {
            final String oldText = lastViewState.getText();
            final int selection = correctSelection(lastViewState.getSelection(), oldText);

            int newTextLength = text.length() + oldText.length();
            final StringBuilder newText = new StringBuilder(newTextLength);

            newText.append(oldText.substring(0, selection));
            newText.append(text);
            newText.append(oldText.substring(selection));

            int newSelection = correctSelection(text.length() + selection + selectionOffset, newTextLength);
            final CalculatorEditorViewState result = CalculatorEditorViewStateImpl.newInstance(newText.toString(), newSelection);
            setViewState(result);
            return result;
        }
    }

    @Nonnull
    @Override
    public CalculatorEditorViewState moveSelection(int offset) {
        synchronized (viewLock) {
            int selection = this.lastViewState.getSelection() + offset;

            return setSelection(selection);
        }
    }

    @Nonnull
    @Override
    public CalculatorEditorViewState setSelection(int selection) {
        synchronized (viewLock) {
            selection = correctSelection(selection, this.lastViewState.getText());

            final CalculatorEditorViewState result = CalculatorEditorViewStateImpl.newSelection(this.lastViewState, selection);
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
