package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.history.CalculatorHistory;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 12:45
 */
public interface CalculatorLocator {

    void init(@NotNull Calculator calculator,
              @NotNull CalculatorEngine engine,
              @NotNull CalculatorClipboard clipboard,
              @NotNull CalculatorNotifier notifier,
              @NotNull CalculatorHistory history,
              @NotNull CalculatorLogger logger);

    @NotNull
    Calculator getCalculator();

    @NotNull
    CalculatorEngine getEngine();

    @NotNull
    CalculatorDisplay getDisplay();

    @NotNull
    CalculatorEditor getEditor();

    @NotNull
    CalculatorKeyboard getKeyboard();

    @NotNull
    CalculatorClipboard getClipboard();

    @NotNull
    CalculatorNotifier getNotifier();

    @NotNull
    CalculatorHistory getHistory();

    @NotNull
    CalculatorLogger getLogger();
}
