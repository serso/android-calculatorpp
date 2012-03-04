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
import org.solovyev.android.calculator.R;
import org.solovyev.common.utils.history.HistoryAction;
import org.solovyev.common.utils.history.HistoryHelper;
import org.solovyev.common.utils.history.SimpleHistoryHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: serso
 * Date: 10/9/11
 * Time: 6:35 PM
 */
public enum CalculatorHistory implements HistoryHelper<CalculatorHistoryState> {

	instance;

	// todo serso: not synchronized
	private int counter = 0;

	@NotNull
	private final HistoryHelper<CalculatorHistoryState> history = new SimpleHistoryHelper<CalculatorHistoryState>();

	@NotNull
	private final List<CalculatorHistoryState> savedHistory = new ArrayList<CalculatorHistoryState> ();

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

	public void load(@Nullable Context context, @Nullable SharedPreferences preferences) {
		if (context != null && preferences != null) {
			final String value = preferences.getString(context.getString(R.string.p_calc_history), null);
			this.savedHistory.clear();
			HistoryUtils.fromXml(value, this.savedHistory);
			for (CalculatorHistoryState historyState : savedHistory) {
				historyState.setSaved(true);
				historyState.setId(counter++);
			}
		}
	}

	public void save(@NotNull Context context) {
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		final SharedPreferences.Editor editor = settings.edit();

		editor.putString(context.getString(R.string.p_calc_history), HistoryUtils.toXml(this.savedHistory));

		editor.commit();
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

			savedState.setId(counter++);
			savedState.setSaved(true);

			savedHistory.add(savedState);

			return savedState;
		}
	}

	public void clearSavedHistory(@NotNull Context context) {
		this.savedHistory.clear();
		save(context);
	}

	public void removeSavedHistory(@NotNull CalculatorHistoryState historyState, @NotNull Context context) {
		historyState.setSaved(false);
		this.savedHistory.remove(historyState);
		save(context);
	}
}
