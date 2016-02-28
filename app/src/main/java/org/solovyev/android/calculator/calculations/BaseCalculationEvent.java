package org.solovyev.android.calculator.calculations;

import org.solovyev.android.calculator.jscl.JsclOperation;

import javax.annotation.Nonnull;

public abstract class BaseCalculationEvent {
    @Nonnull
    public final JsclOperation operation;
    @Nonnull
    public final String expression;
    public final long sequence;

    protected BaseCalculationEvent(@Nonnull JsclOperation operation, @Nonnull String expression, long sequence) {
        this.operation = operation;
        this.expression = expression;
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return "BaseCalculationEvent{" +
                "operation=" + operation +
                ", expression='" + expression + '\'' +
                ", sequence=" + sequence +
                '}';
    }
}
