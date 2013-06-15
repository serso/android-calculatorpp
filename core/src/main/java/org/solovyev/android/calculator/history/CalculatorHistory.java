package org.solovyev.android.calculator.history;

import javax.annotation.Nonnull;
import org.solovyev.android.calculator.CalculatorEventListener;
import org.solovyev.common.history.HistoryHelper;

import java.util.List;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 16:11
 */
public interface CalculatorHistory extends HistoryHelper<CalculatorHistoryState>, CalculatorEventListener {

	void load();

	void save();

	void fromXml(@Nonnull String xml);

	String toXml();

	void clearSavedHistory();

	void removeSavedHistory(@Nonnull CalculatorHistoryState historyState);

	@Nonnull
	List<CalculatorHistoryState> getSavedHistory();

	@Nonnull
	CalculatorHistoryState addSavedState(@Nonnull CalculatorHistoryState historyState);

	@Nonnull
	List<CalculatorHistoryState> getStates();

	@Nonnull
	List<CalculatorHistoryState> getStates(boolean includeIntermediateStates);

}
