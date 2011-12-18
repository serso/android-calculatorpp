/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.CalculatorHistoryActivity;

/**
* User: serso
* Date: 12/18/11
* Time: 3:10 PM
*/
public class HistoryItemMenuData {

	@NotNull
	private final CalculatorHistoryActivity.HistoryArrayAdapter adapter;

	@NotNull
	private final CalculatorHistoryState historyState;

	public HistoryItemMenuData(@NotNull CalculatorHistoryState historyState, CalculatorHistoryActivity.HistoryArrayAdapter adapter) {
		this.historyState = historyState;
		this.adapter = adapter;
	}

	@NotNull
	public CalculatorHistoryState getHistoryState() {
		return historyState;
	}

	@NotNull
	public CalculatorHistoryActivity.HistoryArrayAdapter getAdapter() {
		return adapter;
	}
}
