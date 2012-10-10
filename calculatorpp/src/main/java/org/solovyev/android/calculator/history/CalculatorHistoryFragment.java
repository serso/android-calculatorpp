/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import android.preference.PreferenceManager;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.CalculatorLocatorImpl;
import org.solovyev.android.calculator.CalculatorPreferences;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.about.CalculatorFragmentType;

import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 12/18/11
 * Time: 7:39 PM
 */
public class CalculatorHistoryFragment extends AbstractCalculatorHistoryFragment {

    public CalculatorHistoryFragment() {
        super(CalculatorFragmentType.history);
    }

    @Override
	protected int getItemLayoutId() {
		return R.layout.history_item;
	}

	@NotNull
	@Override
	protected List<CalculatorHistoryState> getHistoryItems() {
        final boolean showIntermediateCalculations = CalculatorPreferences.History.showIntermediateCalculations.getPreference(PreferenceManager.getDefaultSharedPreferences(getActivity()));
		return new ArrayList<CalculatorHistoryState>(CalculatorLocatorImpl.getInstance().getHistory().getStates(showIntermediateCalculations));
	}

	@Override
	protected void clearHistory() {
        CalculatorLocatorImpl.getInstance().getHistory().clear();
		getAdapter().clear();
	}
}
