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
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.common.base.Strings;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.CalculatorEventType;
import org.solovyev.android.calculator.Display;
import org.solovyev.android.calculator.DisplayState;
import org.solovyev.android.calculator.Editor;
import org.solovyev.android.calculator.EditorState;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;
import org.solovyev.android.io.FileLoader;
import org.solovyev.android.io.FileSaver;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.lang.Character.isDigit;

public class History {

    public static final String TAG = App.subTag("History");
    @NonNull
    private final Runnable writeCurrent = new WriteTask(true);
    @NonNull
    private final Runnable writeSaved = new WriteTask(false);
    @Nonnull
    private final HistoryList current = new HistoryList();
    @Nonnull
    private final List<HistoryState> saved = new ArrayList<>();
    @Nonnull
    private final Handler handler = App.getHandler();
    @Nullable
    private EditorState lastEditorState;
    private boolean initialized;

    public History() {
        App.getBus().register(this);
        App.getInitThread().execute(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
    }

    private static void migrateOldHistory() {
        try {
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
            final JSONArray json = HistoryList.toJson(states);
            FileSaver.save(getSavedHistoryFile(), json.toString());
        } catch (Exception e) {
            Locator.getInstance().getLogger().error(TAG, e.getMessage(), e);
        }
    }

    @NonNull
    private static File getSavedHistoryFile() {
        return new File(App.getApplication().getFilesDir(), "history-saved.json");
    }

    @NonNull
    private static File getCurrentHistoryFile() {
        return new File(App.getApplication().getFilesDir(), "history-current.json");
    }

    @Nonnull
    private static List<HistoryState> loadStates(@Nonnull File file) {
        if (!file.exists()) {
            return Collections.emptyList();
        }
        final CharSequence json = FileLoader.load(file);
        if (TextUtils.isEmpty(json)) {
            return Collections.emptyList();
        }
        try {
            return HistoryList.fromJson(new JSONArray(json.toString()));
        } catch (JSONException e) {
            Locator.getInstance().getLogger().error(TAG, e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    private void init() {
        Check.isNotMainThread();
        migrateOldHistory();
        final List<HistoryState> currentStates = loadStates(getCurrentHistoryFile());
        final List<HistoryState> savedStates = loadStates(getSavedHistoryFile());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Check.isTrue(current.isEmpty());
                Check.isTrue(saved.isEmpty());
                current.addAll(currentStates);
                saved.addAll(savedStates);
                initialized = true;
            }
        });
    }

    public void addCurrent(@Nonnull HistoryState state) {
        Check.isMainThread();
        current.add(state);
        Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.history_state_added, state);
        onCurrentChanged();
    }

    public void addSaved(@Nonnull HistoryState state) {
        Check.isMainThread();
        saved.add(state);
        onSavedChanged();
    }

    private void onCurrentChanged() {
        handler.removeCallbacks(writeCurrent);
        handler.postDelayed(writeCurrent, 500);
    }

    private void onSavedChanged() {
        handler.removeCallbacks(writeSaved);
        handler.postDelayed(writeSaved, 500);
    }

    @Nonnull
    public List<HistoryState> getCurrent() {
        Check.isMainThread();

        final List<HistoryState> result = new LinkedList<>();

        final String groupingSeparator = AndroidCalculatorEngine.Preferences.groupingSeparator.getPreference(App.getPreferences());

        final List<HistoryState> states = current.asList();
        final int statesCount = states.size();
        for (int i = 1; i < statesCount; i++) {
            final HistoryState olderState = states.get(i - 1);
            final HistoryState newerState = states.get(i);
            final String olderText = olderState.editor.getTextString();
            final String newerText = newerState.editor.getTextString();
            if (!isIntermediate(olderText, newerText, groupingSeparator)) {
                result.add(0, olderState);
            }
        }
        if (statesCount > 0) {
            final HistoryState state = states.get(statesCount - 1);
            if (!TextUtils.isEmpty(state.editor.getTextString())) {
                result.add(0, state);
            }
        }
        return result;
    }

