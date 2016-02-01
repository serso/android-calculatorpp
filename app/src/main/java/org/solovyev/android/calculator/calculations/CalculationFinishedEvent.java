package org.solovyev.android.calculator.calculations;

import jscl.math.Generic;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.common.msg.Message;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public final class CalculationFinishedEvent extends BaseCalculationEvent {
    @Nullable
    public final Generic result;
    @Nonnull
    public final String stringResult;
    @Nonnull
    public final List<Message> messages;

    public CalculationFinishedEvent(@Nonnull JsclOperation operation, @Nonnull String expression, long sequence) {
        super(operation, expression, sequence);
        result = null;
        stringResult = "";
        messages = Collections.emptyList();
    }

    public CalculationFinishedEvent(@Nonnull JsclOperation operation, @Nonnull String expression, long sequence, @Nullable Generic result, @Nonnull String stringResult, @Nonnull List<Message> messages) {
        super(operation, expression, sequence);
        this.result = result;
        this.stringResult = stringResult;
        this.messages = messages;
    }
}
