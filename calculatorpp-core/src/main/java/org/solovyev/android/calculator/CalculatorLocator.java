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

    @NotNull
    Calculator getCalculator();

    @NotNull
    CalculatorDisplay getCalculatorDisplay();

    @NotNull
    CalculatorEditor getCalculatorEditor();

    void setCalculatorEngine(@NotNull JCalculatorEngine calculatorEngine);

    @NotNull
    CalculatorKeyboard getCalculatorKeyboard();

    @NotNull
    CalculatorClipboard getCalculatorClipboard();

    void setCalculatorClipboard(@NotNull CalculatorClipboard calculatorClipboard);

    @NotNull
    CalculatorNotifier getCalculatorNotifier();

    void setCalculatorNotifier(@NotNull CalculatorNotifier calculatorNotifier);
}
