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

import static java.lang.Math.min;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.solovyev.android.Check;
import org.solovyev.android.calculator.history.HistoryState;
import org.solovyev.android.calculator.history.RecentHistory;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.memory.Memory;
import org.solovyev.android.calculator.text.TextProcessorEditorResult;
import org.solovyev.android.calculator.view.EditorTextProcessor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Editor {

    private class AsyncHighlighter extends AsyncTask<Void, Void, EditorState> {
        @NonNull
        private final EditorState oldState;
        @Nonnull
        private final EditorState newState;
        private final boolean force;
        @Nullable
        private final EditorTextProcessor processor;
        @Nonnull
        private final String text;

        AsyncHighlighter(@NonNull EditorState oldState, @Nonnull EditorState newState, boolean force,
                @Nullable EditorTextProcessor processor) {
            this.oldState = oldState;
            this.newState = newState;
            this.text = newState.getTextString();
            this.force = force;
            this.processor = processor;
        }

        boolean shouldAsync() {
            return !TextUtils.isEmpty(text) && processor != null;
        }

        @Nonnull
        @Override
        protected EditorState doInBackground(Void... params) {
            if (processor == null) {
                return newState;
            }
            final TextProcessorEditorResult res = processor.process(newState.getTextString());
            return EditorState.create(res.getCharSequence(), newState.selection + res.getOffset());
        }

        @Override
        protected void onPostExecute(@Nonnull EditorState state) {
            if (highlighterTask != this) {
                return;
            }
            Editor.this.state = state;
            if (view != null) {
                view.setState(state);
            }
            bus.post(new ChangedEvent(oldState, state, force));
            highlighterTask = null;
        }
    }

    /**
     * Splits selected text into parts left, mid (selected) and right of selection.
     */
    private class SplitText {
        public int selectionStart = 0;
        public int selectionEnd = 0;
        public int selectionLength = 0;
        public int insertionPos = 0;
        public boolean textSelected = false;
        public String textLeft = "";
        public String textMid = "";
        public String textRight = "";
        public String text = "";

        /**
         * SplitText constructor.
         *
         * @param text the content of the calculator's text input field.
         * @param cursorPos the current position of the text cursor in the input field.
         */
        public SplitText(String text, int cursorPos) {
            this.text = text;
            selectionStart = view.getSelectionStart();
            selectionEnd = view.getSelectionEnd();
            selectionLength = selectionEnd - selectionStart;
            textSelected = selectionLength != 0;
            insertionPos = textSelected ?
                      clamp(selectionStart, text)
                    : clamp(cursorPos, text);
            textLeft = text.substring(0, insertionPos);
            textMid = text.substring(selectionStart, selectionEnd);
            textRight = text.substring(insertionPos + selectionLength, text.length());
        }

        /**
         * Retrieves the middle part (=selection) of the split text. 
         *
         * @param deleteSelection specifies whether the selection is to be deleted.
         * @return the selected text or an empty string depending on deletion context.
         */
        public String getTextMid(boolean deleteSelection) {
            return deleteSelection ? "" : this.textMid;
        }

        /**
         * Retrieves the text left of the selection/cursor pos when deleting.
         *
         * <p>The left part of the text upon deletion depends on whether text
         * is selected or not. For selected text, the left part is simply from
         * the beginning of the text input to the beginning of the selection.
         * <p>For no text selected, the deletion is a one-character deletion,
         * so returns the string from the beginning of the text input to one
         * character left of the cursor position.
         * <p>A special case is given when the cursor position is to the right of  
         * a decimal grouping separator (e.g. a whitespace) in which case the
         * grouping separator plus the digit left to it has to be deleted
         * (because deleting only the grouping separator would restore it
         * immediately after deletion). Thus, the string from the beginning
         * to the original cursor position up until two characters left of it
         * is returned.
         * @return the left part of the text after a deletion operation.
         */
        public String getDelTextLeft() {
            MathType type = MathType.getType(text, insertionPos - 1, false, engine).type;
            return this.textSelected ?
                   this.textLeft
                 : type == MathType.grouping_separator ?
                   this.textLeft.substring(0, Math.max(this.textLeft.length()-2, 0))
                 : this.textLeft.substring(0, Math.max(this.textLeft.length()-1, 0));
        }

        /**
         * Returns the cursor position after a deletion.
         */
        public int getDelPos() {
            return this.getDelTextLeft().length();
        }
    }


    @VisibleForTesting
    @Nullable
    EditorTextProcessor textProcessor;
    @Nonnull
    private final Engine engine;
    @Nullable
    private AsyncHighlighter highlighterTask;
    @Nullable
    private EditorView view;
    @Nonnull
    private EditorState state = EditorState.empty();
    @Inject
    Bus bus;

    @Inject
    public Editor(@Nonnull Application application, @Nonnull SharedPreferences preferences, @Nonnull Engine engine) {
        this.engine = engine;
        this.textProcessor = new EditorTextProcessor(application, preferences, engine);
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

    private void onTextChanged(@Nonnull EditorState newState) {
        onTextChanged(newState, false);
    }

    private void onTextChanged(@Nonnull EditorState newState, boolean force) {
        Check.isMainThread();
        asyncHighlightText(newState, force);
    }

    private void cancelAsyncHighlightText() {
        if (highlighterTask == null) {
            return;
        }
        highlighterTask.cancel(false);
        highlighterTask = null;
    }

    private void asyncHighlightText(@NonNull EditorState newState, boolean force) {
        // synchronous operation should continue working regardless of the highlighter
        final EditorState oldState = state;
        state = newState;

        cancelAsyncHighlightText();
        AsyncHighlighter newHighlighterTask = new AsyncHighlighter(oldState, newState, force, textProcessor);
        highlighterTask = newHighlighterTask;
        if (newHighlighterTask.shouldAsync()) {
            newHighlighterTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            return;
        }
        newHighlighterTask.onPostExecute(newState);
        Check.isNull(highlighterTask);
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

    public void setState(@Nonnull EditorState state) {
        Check.isMainThread();
        onTextChanged(state);
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

    /**
     * Erases text in the input field upon pressing of the backspace button.
     *
     * @return whether the content of the text input is empty befor or after deletion.
     */
    public boolean erase() {
        Check.isMainThread();
        final int delPos = state.selection;
        final String text = state.getTextString();
        final SplitText st = new SplitText(text, delPos);

        if (delPos <= 0 || text.length() <= 0 || delPos > text.length()) {
            return false;
        }

        // For an erase operation with text selected that text will be deleted (mid part empty).
        final String newText = st.getDelTextLeft() + st.getTextMid(true) + st.textRight;
        onTextChanged(EditorState.create(newText, st.getDelPos()));

        return !newText.isEmpty();
    }

    public void clear() {
        Check.isMainThread();
        setText("");
    }

    public void setText(@Nonnull String text) {
        Check.isMainThread();
        final int cursorPos = view.getSelectionEnd();
        onTextChanged(EditorState.create(text, cursorPos));
    }

    public void setText(@Nonnull String text, int selection) {
        Check.isMainThread();
        onTextChanged(EditorState.create(text, clamp(selection, text)));
    }

    public void insert(@Nonnull String text) {
        Check.isMainThread();
        insert(text, 0);
    }

    /**
     * Inserts new content into the text input field.
     *
     * <p>This might be anything inserted via some function of the calculator
     * input keys, e.g. a simple digit, parentheses, some function, something 
     * pasted from the clipboard etc.
     *
     * @param textToInsert the text to put into the input field at a certain position.
     * @param cursorOffset an integer specifying whether to move the cursor after insertion.
     */
    public void insert(@Nonnull String textToInsert, int cursorOffset) {
        Check.isMainThread();
        if (TextUtils.isEmpty(textToInsert) && cursorOffset == 0) {
            return;
        }
        final String oldText = state.getTextString();
        final MathType type = MathType.getType(textToInsert, 0, false, engine).type;
        final SplitText st = new SplitText(oldText, state.selection);

        boolean deleteSelection = false;
        if (st.textSelected && type == MathType.digit) {
            deleteSelection = true;
        }

        if (st.textSelected && type == MathType.binary_operation) {
            // Add parentheses to the left of the text to be inserted and prepare to move
            // the cursor to inside of the parentheses, e.g. when pressing "^2", "+" etc.
            // with text selected. At that position the _selected_ text will be inserted.
            textToInsert = "()" + textToInsert;
            cursorOffset = -textToInsert.length() + 1;
        }

        final int insertedTextLength = textToInsert.length();
        // pluginPos is the position at which to plug in selected text in the string to be
        // inserted, i.e. a "local" position (in contrast to a "global" cursor position in
        // the text input field).
        final int pluginPos = insertedTextLength + cursorOffset;
        // For pluginPos == insertedTextLength the inserted text is split into a left
        // and a right part.
        final String insertLeft = textToInsert.substring(0, pluginPos);
        final String insertRight = textToInsert.substring(pluginPos, insertedTextLength);

        final String textMid = st.getTextMid(deleteSelection);
        // New content of text input field (with example strings in comments, assuming the
        // text input field to contain "5*6+7*8" with "6+7" selected and "^2" pressed).
        final String newText = st.textLeft // "5*"
                             + insertLeft // "("
                             + textMid // "6+7"
                             + insertRight // ")^2"
                             + st.textRight; // "*8" => "5*(6+7)*8"

        // Example cursor position: after the parentheses and the operator, i.e. at: "5*(6+7)^2|*8".
        int newCursorPos = st.textLeft.length() + insertLeft.length() + textMid.length();
        if (st.textSelected) newCursorPos += insertRight.length();
        newCursorPos = clamp(newCursorPos, newText);

        onTextChanged(EditorState.create(newText, newCursorPos));
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

    @Subscribe
    public void onMemoryValueReady(@Nonnull Memory.ValueReadyEvent e) {
        insert(e.value);
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
