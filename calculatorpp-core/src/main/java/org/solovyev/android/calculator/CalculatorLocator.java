package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 12:45
 */
public interface CalculatorLocator {

    @NotNull
    JCalculatorEngine getCalculatorEngine();

    void setCalculatorEngine(@NotNull JCalculatorEngine calculatorEngine);
}
