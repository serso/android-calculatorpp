package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 7:33 PM
 */
public interface CalculatorFailure {

    @NotNull
    Exception getException();

    @Nullable
    CalculatorParseException getCalculationParseException();

    @Nullable
    CalculatorEvalException getCalculationEvalException();
}
