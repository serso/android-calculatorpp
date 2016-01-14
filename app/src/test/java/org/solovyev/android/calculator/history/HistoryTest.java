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

import android.content.SharedPreferences;
import android.os.Handler;

import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.solovyev.android.CalculatorTestRunner;
import org.solovyev.android.calculator.BuildConfig;
import org.solovyev.android.calculator.DisplayState;
import org.solovyev.android.calculator.Editor;
import org.solovyev.android.calculator.EditorState;

import java.util.List;
import java.util.concurrent.Executor;

import javax.annotation.Nonnull;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@Config(constants = BuildConfig.class, sdk = CalculatorTestRunner.SUPPORTED_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class HistoryTest {

    private History history;

    @Before
    public void setUp() throws Exception {
        history = new History(Mockito.mock(Bus.class), Mockito.mock(Executor.class));
        history.handler = Mockito.mock(Handler.class);
        history.preferences = Mockito.mock(SharedPreferences.class);
        history.editor = Mockito.mock(Editor.class);
        history.application = RuntimeEnvironment.application;
    }

    @Test
    public void testGetStates() throws Exception {
        addState("1");
        addState("12");
        addState("123");
        addState("123+");
        addState("123+3");
        addState("");
        addState("2");
        addState("23");
        addState("235");
        addState("2355");
        addState("235");
        addState("2354");
        addState("23547");

        final List<HistoryState> states = history.getRecent();
        assertEquals(2, states.size());
        assertEquals("23547", states.get(0).editor.getTextString());
        assertEquals("123+3", states.get(1).editor.getTextString());
    }

    @Test
    public void testRecentHistoryShouldNotContainEmptyStates() throws Exception {
        addState("");
        addState("1");
        addState("12");
        addState("");
        addState("");
        addState("34");
        addState("");

        final List<HistoryState> states = history.getRecent();
        assertEquals(2, states.size());
        assertEquals("34", states.get(0).editor.getTextString());
        assertEquals("12", states.get(1).editor.getTextString());
    }

    private void addState(@Nonnull String text) {
        history.addRecent(HistoryState.newBuilder(EditorState.create(text, 3), DisplayState.empty()));
    }

    private static final String oldXml1 = "<history>\n" +
            "   <historyItems class=\"java.util.ArrayList\">\n" +
            "      <calculatorHistoryState>\n" +
            "         <time>100000000</time>\n" +
            "         <editorState>\n" +
            "            <cursorPosition>3</cursorPosition>\n" +
            "            <text>1+1</text>\n" +
            "         </editorState>\n" +
            "         <displayState>\n" +
            "            <editorState>\n" +
            "               <cursorPosition>0</cursorPosition>\n" +
            "               <text>Error</text>\n" +
            "            </editorState>\n" +
            "            <jsclOperation>simplify</jsclOperation>\n" +
            "         </displayState>\n" +
            "      </calculatorHistoryState>\n" +
            "   </historyItems>\n" +
            "</history>";
    private static final String oldXml2 = "<history>\n" +
            "   <historyItems class=\"java.util.ArrayList\">\n" +
            "      <calculatorHistoryState>\n" +
            "         <time>100000000</time>\n" +
            "         <comment>boom</comment>\n" +
            "         <editorState>\n" +
            "            <cursorPosition>3</cursorPosition>\n" +
            "            <text>1+11</text>\n" +
            "         </editorState>\n" +
            "         <displayState>\n" +
            "            <editorState>\n" +
            "               <cursorPosition>0</cursorPosition>\n" +
            "               <text>Error</text>\n" +
            "            </editorState>\n" +
            "            <jsclOperation>simplify</jsclOperation>\n" +
            "         </displayState>\n" +
            "      </calculatorHistoryState>\n" +
            "      <calculatorHistoryState>\n" +
            "         <time>100000000</time>\n" +
            "         <editorState>\n" +
            "            <cursorPosition>2</cursorPosition>\n" +
            "            <text>5/6</text>\n" +
            "         </editorState>\n" +
            "         <displayState>\n" +
            "            <editorState>\n" +
            "               <cursorPosition>3</cursorPosition>\n" +
            "               <text>5/6</text>\n" +
            "            </editorState>\n" +
            "            <jsclOperation>numeric</jsclOperation>\n" +
            "         </displayState>\n" +
            "      </calculatorHistoryState>\n" +
            "      <calculatorHistoryState>\n" +
            "         <time>100000000</time>\n" +
            "         <editorState>\n" +
            "            <cursorPosition>1</cursorPosition>\n" +
            "            <text></text>\n" +
            "         </editorState>\n" +
            "         <displayState>\n" +
            "            <editorState>\n" +
            "               <cursorPosition>0</cursorPosition>\n" +
            "               <text>Error</text>\n" +
            "            </editorState>\n" +
            "            <jsclOperation>elementary</jsclOperation>\n" +
            "         </displayState>\n" +
            "      </calculatorHistoryState>\n" +
            "      <calculatorHistoryState>\n" +
            "         <time>1</time>\n" +
            "         <editorState>\n" +
            "            <cursorPosition>0</cursorPosition>\n" +
            "            <text>4+5/35sin(41)+dfdsfsdfs</text>\n" +
            "         </editorState>\n" +
            "         <displayState>\n" +
            "            <editorState>\n" +
            "               <cursorPosition>1</cursorPosition>\n" +
            "               <text>4+5/35sin(41)+dfdsfsdfs</text>\n" +
            "            </editorState>\n" +
            "            <jsclOperation>numeric</jsclOperation>\n" +
            "         </displayState>\n" +
            "      </calculatorHistoryState>\n" +
            "   </historyItems>\n" +
            "</history>";

    @Test
    public void testShouldConvertOldHistory() throws Exception {
        List<HistoryState> states = History.convertOldHistory(oldXml1);
        assertNotNull(states);
        assertEquals(1, states.size());

        HistoryState state = states.get(0);
        assertEquals(100000000, state.time);
        assertEquals("", state.comment);
        assertEquals("1+1", state.editor.getTextString());
        assertEquals(3, state.editor.selection);
        assertEquals("Error", state.display.text);
        assertEquals(true, state.display.valid);
        assertNull(state.display.getResult());

        states = History.convertOldHistory(oldXml2);
        assertNotNull(states);
        assertEquals(4, states.size());

        state = states.get(0);
        assertEquals(100000000, state.time);
        assertEquals("boom", state.comment);
        assertEquals("1+11", state.editor.getTextString());
        assertEquals(3, state.editor.selection);
        assertEquals("Error", state.display.text);
        assertEquals(true, state.display.valid);
        assertNull(state.display.getResult());

        state = states.get(3);
        assertEquals(1, state.time);
        assertEquals("", state.comment);
        assertEquals("4+5/35sin(41)+dfdsfsdfs", state.editor.getTextString());
        assertEquals(0, state.editor.selection);
        assertEquals("4+5/35sin(41)+dfdsfsdfs", state.display.text);
        assertEquals(true, state.display.valid);
        assertNull(state.display.getResult());
    }
}
