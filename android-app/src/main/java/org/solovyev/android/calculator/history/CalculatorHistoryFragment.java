/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import android.preference.PreferenceManager;
import javax.annotation.Nonnull;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.CalculatorPreferences;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.R;

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
		final boolean showIntermediateCalculations = CalculatorPreferences.History.showIntermediateCalculations.getPreference(PreferenceManager.getDefaultSharedPreferences(getActivity()));
		return new ArrayList<CalculatorHistoryState>(Locator.getInstance().getHistory().getStates(showIntermediateCalculations));
	}

	@Override
	protected void clearHistory() {
		Locator.getInstance().getHistory().clear();
		getAdapter().clear();
	}
}
