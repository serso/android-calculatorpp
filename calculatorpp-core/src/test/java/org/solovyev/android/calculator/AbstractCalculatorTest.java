package org.solovyev.android.calculator;

import org.mockito.Mockito;
import org.solovyev.android.calculator.history.CalculatorHistory;

/**
 * User: serso
 * Date: 10/7/12
 * Time: 6:30 PM
 */
public class AbstractCalculatorTest {

    protected void setUp() throws Exception {
        CalculatorLocatorImpl.getInstance().init(new CalculatorImpl(), CalculatorTestUtils.newCalculatorEngine(), Mockito.mock(CalculatorClipboard.class), Mockito.mock(CalculatorNotifier.class), Mockito.mock(CalculatorHistory.class), new SystemOutCalculatorLogger());
        CalculatorLocatorImpl.getInstance().getEngine().init();
    }

}
