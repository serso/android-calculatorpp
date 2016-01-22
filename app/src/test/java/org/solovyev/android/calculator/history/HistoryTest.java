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
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.solovyev.android.CalculatorTestRunner;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.calculator.json.Json;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.solovyev.android.calculator.Engine.Preferences.groupingSeparator;

@Config(constants = BuildConfig.class, sdk = CalculatorTestRunner.SUPPORTED_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class HistoryTest {

    private History history;

    @Before
    public void setUp() throws Exception {
        history = new History();
        history.application = RuntimeEnvironment.application;
        history.bus = mock(Bus.class);
        history.handler = mock(Handler.class);
        history.preferences = mock(SharedPreferences.class);
        history.editor = mock(Editor.class);
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
        assertEquals(3, states.size());
        assertEquals("23547", states.get(0).editor.getTextString());
        // intermediate state
        assertEquals("235", states.get(1).editor.getTextString());
        assertEquals("123+3", states.get(2).editor.getTextString());
    }

    @Test
    public void testRecentHistoryShouldTakeIntoAccountGroupingSeparator() throws Exception {
        when(history.preferences.contains(eq(groupingSeparator.getKey()))).thenReturn(true);
        when(history.preferences.getString(eq(groupingSeparator.getKey()), anyString())).thenReturn(" ");
        addState("1");
        addState("12");
        addState("123");
        addState("1 234");
        addState("12 345");

        List<HistoryState> states = history.getRecent();
        assertEquals(1, states.size());
        assertEquals("12 345", states.get(0).editor.getTextString());
        history.clearRecent();

        when(history.preferences.getString(eq(groupingSeparator.getKey()), anyString())).thenReturn("'");
        addState("1");
        addState("12");
        addState("123");
        addState("1'234");
        addState("12'345");
        addState("12 345");

        states = history.getRecent();
        assertEquals(2, states.size());
        assertEquals("12 345", states.get(0).editor.getTextString());
        assertEquals("12'345", states.get(1).editor.getTextString());
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
        history.addRecent(HistoryState.builder(EditorState.create(text, 3), DisplayState.empty()).build());
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

    @Test
    public void testShouldAddStateIfEditorAndDisplayAreInSync() throws Exception {
        final EditorState editorState = EditorState.create("editor", 2);
        when(history.editor.getState()).thenReturn(editorState);

        final DisplayState displayState = DisplayState.createError(JsclOperation.numeric, "test", editorState.sequence);
        history.onDisplayChanged(new Display.ChangedEvent(DisplayState.empty(), displayState));

        final List<HistoryState> states = history.getRecent();
        assertEquals(1, states.size());
        assertSame(editorState, states.get(0).editor);
        assertSame(displayState, states.get(0).display);
    }

    @Test
    public void testShouldNotAddStateIfEditorAndDisplayAreOutOfSync() throws Exception {
        final EditorState editorState = EditorState.create("editor", 2);
        when(history.editor.getState()).thenReturn(editorState);

        final DisplayState displayState = DisplayState.createError(JsclOperation.numeric, "test", editorState.sequence - 1);
        history.onDisplayChanged(new Display.ChangedEvent(DisplayState.empty(), displayState));

        final List<HistoryState> states = history.getRecent();
        assertEquals(0, states.size());
    }

    @Test
    public void testShouldLoadStates() throws Exception {
        final List<HistoryState> states = Json.load(new File(HistoryTest.class.getResource("recent-history.json").getFile()), HistoryState.JSON_CREATOR);
        assertEquals(8, states.size());

        HistoryState state = states.get(0);
        assertEquals(1452770652381L, state.time);
        assertEquals("", state.comment);
        assertEquals("01 234 567 890 123 456 789", state.editor.getTextString());
        assertEquals(26, state.editor.selection);
        assertEquals("1 234 567 890 123 460 000", state.display.text);

        state = states.get(4);
        assertEquals(1452770626394L, state.time);
        assertEquals("", state.comment);
        assertEquals("985", state.editor.getTextString());
        assertEquals(3, state.editor.selection);
        assertEquals("985", state.display.text);

        state = states.get(7);
        assertEquals(1452770503823L, state.time);
        assertEquals("", state.comment);
        assertEquals("52", state.editor.getTextString());
        assertEquals(2, state.editor.selection);
        assertEquals("52", state.display.text);
    }
}
