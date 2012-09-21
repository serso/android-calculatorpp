package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 9/21/12
 * Time: 9:49 PM
 */
public interface CalculatorDisplayChangeEventData {

    @NotNull
    CalculatorDisplayViewState getOldState();

    @NotNull
    CalculatorDisplayViewState getNewState();
}
