/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import android.widget.ArrayAdapter;
import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 12/18/11
 * Time: 3:10 PM
 */
public class HistoryItemMenuData {

	@Nonnull
	private final ArrayAdapter<CalculatorHistoryState> adapter;

	@Nonnull
	private final CalculatorHistoryState historyState;

	public HistoryItemMenuData(@Nonnull CalculatorHistoryState historyState, ArrayAdapter<CalculatorHistoryState> adapter) {
		this.historyState = historyState;
		this.adapter = adapter;
	}

	@Nonnull
	public CalculatorHistoryState getHistoryState() {
		return historyState;
	}

	@Nonnull
	public ArrayAdapter<CalculatorHistoryState> getAdapter() {
		return adapter;
	}
}
