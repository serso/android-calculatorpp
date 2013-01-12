package org.solovyev.android.calculator;

import org.mockito.Mockito;
import org.solovyev.android.calculator.external.CalculatorExternalListenersContainer;
import org.solovyev.android.calculator.history.CalculatorHistory;
import org.solovyev.android.calculator.plot.CalculatorPlotter;

/**
 * User: serso
 * Date: 10/7/12
 * Time: 6:30 PM
 */
public class AbstractCalculatorTest {

    protected void setUp() throws Exception {
		Locator.getInstance().init(new CalculatorImpl(), CalculatorTestUtils.newCalculatorEngine(), Mockito.mock(CalculatorClipboard.class), Mockito.mock(CalculatorNotifier.class), Mockito.mock(CalculatorHistory.class), new SystemOutCalculatorLogger(), Mockito.mock(CalculatorPreferenceService.class), Mockito.mock(CalculatorKeyboard.class), Mockito.mock(CalculatorExternalListenersContainer.class), Mockito.mock(CalculatorPlotter.class));
		Locator.getInstance().getEngine().init();
    }

}
