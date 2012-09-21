package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.jscl.JsclOperation;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 10:01 PM
 */
public class CalculatorEvaluationEventDataImpl implements CalculatorEvaluationEventData {

    @NotNull
    private final CalculatorEventData calculatorEventData;

    @NotNull
    private final JsclOperation operation;

    @NotNull
    private final String expression;

    public CalculatorEvaluationEventDataImpl(@NotNull CalculatorEventData calculatorEventData,
                                             @NotNull JsclOperation operation,
                                             @NotNull String expression) {
        this.calculatorEventData = calculatorEventData;
        this.operation = operation;
        this.expression = expression;
    }

    @NotNull
    @Override
    public JsclOperation getOperation() {
        return this.operation;
    }

    @NotNull
    @Override
    public String getExpression() {
        return this.expression;
    }

    @Override
    public long getEventId() {
        return calculatorEventData.getEventId();
    }

    @Override
    @Nullable
    public Long getSequenceId() {
        return calculatorEventData.getSequenceId();
    }

    @Override
    public boolean isAfter(@NotNull CalculatorEventDataId that) {
        return calculatorEventData.isAfter(that);
    }

    @Override
    public boolean isSameSequence(@NotNull CalculatorEventDataId that) {
        return this.calculatorEventData.isSameSequence(that);
    }
}
