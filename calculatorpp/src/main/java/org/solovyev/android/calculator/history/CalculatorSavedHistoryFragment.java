/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.CalculatorLocatorImpl;
import org.solovyev.android.calculator.R;

import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 12/18/11
 * Time: 7:40 PM
 */
public class CalculatorSavedHistoryFragment extends AbstractCalculatorHistoryFragment {

    @Override
    protected int getFragmentTitleResId() {
        return R.string.c_saved_history;
    }

    @Override
	protected int getItemLayoutId() {
		return R.layout.saved_history;
	}

	@NotNull
	@Override
	protected List<CalculatorHistoryState> getHistoryItems() {
		return new ArrayList<CalculatorHistoryState>(CalculatorLocatorImpl.getInstance().getHistory().getSavedHistory());
	}

	@Override
	protected void clearHistory() {
        CalculatorLocatorImpl.getInstance().getHistory().clearSavedHistory();
		getAdapter().clear();
	}
}
