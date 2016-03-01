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
import android.text.TextUtils;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.solovyev.android.Check;
import org.solovyev.android.calculator.history.HistoryState;
import org.solovyev.android.calculator.history.RecentHistory;
import org.solovyev.android.calculator.text.TextProcessorEditorResult;
import org.solovyev.android.calculator.view.EditorTextProcessor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import static java.lang.Math.min;

@Singleton
public class Editor {

    @Nullable
    private final EditorTextProcessor textProcessor;
    @Nullable
    private EditorView view;
    @Nonnull
    private EditorState state = EditorState.empty();
    @Inject
    Bus bus;

    @Inject
    public Editor(@Nonnull SharedPreferences preferences, @Nonnull Engine engine) {
        textProcessor = new EditorTextProcessor(preferences, engine);
    }

    public void init() {
        bus.register(this);
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
        this.view.setEditor(this);
    }

    public void clearView(@Nonnull EditorView view) {
        Check.isMainThread();
        if (this.view == view) {
            this.view.setEditor(null);
            this.view = null;
        }
    }

    @Nonnull
    public EditorState getState() {
        return state;
    }

    @Nonnull
    public EditorState onTextChanged(@Nonnull EditorState newState) {
        return onTextChanged(newState, false);
    }

    @Nonnull
    public EditorState onTextChanged(@Nonnull EditorState newState, boolean force) {
        Check.isMainThread();
        if (textProcessor != null) {
            final TextProcessorEditorResult result = textProcessor.process(newState.getTextString());
            newState = EditorState.create(result.getCharSequence(), newState.selection + result.getOffset());
        }
        final EditorState oldState = state;
        state = newState;
        if (view != null) {
            view.setState(newState);
        }
        bus.post(new ChangedEvent(oldState, newState, force));
        return state;
    }

    @Nonnull
    private EditorState onSelectionChanged(@Nonnull EditorState newState) {
        Check.isMainThread();
        state = newState;
        if (view != null) {
            view.setState(newState);
        }
        bus.post(new CursorMovedEvent(newState));
        return state;
    }

    @Nonnull
    public EditorState setState(@Nonnull EditorState state) {
        Check.isMainThread();
        return onTextChanged(state);
    }

    @Nonnull
    private EditorState newSelectionViewState(int newSelection) {
        Check.isMainThread();
        if (state.selection == newSelection) {
            return state;
        }
        return onSelectionChanged(EditorState.forNewSelection(state, newSelection));
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
        if (state.selection <= 0) {
            return state;
        }
        return newSelectionViewState(state.selection - 1);
    }

    @Nonnull
    public EditorState moveCursorRight() {
        Check.isMainThread();
        if (state.selection >= state.text.length()) {
            return state;
        }
        return newSelectionViewState(state.selection + 1);
    }

    @Nonnull
    public EditorState erase() {
        Check.isMainThread();
        final int selection = state.selection;
        final String text = state.getTextString();
        if (selection <= 0 || text.length() <= 0 || selection > text.length()) {
            return state;
        }
        final String newText = text.substring(0, selection - 1) + text.substring(selection,
                text.length());
        return onTextChanged(EditorState.create(newText, selection - 1));
    }

    @Nonnull
    public EditorState clear() {
        Check.isMainThread();
        return setText("");
    }

    @Nonnull
    public EditorState setText(@Nonnull String text) {
        Check.isMainThread();
        return onTextChanged(EditorState.create(text, text.length()));
    }

    @Nonnull
    public EditorState setText(@Nonnull String text, int selection) {
        Check.isMainThread();
        return onTextChanged(EditorState.create(text, clamp(selection, text)));
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
        final int newTextLength = text.length() + oldText.length();
        final int newSelection = clamp(text.length() + selection + selectionOffset, newTextLength);
        final String newText = oldText.substring(0, selection) + text + oldText.substring(selection);
        return onTextChanged(EditorState.create(newText, newSelection));
    }

    @Nonnull
    public EditorState moveSelection(int offset) {
        Check.isMainThread();
        return setSelection(state.selection + offset);
    }

    @Nonnull
    public EditorState setSelection(int selection) {
        Check.isMainThread();
        if (state.selection == selection) {
            return state;
        }
        return onSelectionChanged(EditorState.forNewSelection(state, clamp(selection, state.text)));
    }

    @Subscribe
    public void onEngineChanged(@Nonnull Engine.ChangedEvent e) {
        // this will effectively apply new formatting (if f.e. grouping separator has changed) and
        // will start new evaluation
        onTextChanged(getState(), true);
    }

    public void onHistoryLoaded(@Nonnull RecentHistory history) {
        if (!state.isEmpty()) {
            return;
        }
        final HistoryState state = history.getCurrent();
        if (state == null) {
            return;
        }
        setState(state.editor);
    }

    public static class ChangedEvent {
        @Nonnull
        public final EditorState oldState;
        @Nonnull
        public final EditorState newState;
        public final boolean force;

        private ChangedEvent(@Nonnull EditorState oldState, @Nonnull EditorState newState, boolean force) {
            this.oldState = oldState;
            this.newState = newState;
            this.force = force;
        }

        boolean shouldEvaluate() {
            return force || !TextUtils.equals(newState.text, oldState.text);
        }
    }

    public static class CursorMovedEvent {
        @Nonnull
        public final EditorState state;

        public CursorMovedEvent(@Nonnull EditorState state) {
            this.state = state;
        }
    }
}
