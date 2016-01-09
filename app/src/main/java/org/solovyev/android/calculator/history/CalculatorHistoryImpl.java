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

import com.squareup.otto.Subscribe;
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

import static org.solovyev.android.calculator.CalculatorEventType.manual_calculation_requested;

public class CalculatorHistoryImpl implements CalculatorHistory {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Nonnull
    private final HistoryHelper<HistoryState> history = SimpleHistoryHelper.newInstance();

    @Nonnull
    private final History savedHistory = new History();

    @Nonnull
    private final CalculatorEventHolder lastEventData = new CalculatorEventHolder(CalculatorUtils.createFirstEventDataId());

    @Nullable
    private EditorState lastEditorState;

    public CalculatorHistoryImpl(@Nonnull Calculator calculator) {
        calculator.addCalculatorEventListener(this);
        App.getBus().register(this);
    }

    @Override
    public boolean isEmpty() {
        synchronized (history) {
            return this.history.isEmpty();
        }
    }

    @Override
    public HistoryState getLastHistoryState() {
        synchronized (history) {
            return this.history.getLastHistoryState();
        }
    }

    @Override
    public boolean isUndoAvailable() {
        synchronized (history) {
            return history.isUndoAvailable();
        }
    }

    @Override
    public HistoryState undo(@Nullable HistoryState currentState) {
        synchronized (history) {
            return history.undo(currentState);
        }
    }

    @Override
    public boolean isRedoAvailable() {
        return history.isRedoAvailable();
    }

    @Override
    public HistoryState redo(@Nullable HistoryState currentState) {
        synchronized (history) {
            return history.redo(currentState);
        }
    }

    @Override
    public boolean isActionAvailable(@Nonnull HistoryAction historyAction) {
        synchronized (history) {
            return history.isActionAvailable(historyAction);
        }
    }

    @Override
    public HistoryState doAction(@Nonnull HistoryAction historyAction, @Nullable HistoryState currentState) {
        synchronized (history) {
            return history.doAction(historyAction, currentState);
        }
    }

    @Override
    public void addState(@Nullable HistoryState currentState) {
        synchronized (history) {
            history.addState(currentState);
            Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.history_state_added, currentState);
        }
    }

    @Nonnull
    @Override
    public List<HistoryState> getStates() {
        synchronized (history) {
            return history.getStates();
        }
    }

    @Nonnull
    @Override
    public List<HistoryState> getStates(boolean includeIntermediateStates) {
        synchronized (history) {
            if (includeIntermediateStates) {
                return getStates();
            } else {
                final List<HistoryState> states = getStates();

                final List<HistoryState> result = new LinkedList<HistoryState>();

                HistoryState laterState = null;
                for (HistoryState state : org.solovyev.common.collections.Collections.reversed(states)) {
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

    @Override
    public void clear() {
        synchronized (history) {
            this.history.clear();
        }
    }

    @Override
    @Nonnull
    public List<HistoryState> getSavedHistory() {
        return Collections.unmodifiableList(savedHistory.getItems());
    }

    @Override
    @Nonnull
    public HistoryState addSavedState(@Nonnull HistoryState historyState) {
        if (historyState.isSaved()) {
            return historyState;
        } else {
            final HistoryState savedState = historyState.clone();

            savedState.setId(counter.incrementAndGet());
            savedState.setSaved(true);

            savedHistory.add(savedState);

            return savedState;
        }
    }

    @Override
    public void load() {
        // todo serso: create saved/loader class
    }

    @Override
    public void save() {
        // todo serso: create saved/loader class
    }

    @Override
    public void fromXml(@Nonnull String xml) {
        clearSavedHistory();

        final History history = History.fromXml(xml);
        if (history == null) {
            return;
        }
        for (HistoryState historyState : history.getItems()) {
            historyState.setSaved(true);
            historyState.setId(counter.incrementAndGet());
            savedHistory.add(historyState);
        }
    }

    @Override
    public String toXml() {
        return savedHistory.toXml();
    }

    @Override
    public void clearSavedHistory() {
        savedHistory.clear();
    }

    @Override
    public void removeSavedHistory(@Nonnull HistoryState historyState) {
        this.savedHistory.remove(historyState);
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
        addState(HistoryState.create(lastEditorState, e.newState));
        lastEditorState = null;
    }

    @Override
    public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData,
                                  @Nonnull CalculatorEventType calculatorEventType,
                                  @Nullable Object data) {
        if (calculatorEventType.isOfType(manual_calculation_requested)) {
            final CalculatorEventHolder.Result result = lastEventData.apply(calculatorEventData);
            if (result.isNewAfter() && result.isNewSameOrAfterSequence()) {
                switch (calculatorEventType) {
                    case manual_calculation_requested:
                        lastEditorState = (EditorState) data;
                        break;
                }
            }
        }
    }
}
