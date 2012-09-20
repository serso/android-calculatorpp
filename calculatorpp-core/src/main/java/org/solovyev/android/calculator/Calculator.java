package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.common.msg.MessageRegistry;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 16:38
 */
public interface Calculator extends CalculatorEventContainer {

    @NotNull
    CalculatorEventDataId createFirstEventDataId();

    void evaluate(@NotNull JsclOperation operation,
                  @NotNull String expression);

    @NotNull
    CalculatorEventDataId evaluate(@NotNull JsclOperation operation,
                                   @NotNull String expression,
                                   @Nullable MessageRegistry mr);
}
