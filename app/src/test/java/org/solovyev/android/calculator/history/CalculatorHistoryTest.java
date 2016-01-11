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

package org.solovyev.android.calculator.history;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.solovyev.android.calculator.CalculatorTestUtils;
import org.solovyev.android.calculator.DisplayState;
import org.solovyev.android.calculator.EditorState;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * User: Solovyev_S
 * Date: 10.10.12
 * Time: 15:07
 */
public class CalculatorHistoryTest {

    @BeforeClass
    public static void setUp() throws Exception {
        CalculatorTestUtils.staticSetUp();
    }

    @Test
    public void testGetStates() throws Exception {
        CalculatorHistory calculatorHistory = new CalculatorHistory();

        addState(calculatorHistory, "1");
        addState(calculatorHistory, "12");
        addState(calculatorHistory, "123");
        addState(calculatorHistory, "123+");
        addState(calculatorHistory, "123+3");
        addState(calculatorHistory, "");
        addState(calculatorHistory, "2");
        addState(calculatorHistory, "23");
        addState(calculatorHistory, "235");
        addState(calculatorHistory, "2355");
        addState(calculatorHistory, "235");
        addState(calculatorHistory, "2354");
        addState(calculatorHistory, "23547");

        final List<HistoryState> states = calculatorHistory.getCurrentHistory();
        Assert.assertEquals(2, states.size());
        Assert.assertEquals("23547", states.get(1).getEditor().getTextString());
        Assert.assertEquals("123+3", states.get(0).getEditor().getTextString());
    }

    private void addState(@Nonnull CalculatorHistory calculatorHistory, @Nonnull String text) {
        calculatorHistory.addCurrentState(HistoryState.newBuilder(EditorState.create(text, 3), DisplayState.empty()));
    }
}
