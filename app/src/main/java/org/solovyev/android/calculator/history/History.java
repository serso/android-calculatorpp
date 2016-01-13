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

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import com.google.common.base.Strings;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.*;
import org.solovyev.android.io.FileLoader;
import org.solovyev.android.io.FileSaver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

import static android.text.TextUtils.isEmpty;

public class History {

    public static final String TAG = App.subTag("History");
    private static final ChangedEvent CHANGED_EVENT_RECENT = new ChangedEvent(true);
    private static final ChangedEvent CHANGED_EVENT_SAVED = new ChangedEvent(false);
    @NonNull
    private final Runnable writeRecent = new WriteTask(true);
    @NonNull
    private final Runnable writeSaved = new WriteTask(false);
    @Nonnull
    private final RecentHistory recent = new RecentHistory();
    @Nonnull
    private final List<HistoryState> saved = new ArrayList<>();
    @Nonnull
    private final Bus bus;
    @Inject
    Handler handler;
    @Inject
    SharedPreferences preferences;
    @Inject
    Editor editor;
    @Inject
    Application application;
    private boolean initialized;

    @Inject
    public History(@NonNull Bus bus, @Nonnull @Named(AppModule.THREAD_INIT) Executor initThread) {
        this.bus = bus;
        this.bus.register(this);
        initThread.execute(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
    }

    @Nullable
    static List<HistoryState> convertOldHistory(@NonNull String xml) {
        final OldHistory history = OldHistory.fromXml(xml);
        if (history == null) {
            // strange, history seems to be broken. Avoid clearing the preference
            return null;
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
        return states;
    }

    @NonNull
    private File getSavedHistoryFile() {
        return new File(application.getFilesDir(), "history-saved.json");
    }

    @NonNull
    private File getRecentHistoryFile() {
        return new File(application.getFilesDir(), "history-recent.json");
    }

    @Nonnull
    private static List<HistoryState> loadStates(@Nonnull File file) {
        if (!file.exists()) {
            return Collections.emptyList();
        }
        final CharSequence json = FileLoader.load(file);
        if (isEmpty(json)) {
            return Collections.emptyList();
        }
        try {
            return RecentHistory.fromJson(new JSONArray(json.toString()));
        } catch (JSONException e) {
            Locator.getInstance().getLogger().error(TAG, e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    private static boolean isIntermediate(@Nonnull String olderText,
                                          @Nonnull String newerText) {
        if (isEmpty(olderText)) {
            return true;
        }
        if (isEmpty(newerText)) {
            return false;
        }

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

    private void migrateOldHistory() {
        try {
            final String xml = preferences.getString("org.solovyev.android.calculator.CalculatorModel_history", null);
            if (isEmpty(xml)) {
                return;
            }
            final List<HistoryState> states = convertOldHistory(xml);
            if (states == null) {
                return;
            }
            final JSONArray json = RecentHistory.toJson(states);
            FileSaver.save(getSavedHistoryFile(), json.toString());
        } catch (Exception e) {
            Locator.getInstance().getLogger().error(TAG, e.getMessage(), e);
        }
    }

    private void init() {
        Check.isNotMainThread();
        migrateOldHistory();
        final List<HistoryState> recentStates = loadStates(getRecentHistoryFile());
        final List<HistoryState> savedStates = loadStates(getSavedHistoryFile());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Check.isTrue(recent.isEmpty());
                Check.isTrue(saved.isEmpty());
                recent.addAll(recentStates);
                saved.addAll(savedStates);
                initialized = true;
            }
        });
    }

    public void addRecent(@Nonnull HistoryState state) {
        Check.isMainThread();
        recent.add(state);
        onRecentChanged();
    }

    public void addSaved(@Nonnull HistoryState state) {
        Check.isMainThread();
        saved.add(state);
        onSavedChanged();
    }

    private void onRecentChanged() {
        handler.removeCallbacks(writeRecent);
        handler.postDelayed(writeRecent, 500);
        bus.post(CHANGED_EVENT_RECENT);
    }

    private void onSavedChanged() {
        handler.removeCallbacks(writeSaved);
        handler.postDelayed(writeSaved, 500);
        bus.post(CHANGED_EVENT_SAVED);
    }

    @Nonnull
    public List<HistoryState> getRecent() {
        Check.isMainThread();

        final List<HistoryState> result = new LinkedList<>();

        final List<HistoryState> states = recent.asList();
        final int statesCount = states.size();
        for (int i = 1; i < statesCount; i++) {
            final HistoryState olderState = states.get(i - 1);
            final HistoryState newerState = states.get(i);
            final String olderText = olderState.editor.getTextString();
            final String newerText = newerState.editor.getTextString();
            if (!isIntermediate(olderText, newerText)) {
                result.add(0, olderState);
            }
        }
        if (statesCount > 0) {
            // try add last state if not empty
            final HistoryState state = states.get(statesCount - 1);
            if (!isEmpty(state.editor.getTextString())) {
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

    public void clearRecent() {
        Check.isMainThread();
        recent.clear();
        onRecentChanged();
    }

    public void clearSaved() {
        Check.isMainThread();
        saved.clear();
        onSavedChanged();
    }

    public void undo() {
        final HistoryState state = recent.undo();
        if (state == null) {
            return;
        }
        applyHistoryState(state);
    }

    public void redo() {
        final HistoryState state = recent.redo();
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

    public void removeRecent(@Nonnull HistoryState state) {
        Check.isMainThread();
        recent.remove(state);
        onRecentChanged();
    }

    @Subscribe
    public void onDisplayChanged(@Nonnull Display.ChangedEvent e) {
        if (!initialized) {
            return;
        }
        final EditorState editorState = editor.getState();
        final DisplayState displayState = e.newState;
        if (editorState.sequence != displayState.sequence) {
            return;
        }
        addRecent(HistoryState.newBuilder(editorState, displayState).build());
    }

    public static class ChangedEvent {
        public final boolean recent;

        public ChangedEvent(boolean recent) {
            this.recent = recent;
        }
    }

    private class WriteTask implements Runnable {
        private final boolean recent;

        public WriteTask(boolean recent) {
            this.recent = recent;
        }

        @Override
        public void run() {
            Check.isMainThread();
            // don't need to save intermediate states, thus {@link History#getRecent}
            final List<HistoryState> states = recent ? getRecent() : getSaved();
            App.getBackground().execute(new Runnable() {
                @Override
                public void run() {
                    final File file = recent ? getRecentHistoryFile() : getSavedHistoryFile();
                    final JSONArray array = RecentHistory.toJson(states);
                    FileSaver.save(file, array.toString());
                }
            });
        }
    }
}