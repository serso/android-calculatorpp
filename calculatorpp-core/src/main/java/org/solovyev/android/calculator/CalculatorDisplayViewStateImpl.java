package org.solovyev.android.calculator;

import jscl.math.Generic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.common.text.StringUtils;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 9:50 PM
 */
public class CalculatorDisplayViewStateImpl implements CalculatorDisplayViewState {

    @NotNull
    private JsclOperation operation = JsclOperation.numeric;

    @Nullable
    private Generic result;

    @Nullable
    private String stringResult = "";

    private boolean valid = true;

    @Nullable
    private String errorMessage;

    private int selection = 0;

    private CalculatorDisplayViewStateImpl() {
    }

    @NotNull
    public static CalculatorDisplayViewState newDefaultInstance() {
        return new CalculatorDisplayViewStateImpl();
    }

    @NotNull
    public static CalculatorDisplayViewState newErrorState(@NotNull JsclOperation operation,
                                                           @NotNull String errorMessage) {
        final CalculatorDisplayViewStateImpl calculatorDisplayState = new CalculatorDisplayViewStateImpl();
        calculatorDisplayState.valid = false;
        calculatorDisplayState.errorMessage = errorMessage;
        calculatorDisplayState.operation = operation;
        return calculatorDisplayState;
    }

    @NotNull
    public static CalculatorDisplayViewState newValidState(@NotNull JsclOperation operation,
                                                           @Nullable Generic result,
                                                           @NotNull String stringResult,
                                                           int selection) {
        final CalculatorDisplayViewStateImpl calculatorDisplayState = new CalculatorDisplayViewStateImpl();
        calculatorDisplayState.valid = true;
        calculatorDisplayState.result = result;
        calculatorDisplayState.stringResult = stringResult;
        calculatorDisplayState.operation = operation;
        calculatorDisplayState.selection = selection;

        return calculatorDisplayState;
    }

    @NotNull
    @Override
    public String getText() {
        return StringUtils.getNotEmpty(isValid() ? stringResult : errorMessage, "");
    }

    @Override
    public int getSelection() {
        return selection;
    }

    @Nullable
    @Override
    public Generic getResult() {
        return this.result;
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

    @Nullable
    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    @Nullable
    public String getStringResult() {
        return stringResult;
    }

    @NotNull
    @Override
    public JsclOperation getOperation() {
        return this.operation;
    }
}
