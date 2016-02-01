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

import android.content.SharedPreferences;
import com.squareup.otto.Bus;
import org.solovyev.android.calculator.plot.CalculatorPlotter;

import java.util.concurrent.Executor;

import static org.mockito.Mockito.mock;

/**
 * User: serso
 * Date: 10/7/12
 * Time: 6:30 PM
 */
public class AbstractCalculatorTest {

    protected void setUp() throws Exception {
        Locator.getInstance().init(new Calculator(mock(SharedPreferences.class), mock(Bus.class), mock(Executor.class), mock(Executor.class)), CalculatorTestUtils.newCalculatorEngine(), mock(Keyboard.class), mock(CalculatorPlotter.class));
        Locator.getInstance().getEngine().init(new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        });
    }

}
