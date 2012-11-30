package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.jscl.JsclOperation;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 7:25 PM
 */
public interface CalculatorInput {

    @NotNull
    String getExpression();

    @NotNull
    JsclOperation getOperation();
}
