/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorEventData;
import org.solovyev.android.calculator.CalculatorEventType;
import org.solovyev.android.calculator.R;
import org.solovyev.common.history.HistoryAction;

import java.util.List;

/**
 * User: serso
 * Date: 10/9/11
 * Time: 6:35 PM
 */
public enum AndroidCalculatorHistoryImpl implements AndroidCalculatorHistory {

    instance;

    @NotNull
    private final CalculatorHistoryImpl calculatorHistory = new CalculatorHistoryImpl();

	@Override
	public void load(@Nullable Context context, @Nullable SharedPreferences preferences) {
		if (context != null && preferences != null) {
			final String value = preferences.getString(context.getString(R.string.p_calc_history), null);
			calculatorHistory.fromXml(value);
		}
	}

	@Override
	public void save(@NotNull Context context) {
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		final SharedPreferences.Editor editor = settings.edit();

		editor.putString(context.getString(R.string.p_calc_history), calculatorHistory.toXml());

		editor.commit();
	}

    public void clearSavedHistory(@NotNull Context context) {
        calculatorHistory.clearSavedHistory();
        save(context);
    }

    public void removeSavedHistory(@NotNull CalculatorHistoryState historyState, @NotNull Context context) {
        historyState.setSaved(false);
        calculatorHistory.removeSavedHistory(historyState);
        save(context);
    }

    @Override
    public boolean isEmpty() {
        return calculatorHistory.isEmpty();
    }

    @Override
    public CalculatorHistoryState getLastHistoryState() {
        return calculatorHistory.getLastHistoryState();
    }

    @Override
    public boolean isUndoAvailable() {
        return calculatorHistory.isUndoAvailable();
    }

    @Override
    public CalculatorHistoryState undo(@Nullable CalculatorHistoryState currentState) {
        return calculatorHistory.undo(currentState);
    }

    @Override
    public boolean isRedoAvailable() {
        return calculatorHistory.isRedoAvailable();
    }

    @Override
    public CalculatorHistoryState redo(@Nullable CalculatorHistoryState currentState) {
        return calculatorHistory.redo(currentState);
    }

    @Override
    public boolean isActionAvailable(@NotNull HistoryAction historyAction) {
        return calculatorHistory.isActionAvailable(historyAction);
    }

    @Override
    public CalculatorHistoryState doAction(@NotNull HistoryAction historyAction, @Nullable CalculatorHistoryState currentState) {
        return calculatorHistory.doAction(historyAction, currentState);
    }

    @Override
    public void addState(@Nullable CalculatorHistoryState currentState) {
        calculatorHistory.addState(currentState);
    }

    @NotNull
    @Override
    public List<CalculatorHistoryState> getStates() {
        return calculatorHistory.getStates();
    }

    @Override
    public void clear() {
        calculatorHistory.clear();
    }

    @NotNull
    public List<CalculatorHistoryState> getSavedHistory() {
        return calculatorHistory.getSavedHistory();
    }

    @NotNull
    public CalculatorHistoryState addSavedState(@NotNull CalculatorHistoryState historyState) {
        return calculatorHistory.addSavedState(historyState);
    }

    @Override
    public void fromXml(@NotNull String xml) {
        calculatorHistory.fromXml(xml);
    }

    @Override
    public String toXml() {
        return calculatorHistory.toXml();
    }

    @Override
    public void clearSavedHistory() {
        calculatorHistory.clearSavedHistory();
    }

    @Override
    public void removeSavedHistory(@NotNull CalculatorHistoryState historyState) {
        calculatorHistory.removeSavedHistory(historyState);
    }

    @Override
    public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable Object data) {
        calculatorHistory.onCalculatorEvent(calculatorEventData, calculatorEventType, data);
    }
}
