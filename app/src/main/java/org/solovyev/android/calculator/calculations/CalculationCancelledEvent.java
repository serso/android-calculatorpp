package org.solovyev.android.calculator.calculations;

import org.solovyev.android.calculator.jscl.JsclOperation;

import javax.annotation.Nonnull;

public final class CalculationCancelledEvent extends BaseCalculationEvent {
    public CalculationCancelledEvent(@Nonnull JsclOperation operation, @Nonnull String expression, long sequence) {
        super(operation, expression, sequence);
    }
}
