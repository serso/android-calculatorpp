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
import android.text.TextUtils;
import com.google.common.base.Strings;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.Engine.Preferences;
import org.solovyev.android.calculator.json.Json;
import org.solovyev.android.io.FileSystem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

import static android.text.TextUtils.isEmpty;

@Singleton
public class History {

    public static final String OLD_HISTORY_PREFS_KEY = "org.solovyev.android.calculator.CalculatorModel_history";
    private static final ClearedEvent CLEARED_EVENT_RECENT = new ClearedEvent(true);
    private static final ClearedEvent CLEARED_EVENT_SAVED = new ClearedEvent(false);
    private static final int MAX_INTERMEDIATE_STREAK = 5;
    @NonNull
    private final Runnable writeRecent = new WriteTask(true);
    @NonNull
    private final Runnable writeSaved = new WriteTask(false);
    @Nonnull
    private final RecentHistory recent = new RecentHistory();
    @Nonnull
    private final List<HistoryState> saved = new ArrayList<>();
    @Nonnull
    private final Runnables whenLoadedRunnables = new Runnables();
    private boolean loaded;
    @Inject
    Application application;
    @Inject
    Bus bus;
    @Inject
    Handler handler;
    @Inject
    SharedPreferences preferences;
    @Inject
    Editor editor;
    @Inject
    Display display;
    @Inject
    ErrorReporter errorReporter;
    @Inject
    FileSystem fileSystem;
    @Inject
    @Named(AppModule.THREAD_BACKGROUND)
    Executor backgroundThread;
    @Inject
    @Named(AppModule.DIR_FILES)
    File filesDir;

    @Nullable
    static List<HistoryState> convertOldHistory(@NonNull String xml) throws Exception {
        final OldHistory history = OldHistory.fromXml(xml);
        if (history == null) {
            // strange, history seems to be broken. Avoid clearing the preference
            return null;
        }
        final List<HistoryState> states = new ArrayList<>();
        for (OldHistoryState state : history.getItems()) {
            final OldEditorHistoryState oldEditor = state.getEditorState();
            final OldDisplayHistoryState oldDisplay = state.getDisplayState();
            final String editorText = oldEditor.getText();
            final EditorState editor = EditorState.create(Strings.nullToEmpty(editorText), oldEditor.getCursorPosition());
            final DisplayState display = DisplayState.createValid(oldDisplay.getJsclOperation(), null, Strings.nullToEmpty(oldDisplay.getEditorState().getText()), Calculator.NO_SEQUENCE);
            states.add(HistoryState.builder(editor, display).withTime(state.getTime()).withComment(state.getComment()).build());
        }
        return states;
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
    static String trimGroupingSeparators(@NonNull String text, @NonNull String groupingSeparator) {
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
            if (Character.isDigit(text.charAt(i - 1)) && text.charAt(i) == groupingSeparator.charAt(0) && Character.isDigit(text.charAt(i + 1))) {
                // grouping separator => skip
                continue;
            }
            sb.append(text.charAt(i));

        }
        return sb.toString();
    }

    @Inject
    public History() {
    }

    public void init(@NonNull Executor initThread) {
        Check.isMainThread();
        bus.register(this);
        initThread.execute(new Runnable() {
            @Override
            public void run() {
                initAsync();
            }
        });
    }

    void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    @NonNull
    File getSavedHistoryFile() {
        return new File(filesDir, "history-saved.json");
    }

    @NonNull
    File getRecentHistoryFile() {
        return new File(filesDir, "history-recent.json");
    }

    private void migrateOldHistory() {
        try {
            final String xml = preferences.getString(OLD_HISTORY_PREFS_KEY, null);
            if (isEmpty(xml)) {
                return;
            }
            final List<HistoryState> states = convertOldHistory(xml);
            if (states == null) {
                return;
            }
            final JSONArray json = Json.toJson(states);
            fileSystem.write(getSavedHistoryFile(), json.toString());
            preferences.edit().remove(OLD_HISTORY_PREFS_KEY).apply();
        } catch (Exception e) {
            errorReporter.onException(e);
        }
    }

    private void initAsync() {
        Check.isNotMainThread();
        migrateOldHistory();
        final List<HistoryState> recentStates = tryLoadStates(getRecentHistoryFile());
        final List<HistoryState> savedStates = tryLoadStates(getSavedHistoryFile());
        handler.post(new Runnable() {
            @Override
            public void run() {
                onLoaded(recentStates, savedStates);
            }
        });
    }

    private void onLoaded(@NonNull List<HistoryState> recentStates, @NonNull List<HistoryState> savedStates) {
        Check.isTrue(saved.isEmpty());
        Check.isMainThread();
        final boolean wasEmpty = recent.isEmpty();
        recent.addInitial(recentStates);
        saved.addAll(savedStates);
        if (wasEmpty) {
            // user has typed nothing while we were loading, let's use recent history to restore
            // editor state
            editor.onHistoryLoaded(recent);
        } else {
            // user has types something => we should schedule save
            postRecentWrite();
        }
        loaded = true;
        whenLoadedRunnables.run();
    }

