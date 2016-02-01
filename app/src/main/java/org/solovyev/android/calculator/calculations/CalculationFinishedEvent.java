package org.solovyev.android.calculator.calculations;

import jscl.math.Generic;
import org.solovyev.android.calculator.jscl.JsclOperation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class CalculationFinishedEvent extends BaseCalculationEvent {
    @Nullable
    public final Generic result;
    @Nonnull
    public final String stringResult;

    public CalculationFinishedEvent(@Nonnull JsclOperation operation, @Nonnull String expression, long sequence) {
        super(operation, expression, sequence);
        result = null;
        stringResult = "";
    }

    public CalculationFinishedEvent(@Nonnull JsclOperation operation, @Nonnull String expression, long sequence, @Nullable Generic result, @Nonnull String stringResult) {
        super(operation, expression, sequence);
        this.result = result;
        this.stringResult = stringResult;
    }
}
