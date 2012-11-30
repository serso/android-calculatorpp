package org.solovyev.android.calculator;

import jscl.math.Generic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.jscl.JsclOperation;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 7:29 PM
 */
public interface CalculatorOutput {

    @NotNull
    String getStringResult();

    @NotNull
    JsclOperation getOperation();


    // null in case of empty expression
    @Nullable
    Generic getResult();
}