    @Nonnull
    private List<HistoryState> tryLoadStates(@NonNull File file) {
        try {
            return Json.load(file, fileSystem, HistoryState.JSON_CREATOR);
        } catch (IOException | JSONException e) {
            errorReporter.onException(e);
        }
        return Collections.emptyList();
    }

    public void addRecent(@Nonnull HistoryState state) {
        Check.isMainThread();
        if (recent.isEmpty() && state.isEmpty()) {
            // don't add empty states to empty history
            return;
        }
        if (recent.add(state)) {
            onRecentChanged(new AddedEvent(state, true));
        }
    }

    public void updateSaved(@Nonnull HistoryState state) {
        Check.isMainThread();
        final int i = saved.indexOf(state);
        if(i >= 0) {
            saved.set(i, state);
            onSavedChanged(new UpdatedEvent(state, false));
        } else {
            saved.add(state);
            onSavedChanged(new AddedEvent(state, false));
        }
    }

    private void onRecentChanged(@Nonnull Object event) {
        postRecentWrite();
        bus.post(event);
    }

    private void postRecentWrite() {
        handler.removeCallbacks(writeRecent);
        handler.postDelayed(writeRecent, 5000);
    }

    private void onSavedChanged(@Nonnull Object event) {
        postSavedWrite();
        bus.post(event);
    }

    private void postSavedWrite() {
        handler.removeCallbacks(writeSaved);
        handler.postDelayed(writeSaved, 500);
    }

    @Nonnull
    public List<HistoryState> getRecent() {
        return getRecent(true);
    }

    @Nonnull
    private List<HistoryState> getRecent(boolean forUi) {
        Check.isMainThread();

        final List<HistoryState> result = new LinkedList<>();

        final String groupingSeparator = Preferences.groupingSeparator.getPreference(preferences);

        final List<HistoryState> states = recent.asList();
        final int statesCount = states.size();
        int streak = 0;
        for (int i = 1; i < statesCount; i++) {
            final HistoryState olderState = states.get(i - 1);
            final HistoryState newerState = states.get(i);
            final String olderText = olderState.editor.getTextString();
            final String newerText = newerState.editor.getTextString();
            if (streak >= MAX_INTERMEDIATE_STREAK || !isIntermediate(olderText, newerText, groupingSeparator)) {
                result.add(0, olderState);
                streak = 0;
            } else {
                streak++;
            }
        }
        if (statesCount > 0) {
            // try add last state if not empty
            final HistoryState state = states.get(statesCount - 1);
            if (!state.editor.isEmpty() || !forUi) {
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
        onRecentChanged(CLEARED_EVENT_RECENT);
    }

    public void clearSaved() {
        Check.isMainThread();
        saved.clear();
        onSavedChanged(CLEARED_EVENT_SAVED);
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
        editor.setState(state.editor);
        display.setState(state.display);
    }

    public void removeSaved(@Nonnull HistoryState state) {
        Check.isMainThread();
        saved.remove(state);
        onSavedChanged(new RemovedEvent(state, false));
    }

    @Subscribe
    public void onDisplayChanged(@Nonnull Display.ChangedEvent e) {
        final EditorState editorState = editor.getState();
        final DisplayState displayState = e.newState;
        if (editorState.sequence != displayState.sequence) {
            return;
        }
        addRecent(HistoryState.builder(editorState, displayState).build());
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void runWhenLoaded(@NonNull Runnable runnable) {
        Check.isTrue(!loaded);
        whenLoadedRunnables.add(runnable);
    }

    public static class ClearedEvent {
        public final boolean recent;

        ClearedEvent(boolean recent) {
            this.recent = recent;
        }
    }

    public static class RemovedEvent extends StateEvent {
        RemovedEvent(@Nonnull HistoryState state, boolean recent) {
            super(state, recent);
        }
    }

    public static class AddedEvent extends StateEvent {
        AddedEvent(@Nonnull HistoryState state, boolean recent) {
            super(state, recent);
        }
    }

    public static class UpdatedEvent extends StateEvent {
        UpdatedEvent(@Nonnull HistoryState state, boolean recent) {
            super(state, recent);
        }
    }

    public abstract static class StateEvent {
        @Nonnull
        public final HistoryState state;
        public final boolean recent;

        protected StateEvent(@Nonnull HistoryState state, boolean recent) {
            this.state = state;
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
            if (!loaded) {
                return;
            }
            // don't need to save intermediate states, thus {@link History#getRecent}
            final List<HistoryState> states = recent ? getRecent(false) : getSaved();
            backgroundThread.execute(new Runnable() {
                @Override
                public void run() {
                    final File file = recent ? getRecentHistoryFile() : getSavedHistoryFile();
                    final JSONArray array = Json.toJson(states);
                    fileSystem.writeSilently(file, array.toString());
                }
            });
        }
    }
}