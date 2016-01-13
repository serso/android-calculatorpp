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

import com.squareup.otto.Bus;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.solovyev.android.CalculatorTestRunner;
import org.solovyev.android.calculator.CalculatorTestUtils;
import org.solovyev.android.calculator.DisplayState;
import org.solovyev.android.calculator.EditorState;

import java.util.List;
import java.util.concurrent.Executor;

import javax.annotation.Nonnull;

@RunWith(CalculatorTestRunner.class)
public class HistoryTest {

    @BeforeClass
    public static void setUp() throws Exception {
        CalculatorTestUtils.staticSetUp();
    }

    @Test
    public void testGetStates() throws Exception {
        History history = new History(Mockito.any(Bus.class), new Executor() {
            @Override
            public void execute(@Nonnull Runnable command) {
                command.run();
            }
        });

        addState(history, "1");
        addState(history, "12");
        addState(history, "123");
        addState(history, "123+");
        addState(history, "123+3");
        addState(history, "");
        addState(history, "2");
        addState(history, "23");
        addState(history, "235");
        addState(history, "2355");
        addState(history, "235");
        addState(history, "2354");
        addState(history, "23547");

        final List<HistoryState> states = history.getRecent();
        Assert.assertEquals(2, states.size());
        Assert.assertEquals("23547", states.get(1).editor.getTextString());
        Assert.assertEquals("123+3", states.get(0).editor.getTextString());
    }

    private void addState(@Nonnull History history, @Nonnull String text) {
        history.addRecent(HistoryState.newBuilder(EditorState.create(text, 3), DisplayState.empty()));
    }
}
