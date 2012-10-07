package org.solovyev.android.calculator;

import jscl.JsclMathEngine;
import org.jetbrains.annotations.NotNull;
import org.mockito.Mockito;
import org.solovyev.android.calculator.history.CalculatorHistory;

/**
 * User: serso
 * Date: 10/7/12
 * Time: 6:30 PM
 */
public class AbstractCalculatorTest {

    protected static void staticSetUp() throws Exception {
        CalculatorLocatorImpl.getInstance().init(new CalculatorImpl(), newCalculatorEngine(), Mockito.mock(CalculatorClipboard.class), Mockito.mock(CalculatorNotifier.class), Mockito.mock(CalculatorHistory.class));
    }

    protected void setUp() throws Exception {
        CalculatorLocatorImpl.getInstance().init(new CalculatorImpl(), newCalculatorEngine(), Mockito.mock(CalculatorClipboard.class), Mockito.mock(CalculatorNotifier.class), Mockito.mock(CalculatorHistory.class));
    }

    @NotNull
    private static CalculatorEngineImpl newCalculatorEngine() {
        final CalculatorMathRegistry varsRegistry = Mockito.mock(CalculatorMathRegistry.class);
        //Mockito.when(varsRegistry.get())

        final CalculatorEngineImpl result = new CalculatorEngineImpl(JsclMathEngine.getInstance(), varsRegistry, Mockito.mock(CalculatorMathRegistry.class), Mockito.mock(CalculatorMathRegistry.class), Mockito.mock(CalculatorMathRegistry.class), null);
        result.init();
        return result;
    }
}
