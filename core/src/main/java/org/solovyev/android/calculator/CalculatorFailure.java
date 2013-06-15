package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 7:33 PM
 */
public interface CalculatorFailure {

	@Nonnull
	Exception getException();

	@Nullable
	CalculatorParseException getCalculationParseException();

	@Nullable
	CalculatorEvalException getCalculationEvalException();
}
