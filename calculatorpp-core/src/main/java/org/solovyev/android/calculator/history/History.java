/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 12/17/11
 * Time: 9:30 PM
 */

@Root
public class History {

	@ElementList(type = CalculatorHistoryState.class)
	private List<CalculatorHistoryState> historyItems = new ArrayList<CalculatorHistoryState>();

	public History() {
	}

	public List<CalculatorHistoryState> getHistoryItems() {
		return historyItems;
	}
}
