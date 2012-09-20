package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 12:45
 */
public class CalculatorLocatorImpl implements CalculatorLocator {

    @NotNull
    private JCalculatorEngine calculatorEngine;

    @NotNull
    private static final CalculatorLocator instance = new CalculatorLocatorImpl();

    private CalculatorLocatorImpl() {
    }

    @NotNull
    public static CalculatorLocator getInstance() {
        return instance;
    }

    @NotNull
    @Override
    public JCalculatorEngine getCalculatorEngine() {
        return calculatorEngine;
    }

    @Override
    public void setCalculatorEngine(@NotNull JCalculatorEngine calculatorEngine) {
        this.calculatorEngine = calculatorEngine;
    }
}
