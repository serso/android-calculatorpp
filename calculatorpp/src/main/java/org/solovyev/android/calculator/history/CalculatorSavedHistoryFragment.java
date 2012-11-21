/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.about.CalculatorFragmentType;

import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 12/18/11
 * Time: 7:40 PM
 */
public class CalculatorSavedHistoryFragment extends AbstractCalculatorHistoryFragment {

    public CalculatorSavedHistoryFragment() {
        super(CalculatorFragmentType.saved_history);
    }

    @Override
	protected int getItemLayoutId() {
		return R.layout.saved_history_item;
	}

	@NotNull
	@Override
	protected List<CalculatorHistoryState> getHistoryItems() {
		return new ArrayList<CalculatorHistoryState>(Locator.getInstance().getHistory().getSavedHistory());
	}

	@Override
	protected void clearHistory() {
        Locator.getInstance().getHistory().clearSavedHistory();
		getAdapter().clear();
	}
}