    @Nonnull
    public List<HistoryState> getSaved() {
        Check.isMainThread();
        return new ArrayList<>(saved);
    }

    private static boolean isIntermediate(@Nonnull String olderText,
                                          @Nonnull String newerText,
                                          @NonNull String groupingSeparator) {
        if (TextUtils.isEmpty(olderText)) {
            return true;
        }
        if (TextUtils.isEmpty(newerText)) {
            return false;
        }
        olderText = trimGroupingSeparators(olderText, groupingSeparator);
        newerText = trimGroupingSeparators(newerText, groupingSeparator);

        final int diff = newerText.length() - olderText.length();
        if (diff >= 1) {
            return newerText.startsWith(olderText);
        } else if (diff <= 1) {
            return olderText.startsWith(newerText);
        } else if (diff == 0) {
            return olderText.equals(newerText);
        }

        return false;
    }

    @NonNull
    private static String trimGroupingSeparators(@NonNull String text, @NonNull String groupingSeparator) {
        if (TextUtils.isEmpty(groupingSeparator)) {
            return text;
        }
        Check.isTrue(groupingSeparator.length() == 1);
        final StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            if (i == 0 || i == text.length() - 1) {
                // grouping separator can't be the first and the last character
                sb.append(text.charAt(i));
                continue;
            }
            if (isDigit(text.charAt(i - 1)) && text.charAt(i) == groupingSeparator.charAt(0) && isDigit(text.charAt(i + 1))) {
                // grouping separator => skip
                continue;
            }
            sb.append(text.charAt(i));

        }
        return sb.toString();
    }

    public void clearCurrent() {
        Check.isMainThread();
        current.clear();
        onCurrentChanged();
    }

    public void clearSaved() {
        Check.isMainThread();
        saved.clear();
        onSavedChanged();
    }

    public void undo() {
        final HistoryState state = current.undo();
        if (state == null) {
            return;
        }
        applyHistoryState(state);
    }

    public void redo() {
        final HistoryState state = current.redo();
        if (state == null) {
            return;
        }
        applyHistoryState(state);
    }

    private void applyHistoryState(@Nonnull HistoryState state) {
        App.getEditor().setState(state.editor);
        App.getDisplay().setState(state.display);
    }

    public void removeSaved(@Nonnull HistoryState state) {
        Check.isMainThread();
        saved.remove(state);
        onSavedChanged();
    }

    public void removeCurrent(@Nonnull HistoryState state) {
        Check.isMainThread();
        current.remove(state);
        onCurrentChanged();
    }

    @Subscribe
    public void onEditorChanged(@Nonnull Editor.ChangedEvent e) {
        if (!initialized) {
            return;
        }
        lastEditorState = e.newState;
    }

    @Subscribe
    public void onDisplayChanged(@Nonnull Display.ChangedEvent e) {
        if (!initialized) {
            return;
        }
        if (lastEditorState == null) {
            return;
        }
        if (lastEditorState.sequence != e.newState.sequence) {
            return;
        }
        addCurrent(HistoryState.newBuilder(lastEditorState, e.newState).build());
        lastEditorState = null;
    }

    private class WriteTask implements Runnable {
        private final boolean current;

        public WriteTask(boolean current) {
            this.current = current;
        }

        @Override
        public void run() {
            Check.isMainThread();
            // don't need to save intermediate states, thus {@link History#getCurrent}
            final List<HistoryState> states = current ? getCurrent() : getSaved();
            App.getBackground().execute(new Runnable() {
                @Override
                public void run() {
                    final File file = current ? getCurrentHistoryFile() : getSavedHistoryFile();
                    final JSONArray array = HistoryList.toJson(states);
                    FileSaver.save(file, array.toString());
                }
            });
        }
    }
}