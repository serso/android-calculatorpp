/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.CalculatorPreferences;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.R;

import javax.annotation.Nonnull;
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

	@Nonnull
	@Override
	protected List<CalculatorHistoryState> getHistoryItems() {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		final boolean showIntermediateCalculations = CalculatorPreferences.History.showIntermediateCalculations.getPreference(preferences);
		final List<CalculatorHistoryState> historyStates = Locator.getInstance().getHistory().getStates(showIntermediateCalculations);
		return new ArrayList<CalculatorHistoryState>(historyStates);
	}

	@Override
	protected void clearHistory() {
		Locator.getInstance().getHistory().clear();
		getAdapter().clear();
	}
}
