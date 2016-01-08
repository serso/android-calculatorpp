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

import org.solovyev.android.Check;
import org.solovyev.android.calculator.history.HistoryState;
import org.solovyev.android.calculator.history.EditorHistoryState;
import org.solovyev.android.calculator.text.TextProcessor;
import org.solovyev.android.calculator.text.TextProcessorEditorResult;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.lang.Math.min;
import static org.solovyev.android.calculator.CalculatorEditorChangeEventData.newChangeEventData;
import static org.solovyev.android.calculator.CalculatorEventType.editor_state_changed;
import static org.solovyev.android.calculator.CalculatorEventType.editor_state_changed_light;

public class Editor implements CalculatorEventListener {

    private static final String TAG = App.subTag("Editor");

    @Nonnull
    private final Calculator calculator;
    @Nonnull
    private final CalculatorEventHolder lastEventHolder;
    @Nullable
    private final TextProcessor<TextProcessorEditorResult, String> textProcessor;
    @Nullable
    private EditorView view;
    @Nonnull
    private EditorState state = EditorState.empty();

    public Editor(@Nonnull Calculator calculator, @Nullable TextProcessor<TextProcessorEditorResult, String> textProcessor) {
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

    public void setView(@Nonnull EditorView view) {
        Check.isMainThread();
        this.view = view;
        this.view.setState(state);
    }

    public void clearView(@Nonnull EditorView view) {
        Check.isMainThread();
        if (this.view == view) {
            this.view = null;
        }
    }

    @Nonnull
    public EditorState getState() {
        return state;
    }

    public void setState(@Nonnull EditorState state) {
        setState(state, true);
    }

    private void setState(@Nonnull EditorState newState, boolean majorChanges) {
        Check.isMainThread();
        if (textProcessor != null) {
            try {
                final TextProcessorEditorResult result = textProcessor.process(newState.getText());
                newState = EditorState.create(result.getCharSequence(), newState.getSelection() + result.getOffset());
            } catch (CalculatorParseException e) {
                Locator.getInstance().getLogger().error(TAG, e.getMessage(), e);
            }
        }
        final EditorState oldState = state;
        state = newState;
        if (view != null) {
            view.setState(newState);
        }
        fireStateChangedEvent(majorChanges, oldState, newState);
    }

    private void fireStateChangedEvent(boolean majorChanges, @Nonnull EditorState oldViewState, @Nonnull EditorState newViewState) {
        Check.isMainThread();

        if (majorChanges) {
            calculator.fireCalculatorEvent(editor_state_changed, newChangeEventData(oldViewState, newViewState));
        } else {
            calculator.fireCalculatorEvent(editor_state_changed_light, newChangeEventData(oldViewState, newViewState));
        }
    }

    @Override
    public void onCalculatorEvent(@Nonnull CalculatorEventData evenData,
                                  @Nonnull CalculatorEventType eventType,
                                  @Nullable Object data) {
        Check.isMainThread();
        final CalculatorEventHolder.Result result = lastEventHolder.apply(evenData);

        if (result.isNewAfter()) {
            switch (eventType) {
                case use_history_state:
                    final HistoryState historyState = (HistoryState) data;
                    final EditorHistoryState editorState = historyState.getEditorState();
                    this.setText(Strings.getNotEmpty(editorState.getText(), ""), editorState.getCursorPosition());
                    break;
            }
        }
    }

    @Nonnull
    private EditorState newSelectionViewState(int newSelection) {
        Check.isMainThread();
        if (state.getSelection() != newSelection) {
            final EditorState result = EditorState.newSelection(state, newSelection);
            setState(result, false);
            return result;
        } else {
            return state;
        }
    }

    @Nonnull
    public EditorState setCursorOnStart() {
        Check.isMainThread();
        return newSelectionViewState(0);
    }

    @Nonnull
    public EditorState setCursorOnEnd() {
        Check.isMainThread();
        return newSelectionViewState(state.getText().length());
    }

    @Nonnull
    public EditorState moveCursorLeft() {
        Check.isMainThread();
        if (state.getSelection() > 0) {
            return newSelectionViewState(state.getSelection() - 1);
        } else {
            return state;
        }
    }

    @Nonnull
    public EditorState moveCursorRight() {
        Check.isMainThread();
        if (state.getSelection() < state.getText().length()) {
            return newSelectionViewState(state.getSelection() + 1);
        } else {
            return state;
        }
    }

    @Nonnull
    public EditorState erase() {
        Check.isMainThread();
        int selection = state.getSelection();
        final String text = state.getText();
        if (selection > 0 && text.length() > 0 && selection <= text.length()) {
            final StringBuilder newText = new StringBuilder(text.length() - 1);
            newText.append(text.substring(0, selection - 1)).append(text.substring(selection, text.length()));

            final EditorState result = EditorState.create(newText.toString(), selection - 1);
            setState(result);
            return result;
        } else {
            return state;
        }
    }

    @Nonnull
    public EditorState clear() {
        Check.isMainThread();
        return setText("");
    }

    @Nonnull
    public EditorState setText(@Nonnull String text) {
        Check.isMainThread();
        final EditorState result = EditorState.create(text, text.length());
        setState(result);
        return result;
    }

    @Nonnull
    public EditorState setText(@Nonnull String text, int selection) {
        Check.isMainThread();
        selection = clamp(selection, text);

        final EditorState result = EditorState.create(text, selection);
        setState(result);
        return result;
    }

    @Nonnull
    public EditorState insert(@Nonnull String text) {
        Check.isMainThread();
        return insert(text, 0);
    }

    @Nonnull
    public EditorState insert(@Nonnull String text, int selectionOffset) {
        Check.isMainThread();
        final String oldText = state.getText();
        final int selection = clamp(state.getSelection(), oldText);

        int newTextLength = text.length() + oldText.length();
        final StringBuilder newText = new StringBuilder(newTextLength);

        newText.append(oldText.substring(0, selection));
        newText.append(text);
        newText.append(oldText.substring(selection));

        int newSelection = clamp(text.length() + selection + selectionOffset, newTextLength);
        final EditorState result = EditorState.create(newText.toString(), newSelection);
        setState(result);
        return result;
    }

    @Nonnull
    public EditorState moveSelection(int offset) {
        Check.isMainThread();
        int selection = state.getSelection() + offset;
        return setSelection(selection);
    }

    @Nonnull
    public EditorState setSelection(int selection) {
        Check.isMainThread();
        selection = clamp(selection, state.getText());

        final EditorState result = EditorState.newSelection(state, selection);
        setState(result, false);
        return result;
    }

}
