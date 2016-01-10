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
import com.squareup.otto.Subscribe;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.*;
import org.solovyev.common.history.HistoryAction;
import org.solovyev.common.history.HistoryHelper;
import org.solovyev.common.history.SimpleHistoryHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CalculatorHistory {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Nonnull
    private final HistoryHelper<OldHistoryState> history = SimpleHistoryHelper.newInstance();

    @Nonnull
    private final OldHistory savedOldHistory = new OldHistory();

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

    private void init() {
        Check.isNotMainThread();
        migrateOldHistory();
    }

    private void migrateOldHistory() {
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
        for (OldHistoryState state : history.getItems()) {
            state.setSaved(true);
            state.setId(counter.incrementAndGet());
            savedOldHistory.add(state);
        }
    }

    public boolean isEmpty() {
        Check.isMainThread();
        return history.isEmpty();
    }

    public boolean isActionAvailable(@Nonnull HistoryAction action) {
        Check.isMainThread();
        return history.isActionAvailable(action);
    }

    public OldHistoryState doAction(@Nonnull HistoryAction action, @Nullable OldHistoryState state) {
        Check.isMainThread();
        return history.doAction(action, state);
    }

    public void addState(@Nullable OldHistoryState currentState) {
        Check.isMainThread();
        history.addState(currentState);
        Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.history_state_added, currentState);
    }

    @Nonnull
    public List<OldHistoryState> getStates() {
        Check.isMainThread();
        return history.getStates();
    }

    @Nonnull
    public List<OldHistoryState> getStates(boolean includeIntermediateStates) {
        Check.isMainThread();
            if (includeIntermediateStates) {
                return getStates();
            } else {
                final List<OldHistoryState> states = getStates();

                final List<OldHistoryState> result = new LinkedList<>();

                OldHistoryState laterState = null;
                for (OldHistoryState state : org.solovyev.common.collections.Collections.reversed(states)) {
                    if (laterState != null) {
                        final String laterEditorText = laterState.getEditorState().getText();
                        final String editorText = state.getEditorState().getText();
                        if (laterEditorText != null && editorText != null && isIntermediate(laterEditorText, editorText)) {
                            // intermediate result => skip from add
                        } else {
                            result.add(0, state);
                        }
                    } else {
                        result.add(0, state);
                    }

                    laterState = state;
                }

                return result;
            }
        }

    private boolean isIntermediate(@Nonnull String laterEditorText,
                                   @Nonnull String editorText) {
        if (Math.abs(laterEditorText.length() - editorText.length()) <= 1) {
            if (laterEditorText.length() > editorText.length()) {
                return laterEditorText.startsWith(editorText);
            } else {
                return editorText.startsWith(laterEditorText);
            }
        }

        return false;
    }

    public void clear() {
        Check.isMainThread();
        history.clear();
    }

    @Nonnull
    public List<OldHistoryState> getSavedOldHistory() {
        return Collections.unmodifiableList(savedOldHistory.getItems());
    }

    @Nonnull
    public OldHistoryState addSavedState(@Nonnull OldHistoryState state) {
        if (state.isSaved()) {
            return state;
        } else {
            final OldHistoryState savedState = state.clone();

            savedState.setId(counter.incrementAndGet());
            savedState.setSaved(true);

            savedOldHistory.add(savedState);

            return savedState;
        }
    }

    public void load() {
    }

    public void save() {
        final SharedPreferences settings = App.getPreferences();
        final SharedPreferences.Editor editor = settings.edit();
        editor.putString("org.solovyev.android.calculator.CalculatorModel_history", toXml());
        editor.apply();
    }

    public String toXml() {
        return savedOldHistory.toXml();
    }

    public void clearSavedHistory() {
        savedOldHistory.clear();
        save();
    }

    public void removeSavedHistory(@Nonnull OldHistoryState historyState) {
        historyState.setSaved(false);
        this.savedOldHistory.remove(historyState);
        save();
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
        addState(OldHistoryState.create(lastEditorState, e.newState));
        lastEditorState = null;
    }
}
