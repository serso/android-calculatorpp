package org.solovyev.android.calculator;

import jscl.math.Generic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.jscl.JsclOperation;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 9:50 PM
 */
public interface CalculatorDisplayViewState {

    @NotNull
    String getText();

    int getSelection();

    @Nullable
    Generic getResult();

    boolean isValid();

    @Nullable
    String getErrorMessage();

    @NotNull
    JsclOperation getOperation();

    @Nullable
    String getStringResult();
}
