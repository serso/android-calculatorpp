package org.solovyev.android.calculator;

import jscl.math.Generic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.jscl.JsclOperation;

/**
* User: serso
* Date: 9/20/12
* Time: 7:28 PM
*/
public class CalculatorOutputImpl implements CalculatorOutput {

    @Nullable
    private Generic result;

    @NotNull
    private String stringResult;

    @NotNull
    private JsclOperation operation;

    private CalculatorOutputImpl(@NotNull String stringResult,
                                @NotNull JsclOperation operation,
                                @Nullable Generic result) {
        this.stringResult = stringResult;
        this.operation = operation;
        this.result = result;
    }

    @NotNull
    public static CalculatorOutput newOutput(@NotNull String stringResult,
                                             @NotNull JsclOperation operation,
                                             @NotNull Generic result) {
        return new CalculatorOutputImpl(stringResult, operation, result);
    }

    @NotNull
    public static CalculatorOutput newEmptyOutput(@NotNull JsclOperation operation) {
        return new CalculatorOutputImpl("", operation, null);
    }

    @Override
    @NotNull
    public String getStringResult() {
        return stringResult;
    }

    @Override
    @NotNull
    public JsclOperation getOperation() {
        return operation;
    }

    @Override
    @Nullable
    public Generic getResult() {
        return result;
    }
}
