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

import static org.mockito.Mockito.doAnswer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.solovyev.android.calculator.calculations.CalculationFinishedEvent;

@Config(constants = BuildConfig.class)
@RunWith(RobolectricTestRunner.class)
public class CalculatorTest extends BaseCalculatorTest {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Object[] args = invocationOnMock.getArguments();
                final CalculationFinishedEvent e = (CalculationFinishedEvent) args[0];
                calculator.updateAnsVariable(e.stringResult);
                return null;
            }
        }).when(bus).post(anyFinishedEvent());
    }

    @Test
    public void testAnsVariable() throws Exception {
        assertEval("2", "2");
        assertEval("2", "2");
        assertEval("2", "ans");
        assertEval("4", "ans^2");
        assertEval("16", "ans^2");
        assertEval("0", "0");
        assertEval("0", "ans");
        assertEval("3", "3");
        assertEval("9", "ans*ans");
        assertError("ans*an");
        assertEval("81", "ans*ans");
    }

}
