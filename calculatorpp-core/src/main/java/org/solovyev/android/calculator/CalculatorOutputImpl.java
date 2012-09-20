package org.solovyev.android.calculator;

import jscl.math.Generic;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.jscl.JsclOperation;

/**
* User: serso
* Date: 9/20/12
* Time: 7:28 PM
*/
public class CalculatorOutputImpl implements CalculatorOutput {

    @NotNull
    private Generic result;

    @NotNull
    private String stringResult;

    @NotNull
    private JsclOperation operation;

    public CalculatorOutputImpl(@NotNull String stringResult, @NotNull JsclOperation operation, @NotNull Generic result) {
        this.stringResult = stringResult;
        this.operation = operation;
        this.result = result;
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
    @NotNull
    public Generic getResult() {
        return result;
    }
}
