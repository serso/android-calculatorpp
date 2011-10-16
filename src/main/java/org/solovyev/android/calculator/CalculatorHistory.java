/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.utils.history.HistoryAction;
import org.solovyev.common.utils.history.HistoryHelper;
import org.solovyev.common.utils.history.SimpleHistoryHelper;

import java.util.List;

/**
 * User: serso
 * Date: 10/9/11
 * Time: 6:35 PM
 */
public enum CalculatorHistory implements HistoryHelper<CalculatorHistoryState> {

	instance;

	private final HistoryHelper<CalculatorHistoryState> historyHelper = new SimpleHistoryHelper<CalculatorHistoryState>();

	@Override
	public boolean isEmpty() {
		return this.historyHelper.isEmpty();
	}

	@Override
	public CalculatorHistoryState getLastHistoryState() {
		return this.historyHelper.getLastHistoryState();
	}

	@Override
	public boolean isUndoAvailable() {
		return historyHelper.isUndoAvailable();
	}

	@Override
	public CalculatorHistoryState undo(@Nullable CalculatorHistoryState currentState) {
		return historyHelper.undo(currentState);
	}

	@Override
	public boolean isRedoAvailable() {
		return historyHelper.isRedoAvailable();
	}

	@Override
	public CalculatorHistoryState redo(@Nullable CalculatorHistoryState currentState) {
		return historyHelper.redo(currentState);
	}

	@Override
	public boolean isActionAvailable(@NotNull HistoryAction historyAction) {
		return historyHelper.isActionAvailable(historyAction);
	}

	@Override
	public CalculatorHistoryState doAction(@NotNull HistoryAction historyAction, @Nullable CalculatorHistoryState currentState) {
		return historyHelper.doAction(historyAction, currentState);
	}

	@Override
	public void addState(@Nullable CalculatorHistoryState currentState) {
		historyHelper.addState(currentState);
	}

	@NotNull
	@Override
	public List<CalculatorHistoryState> getStates() {
		return historyHelper.getStates();
	}

	@Override
	public void clear() {
		this.historyHelper.clear();
	}
}
