package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 7:34 PM
 */
public class CalculatorFailureImpl implements CalculatorFailure {

    @NotNull
    private Exception exception;

    public CalculatorFailureImpl(@NotNull Exception exception) {
        this.exception = exception;
    }

    @NotNull
    @Override
    public Exception getException() {
        return this.exception;
    }

    @Override
    public CalculatorParseException getCalculationParseException() {
        return exception instanceof CalculatorParseException ? (CalculatorParseException)exception : null;
    }

    @Override
    public CalculatorEvalException getCalculationEvalException() {
        return exception instanceof CalculatorEvalException ? (CalculatorEvalException)exception : null;
    }

    @Override
    public String toString() {
        return "CalculatorFailureImpl{" +
                "exception=" + exception +
                '}';
    }
}
