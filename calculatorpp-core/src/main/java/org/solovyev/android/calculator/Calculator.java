package org.solovyev.android.calculator;

import jscl.NumeralBase;
import jscl.math.Generic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.history.CalculatorHistoryState;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.common.history.HistoryControl;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 16:38
 */
public interface Calculator extends CalculatorEventContainer, HistoryControl<CalculatorHistoryState> {

    void init();

    /*
    **********************************************************************
    *
    *                           CALCULATIONS
    *
    **********************************************************************
    */

    void evaluate();

    void simplify();

    @NotNull
    CalculatorEventDataId evaluate(@NotNull JsclOperation operation,
                                   @NotNull String expression);

    @NotNull
    CalculatorEventDataId evaluate(@NotNull JsclOperation operation,
                                   @NotNull String expression,
                                   @NotNull Long sequenceId);

    @NotNull
    CalculatorEventDataId convert(@NotNull Generic generic, @NotNull NumeralBase to);

    /*
    **********************************************************************
    *
    *                           EVENTS
    *
    **********************************************************************
    */
    @NotNull
    CalculatorEventDataId fireCalculatorEvent(@NotNull CalculatorEventType calculatorEventType, @Nullable Object data);

    @NotNull
    CalculatorEventDataId fireCalculatorEvent(@NotNull CalculatorEventType calculatorEventType, @Nullable Object data, @NotNull Long sequenceId);
}
