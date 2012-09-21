package org.solovyev.android.calculator;

import jscl.NumeralBase;
import jscl.math.Generic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.jscl.JsclOperation;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 16:38
 */
public interface Calculator extends CalculatorEventContainer {

    @NotNull
    CalculatorEventDataId createFirstEventDataId();

    @NotNull
    CalculatorEventDataId evaluate(@NotNull JsclOperation operation,
                                   @NotNull String expression);

    @NotNull
    CalculatorEventDataId evaluate(@NotNull JsclOperation operation,
                                   @NotNull String expression,
                                   @NotNull Long sequenceId);

    @NotNull
    CalculatorEventDataId convert(@NotNull Generic generic, @NotNull NumeralBase to);

    @NotNull
    CalculatorEventDataId fireCalculatorEvent(@NotNull CalculatorEventType calculatorEventType, @Nullable Object data);

    @NotNull
    CalculatorEventDataId fireCalculatorEvent(@NotNull CalculatorEventType calculatorEventType, @Nullable Object data, @NotNull Long sequenceId);

}
