/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import org.mockito.Mockito;
import org.solovyev.android.calculator.history.History;
import org.solovyev.android.calculator.plot.CalculatorPlotter;

/**
 * User: serso
 * Date: 10/7/12
 * Time: 6:30 PM
 */
public class AbstractCalculatorTest {

    protected void setUp() throws Exception {
        Locator.getInstance().init(new CalculatorImpl(), CalculatorTestUtils.newCalculatorEngine(), Mockito.mock(CalculatorClipboard.class), Mockito.mock(CalculatorNotifier.class), Mockito.mock(History.class), new SystemOutCalculatorLogger(), Mockito.mock(CalculatorPreferenceService.class), Mockito.mock(CalculatorKeyboard.class), Mockito.mock(CalculatorPlotter.class), null);
        Locator.getInstance().getEngine().init();
    }

}
