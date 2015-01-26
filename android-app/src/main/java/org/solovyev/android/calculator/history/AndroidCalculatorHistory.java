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
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.solovyev.android.calculator.Calculator;
import org.solovyev.android.calculator.CalculatorEventData;
import org.solovyev.android.calculator.CalculatorEventType;
import org.solovyev.common.history.HistoryAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * User: serso
 * Date: 10/9/11
 * Time: 6:35 PM
 */
public class AndroidCalculatorHistory implements CalculatorHistory {

	@Nonnull
	private final CalculatorHistoryImpl calculatorHistory;

	@Nonnull
	private final Context context;

	public AndroidCalculatorHistory(@Nonnull Application application, @Nonnull Calculator calculator) {
		this.context = application;
		calculatorHistory = new CalculatorHistoryImpl(calculator);
	}

	@Override
	public void load() {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		if (preferences != null) {
			final String value = preferences.getString("org.solovyev.android.calculator.CalculatorModel_history", null);
			if (value != null) {
				calculatorHistory.fromXml(value);
			}
		}
	}

	public void save() {
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		final SharedPreferences.Editor editor = settings.edit();

		editor.putString("org.solovyev.android.calculator.CalculatorModel_history", calculatorHistory.toXml());

		editor.apply();
	}

	public void clearSavedHistory() {
		calculatorHistory.clearSavedHistory();
		save();
	}

	public void removeSavedHistory(@Nonnull CalculatorHistoryState historyState) {
		historyState.setSaved(false);
		calculatorHistory.removeSavedHistory(historyState);
		save();
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
	public boolean isActionAvailable(@Nonnull HistoryAction historyAction) {
		return calculatorHistory.isActionAvailable(historyAction);
	}

	@Override
	public CalculatorHistoryState doAction(@Nonnull HistoryAction historyAction, @Nullable CalculatorHistoryState currentState) {
		return calculatorHistory.doAction(historyAction, currentState);
	}

	@Override
	public void addState(@Nullable CalculatorHistoryState currentState) {
		calculatorHistory.addState(currentState);
	}

	@Nonnull
	@Override
	public List<CalculatorHistoryState> getStates() {
		return calculatorHistory.getStates();
	}

	@Nonnull
	@Override
	public List<CalculatorHistoryState> getStates(boolean includeIntermediateStates) {
		return calculatorHistory.getStates(includeIntermediateStates);
	}

	@Override
	public void clear() {
		calculatorHistory.clear();
	}

	@Nonnull
	public List<CalculatorHistoryState> getSavedHistory() {
		return calculatorHistory.getSavedHistory();
	}

	@Nonnull
	public CalculatorHistoryState addSavedState(@Nonnull CalculatorHistoryState historyState) {
		return calculatorHistory.addSavedState(historyState);
	}

	@Override
	public void fromXml(@Nonnull String xml) {
		calculatorHistory.fromXml(xml);
	}

	@Override
	public String toXml() {
		return calculatorHistory.toXml();
	}

	@Override
	public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {
		calculatorHistory.onCalculatorEvent(calculatorEventData, calculatorEventType, data);
	}
}
