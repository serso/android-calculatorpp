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

package org.solovyev.android.calculator.history;

import android.content.SharedPreferences;
import android.text.TextUtils;
import com.google.common.base.Strings;
import com.squareup.otto.Subscribe;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.*;
import org.solovyev.android.io.FileSaver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CalculatorHistory {

    @Nonnull
    private final HistoryList current = new HistoryList();
    @Nonnull
    private final List<HistoryState> saved = new ArrayList<>();
    @Nullable
    private EditorState lastEditorState;

    public CalculatorHistory() {
        App.getBus().register(this);
        App.getInitializer().execute(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
    }

    private static void migrateOldHistory() {
        final SharedPreferences preferences = App.getPreferences();
        final String xml = preferences.getString("org.solovyev.android.calculator.CalculatorModel_history", null);
        if (TextUtils.isEmpty(xml)) {
            return;
        }
        final OldHistory history = OldHistory.fromXml(xml);
        if (history == null) {
            // strange, history seems to be broken. Avoid clearing the preference
            return;
        }
        final List<HistoryState> states = new ArrayList<>();
        for (OldHistoryState state : history.getItems()) {
            final OldEditorHistoryState oldEditorState = state.getEditorState();
            final OldDisplayHistoryState oldDisplayState = state.getDisplayState();
            final String editorText = oldEditorState.getText();
            final EditorState editor = EditorState.create(Strings.nullToEmpty(editorText), oldEditorState.getCursorPosition());
            final DisplayState display = oldDisplayState.isValid()
                    ? DisplayState.createValid(oldDisplayState.getJsclOperation(), null, Strings.nullToEmpty(oldDisplayState.getEditorState().getText()), EditorState.NO_SEQUENCE)
                    : DisplayState.createError(oldDisplayState.getJsclOperation(), "", EditorState.NO_SEQUENCE);
            states.add(HistoryState.newBuilder(editor, display).build());
        }
    }

    private void init() {
        Check.isNotMainThread();
        migrateOldHistory();
    }

    public boolean isEmpty() {
        Check.isMainThread();
        return current.isEmpty();
    }

    public void addCurrentState(@Nonnull HistoryState state) {
        Check.isMainThread();
        current.add(state);
        Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.history_state_added, state);
        // todo serso: schedule save
    }

    public void addSavedState(@Nonnull HistoryState state) {
        Check.isMainThread();
        saved.add(state);
        // todo serso: schedule save
    }

    @Nonnull
    public List<HistoryState> getCurrentHistory() {
        Check.isMainThread();

        final List<HistoryState> result = new LinkedList<>();

        final List<HistoryState> states = current.asList();
        for (int i = 1; i < states.size(); i++) {
            final HistoryState newerState = states.get(i);
            final HistoryState olderState = states.get(i - 1);
            final String newerText = newerState.editor.getTextString();
            final String olderText = olderState.editor.getTextString();
            if (!isIntermediate(olderText, newerText)) {
                result.add(0, olderState);
            }
        }
        return result;
    }

    private boolean isIntermediate(@Nonnull String newerText,
                                   @Nonnull String olderText) {
        final int diff = newerText.length() - olderText.length();
        if (diff == 1) {
            return newerText.startsWith(olderText);
        } else if (diff == -1) {
            return olderText.startsWith(newerText);
        } else if (diff == 0) {
            return newerText.equals(olderText);
        }

        return false;
    }

    public void clear() {
        Check.isMainThread();
        current.clear();
    }

    public void undo() {
        final HistoryState state = current.undo();
        if (state == null) {
            return;
        }
        onCurrentStateChanged(state);
    }

    public void redo() {
        final HistoryState state = current.redo();
        if (state == null) {
            return;
        }
        onCurrentStateChanged(state);
    }

    private void onCurrentStateChanged(@Nonnull HistoryState state) {
        App.getEditor().setState(state.editor);
        App.getDisplay().setState(state.display);
    }

    @Nonnull
    public List<HistoryState> getSavedHistory() {
        return Collections.unmodifiableList(saved);
    }

    public void clearSavedHistory() {
        saved.clear();
    }

    public void removeSavedHistory(@Nonnull HistoryState state) {
        saved.remove(state);
    }

    @Subscribe
    public void onEditorChanged(@Nonnull Editor.ChangedEvent e) {
        lastEditorState = e.newState;
    }

    @Subscribe
    public void onDisplayChanged(@Nonnull Display.ChangedEvent e) {
        if (lastEditorState == null) {
            return;
        }
        if (lastEditorState.sequence != e.newState.getSequence()) {
            return;
        }
        addCurrentState(HistoryState.newBuilder(lastEditorState, e.newState).build());
        lastEditorState = null;
    }
}