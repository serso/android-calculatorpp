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
import org.solovyev.android.calculator.history.EditorHistoryState;
import org.solovyev.android.calculator.history.HistoryState;
import org.solovyev.android.calculator.text.TextProcessor;
import org.solovyev.android.calculator.text.TextProcessorEditorResult;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.lang.Math.min;

public class Editor implements CalculatorEventListener {

    private static final String TAG = App.subTag("Editor");

    public static class ChangedEvent {
        @Nonnull
        public final EditorState oldState;
        @Nonnull
        public final EditorState newState;

        private ChangedEvent(@Nonnull EditorState oldState, @Nonnull EditorState newState) {
            this.oldState = oldState;
            this.newState = newState;
        }
    }

    public static class CursorMovedEvent {
        @Nonnull
        public final EditorState state;

        public CursorMovedEvent(@Nonnull EditorState state) {
            this.state = state;
        }
    }

    @Nonnull
    private final CalculatorEventHolder lastEventHolder;
    @Nullable
    private final TextProcessor<TextProcessorEditorResult, String> textProcessor;
    @Nullable
    private EditorView view;
    @Nonnull
    private EditorState state = EditorState.empty();

    public Editor(@Nonnull Calculator calculator, @Nullable TextProcessor<TextProcessorEditorResult, String> textProcessor) {
        this.textProcessor = textProcessor;
        calculator.addCalculatorEventListener(this);
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

    public void onTextChanged(@Nonnull EditorState newState) {
        Check.isMainThread();
        if (textProcessor != null) {
            try {
                final TextProcessorEditorResult result = textProcessor.process(newState.getTextString());
                newState = EditorState.create(result.getCharSequence(), newState.selection + result.getOffset());
            } catch (CalculatorParseException e) {
                Locator.getInstance().getLogger().error(TAG, e.getMessage(), e);
            }
        }
        final EditorState oldState = state;
        state = newState;
        if (view != null) {
            view.setState(newState);
        }
        App.getBus().post(new ChangedEvent(oldState, newState));
    }

    private void onSelectionChanged(@Nonnull EditorState newState) {
        Check.isMainThread();
        state = newState;
        if (view != null) {
            view.setState(newState);
        }
        App.getBus().post(new CursorMovedEvent(newState));
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
        if (state.selection != newSelection) {
            final EditorState newState = EditorState.forNewSelection(state, newSelection);
            onSelectionChanged(newState);
            return newState;
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
        return newSelectionViewState(state.text.length());
    }

    @Nonnull
    public EditorState moveCursorLeft() {
        Check.isMainThread();
        if (state.selection > 0) {
            return newSelectionViewState(state.selection - 1);
        } else {
            return state;
        }
    }

    @Nonnull
    public EditorState moveCursorRight() {
        Check.isMainThread();
        if (state.selection < state.text.length()) {
            return newSelectionViewState(state.selection + 1);
        } else {
            return state;
        }
    }

    @Nonnull
    public EditorState erase() {
        Check.isMainThread();
        int selection = state.selection;
        final String text = state.getTextString();
        if (selection > 0 && text.length() > 0 && selection <= text.length()) {
            final EditorState newState = EditorState.create(text.substring(0, selection - 1) + text.substring(selection, text.length()), selection - 1);
            onTextChanged(newState);
            return newState;
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
        onTextChanged(result);
        return result;
    }

    @Nonnull
    public EditorState setText(@Nonnull String text, int selection) {
        Check.isMainThread();
        final EditorState state = EditorState.create(text, clamp(selection, text));
        onTextChanged(state);
        return state;
    }

    @Nonnull
    public EditorState insert(@Nonnull String text) {
        Check.isMainThread();
        return insert(text, 0);
    }

    @Nonnull
    public EditorState insert(@Nonnull String text, int selectionOffset) {
        Check.isMainThread();
        final String oldText = state.getTextString();
        final int selection = clamp(state.selection, oldText);

        int newTextLength = text.length() + oldText.length();

        int newSelection = clamp(text.length() + selection + selectionOffset, newTextLength);
        final EditorState newState = EditorState.create(oldText.substring(0, selection) + text + oldText.substring(selection), newSelection);
        onTextChanged(newState);
        return newState;
    }

    @Nonnull
    public EditorState moveSelection(int offset) {
        Check.isMainThread();
        int selection = state.selection + offset;
        return setSelection(selection);
    }

    @Nonnull
    public EditorState setSelection(int selection) {
        Check.isMainThread();
        selection = clamp(selection, state.text);

        final EditorState result = EditorState.forNewSelection(state, selection);
        onSelectionChanged(result);
        return result;
    }

}
