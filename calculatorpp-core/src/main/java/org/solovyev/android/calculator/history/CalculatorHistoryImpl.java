package org.solovyev.android.calculator.history;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.*;
import org.solovyev.common.history.HistoryAction;
import org.solovyev.common.history.HistoryHelper;
import org.solovyev.common.history.SimpleHistoryHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.solovyev.android.calculator.CalculatorEventType.display_state_changed;
import static org.solovyev.android.calculator.CalculatorEventType.editor_state_changed;
import static org.solovyev.android.calculator.CalculatorEventType.manual_calculation_requested;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 16:12
 */
public class CalculatorHistoryImpl implements CalculatorHistory {

    private final AtomicInteger counter = new AtomicInteger(0);

    @NotNull
    private final HistoryHelper<CalculatorHistoryState> history = new SimpleHistoryHelper<CalculatorHistoryState>();

    @NotNull
    private final List<CalculatorHistoryState> savedHistory = new ArrayList<CalculatorHistoryState>();

    @NotNull
    private volatile CalculatorEventData lastEventData = CalculatorUtils.createFirstEventDataId();

    @NotNull
    private final Object lastEventDataLock = new Object();

    @Nullable
    private volatile CalculatorEditorViewState lastEditorViewState;

    public CalculatorHistoryImpl(@NotNull Calculator calculator) {
        calculator.addCalculatorEventListener(this);
    }

    @Override
    public boolean isEmpty() {
        synchronized (history) {
            return this.history.isEmpty();
        }
    }

    @Override
    public CalculatorHistoryState getLastHistoryState() {
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
    public CalculatorHistoryState undo(@Nullable CalculatorHistoryState currentState) {
        synchronized (history) {
            return history.undo(currentState);
        }
    }

    @Override
    public boolean isRedoAvailable() {
        return history.isRedoAvailable();
    }

    @Override
    public CalculatorHistoryState redo(@Nullable CalculatorHistoryState currentState) {
        synchronized (history) {
            return history.redo(currentState);
        }
    }

    @Override
    public boolean isActionAvailable(@NotNull HistoryAction historyAction) {
        synchronized (history) {
            return history.isActionAvailable(historyAction);
        }
    }

    @Override
    public CalculatorHistoryState doAction(@NotNull HistoryAction historyAction, @Nullable CalculatorHistoryState currentState) {
        synchronized (history) {
            return history.doAction(historyAction, currentState);
        }
    }

    @Override
    public void addState(@Nullable CalculatorHistoryState currentState) {
        synchronized (history) {
            history.addState(currentState);
            CalculatorLocatorImpl.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.history_state_added, currentState);
        }
    }

    @NotNull
    @Override
    public List<CalculatorHistoryState> getStates() {
        synchronized (history) {
            return history.getStates();
        }
    }

    @Override
    public void clear() {
        synchronized (history) {
            this.history.clear();
        }
    }

    @Override
    @NotNull
    public List<CalculatorHistoryState> getSavedHistory() {
        return Collections.unmodifiableList(savedHistory);
    }

    @Override
    @NotNull
    public CalculatorHistoryState addSavedState(@NotNull CalculatorHistoryState historyState) {
        if (historyState.isSaved()) {
            return historyState;
        } else {
            final CalculatorHistoryState savedState = historyState.clone();

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
    public void fromXml(@NotNull String xml) {
        clearSavedHistory();

        HistoryUtils.fromXml(xml, this.savedHistory);
        for (CalculatorHistoryState historyState : savedHistory) {
            historyState.setSaved(true);
            historyState.setId(counter.incrementAndGet());
        }
    }

    @Override
    public String toXml() {
        return HistoryUtils.toXml(this.savedHistory);
    }

    @Override
    public void clearSavedHistory() {
        this.savedHistory.clear();
    }

    @Override
    public void removeSavedHistory(@NotNull CalculatorHistoryState historyState) {
        this.savedHistory.remove(historyState);
    }

    @Override
    public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData,
                                  @NotNull CalculatorEventType calculatorEventType,
                                  @Nullable Object data) {
        if (calculatorEventType.isOfType(editor_state_changed, display_state_changed, manual_calculation_requested)) {

            boolean sameSequence = false;
            boolean afterSequence = false;

            boolean processEvent = false;

            synchronized (this.lastEventDataLock) {
                if (calculatorEventData.isAfter(this.lastEventData)) {

                    sameSequence = calculatorEventData.isSameSequence(this.lastEventData);
                    if (!sameSequence) {
                        afterSequence = calculatorEventData.isAfterSequence(this.lastEventData);
                        processEvent = afterSequence;
                    } else {
                        processEvent = true;
                    }
                }
            }

            if (processEvent) {
                    this.lastEventData = calculatorEventData;

                    switch (calculatorEventType) {
                        case manual_calculation_requested:
                            lastEditorViewState = (CalculatorEditorViewState) data;
                            break;
                        case editor_state_changed:
                            final CalculatorEditorChangeEventData editorChangeData = (CalculatorEditorChangeEventData) data;
                            lastEditorViewState = editorChangeData.getNewState();
                            break;
                        case display_state_changed:
                            if (sameSequence) {
                                if (lastEditorViewState != null) {
                                    final CalculatorEditorViewState editorViewState = lastEditorViewState;
                                    final CalculatorDisplayChangeEventData displayChangeData = (CalculatorDisplayChangeEventData) data;
                                    final CalculatorDisplayViewState displayViewState = displayChangeData.getNewState();
                                    addState(CalculatorHistoryState.newInstance(editorViewState, displayViewState));
                                }
                            } else {
                                lastEditorViewState = null;
                            }
                            break;
                    }
                }
        }
    }
}
