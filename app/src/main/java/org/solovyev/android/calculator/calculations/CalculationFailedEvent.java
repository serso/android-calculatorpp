package org.solovyev.android.calculator.calculations;

import org.solovyev.android.calculator.jscl.JsclOperation;

import javax.annotation.Nonnull;

public final class CalculationFailedEvent extends BaseCalculationEvent {
    @Nonnull
    public final Exception exception;

    public CalculationFailedEvent(@Nonnull JsclOperation operation, @Nonnull String expression, long sequence, @Nonnull Exception exception) {
        super(operation, expression, sequence);
        this.exception = exception;
    }
}
