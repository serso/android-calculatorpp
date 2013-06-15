package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 18:18
 */
public interface CalculatorEventData {

	// the higher id => the later event
	long getEventId();

	// the higher id => the later event
	@Nonnull
	Long getSequenceId();

	@Nullable
	Object getSource();

	boolean isAfter(@Nonnull CalculatorEventData that);

	boolean isSameSequence(@Nonnull CalculatorEventData that);

	boolean isAfterSequence(@Nonnull CalculatorEventData that);
}
