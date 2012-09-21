package org.solovyev.android.calculator.history;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.history.HistoryAction;
import org.solovyev.common.history.HistoryHelper;
import org.solovyev.common.history.SimpleHistoryHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Override
    public boolean isEmpty() {
        return this.history.isEmpty();
    }

    @Override
    public CalculatorHistoryState getLastHistoryState() {
        return this.history.getLastHistoryState();
    }

    @Override
    public boolean isUndoAvailable() {
        return history.isUndoAvailable();
    }

    @Override
    public CalculatorHistoryState undo(@Nullable CalculatorHistoryState currentState) {
        return history.undo(currentState);
    }

    @Override
    public boolean isRedoAvailable() {
        return history.isRedoAvailable();
    }

    @Override
    public CalculatorHistoryState redo(@Nullable CalculatorHistoryState currentState) {
        return history.redo(currentState);
    }

    @Override
    public boolean isActionAvailable(@NotNull HistoryAction historyAction) {
        return history.isActionAvailable(historyAction);
    }

    @Override
    public CalculatorHistoryState doAction(@NotNull HistoryAction historyAction, @Nullable CalculatorHistoryState currentState) {
        return history.doAction(historyAction, currentState);
    }

    @Override
    public void addState(@Nullable CalculatorHistoryState currentState) {
        history.addState(currentState);
    }

    @NotNull
    @Override
    public List<CalculatorHistoryState> getStates() {
        return history.getStates();
    }

    @Override
    public void clear() {
        this.history.clear();
    }

    @NotNull
    public List<CalculatorHistoryState> getSavedHistory() {
        return Collections.unmodifiableList(savedHistory);
    }

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
}
