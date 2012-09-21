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
    private final CalculatorDisplay calculatorDisplay = new CalculatorDisplayImpl();

    @NotNull
    private final Calculator calculator = new CalculatorImpl();

    @NotNull
    private final CalculatorEditor calculatorEditor = new CalculatorEditorImpl(calculator);

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

    @NotNull
    @Override
    public Calculator getCalculator() {
        return this.calculator;
    }

    @Override
    public void setCalculatorEngine(@NotNull JCalculatorEngine calculatorEngine) {
        this.calculatorEngine = calculatorEngine;
    }

    @Override
    @NotNull
    public CalculatorDisplay getCalculatorDisplay() {
        return calculatorDisplay;
    }

    @NotNull
    @Override
    public CalculatorEditor getCalculatorEditor() {
        return calculatorEditor;
    }
}
