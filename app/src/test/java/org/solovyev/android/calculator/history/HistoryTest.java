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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.solovyev.android.calculator.Tests.sameThreadExecutor;
import static org.solovyev.android.calculator.jscl.JsclOperation.numeric;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import com.squareup.otto.Bus;
import dagger.Lazy;
import java.io.File;
import java.util.List;
import javax.annotation.Nonnull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.solovyev.android.calculator.Display;
import org.solovyev.android.calculator.DisplayState;
import org.solovyev.android.calculator.Editor;
import org.solovyev.android.calculator.EditorState;
import org.solovyev.android.calculator.Engine;
import org.solovyev.android.calculator.ErrorReporter;
import org.solovyev.android.calculator.json.Json;
import org.solovyev.android.io.FileSystem;

@RunWith(value = RobolectricTestRunner.class)
public class HistoryTest {

    private History history;

    @Before
    public void setUp() throws Exception {
        history = new History();
        history.backgroundThread = sameThreadExecutor();
        history.filesDir = new Lazy<File>() {
            @Override
            public File get() {
                return new File(".");
            }
        };
        history.application = RuntimeEnvironment.application;
        history.bus = mock(Bus.class);
        history.errorReporter = mock(ErrorReporter.class);
        history.fileSystem = mock(FileSystem.class);
        history.handler = new Handler(Looper.getMainLooper());
        history.preferences = mock(SharedPreferences.class);
        final SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        when(history.preferences.edit()).thenReturn(editor);
        when(editor.remove(anyString())).thenReturn(editor);
        history.editor = mock(Editor.class);
        history.setLoaded(true);
    }

    @After
    public void tearDown() throws Exception {
        history.getSavedHistoryFile().delete();
        history.getRecentHistoryFile().delete();
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
        when(history.preferences.contains(eq(Engine.Preferences.Output.separator.getKey()))).thenReturn(true);
        when(history.preferences.getString(eq(Engine.Preferences.Output.separator.getKey()), anyString())).thenReturn(" ");
        addState("1");
        addState("12");
        addState("123");
        addState("1 234");
        addState("12 345");

        List<HistoryState> states = history.getRecent();
        assertEquals(3, states.size());
        assertEquals("12 345", states.get(0).editor.getTextString());
        assertEquals("1 234", states.get(1).editor.getTextString());
        assertEquals("123", states.get(2).editor.getTextString());
        history.clearRecent();

        when(history.preferences.getString(eq(Engine.Preferences.Output.separator.getKey()), anyString())).thenReturn("'");
        addState("1");
        addState("12");
        addState("123");
        addState("1'234");
        addState("12'345");
        addState("12 345");

        states = history.getRecent();
        assertEquals(4, states.size());
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
        checkOldXml2States(states);
    }

    private void checkOldXml2States(List<HistoryState> states) {
        assertNotNull(states);
        assertEquals(4, states.size());

        HistoryState state = states.get(0);
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
    public void testShouldMigrateOldHistory() throws Exception {
        history.fileSystem = new FileSystem();
        when(history.preferences.getString(eq(History.OLD_HISTORY_PREFS_KEY), any())).thenReturn(oldXml2);
        history.init(sameThreadExecutor());
        Robolectric.flushForegroundThreadScheduler();
        checkOldXml2States(history.getSaved());
    }

    @Test
    public void testShouldWriteNewHistoryFile() throws Exception {
        history.fileSystem = mock(FileSystem.class);
        when(history.preferences.getString(eq(History.OLD_HISTORY_PREFS_KEY), any()))
            .thenReturn(oldXml1);
        history.init(sameThreadExecutor());
        Robolectric.flushForegroundThreadScheduler();
        verify(history.fileSystem).write(eq(history.getSavedHistoryFile()), eq(
            "[{\"e\":{\"t\":\"1+1\",\"s\":3},\"d\":{\"t\":\"Error\",\"v\":true},\"t\":100000000}]"));
    }

    @Test
    public void testShouldAddStateIfEditorAndDisplayAreInSync() throws Exception {
        final EditorState editorState = EditorState.create("editor", 2);
        when(history.editor.getState()).thenReturn(editorState);

        final DisplayState displayState = DisplayState.createError(numeric, "test", editorState.sequence);
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

        final DisplayState displayState = DisplayState.createError(numeric, "test", editorState.sequence - 1);
        history.onDisplayChanged(new Display.ChangedEvent(DisplayState.empty(), displayState));

        final List<HistoryState> states = history.getRecent();
        assertEquals(0, states.size());
    }

    @Test
    public void testShouldReportOnMigrateException() throws Exception {
        when(history.preferences.getString(eq(History.OLD_HISTORY_PREFS_KEY), any())).thenReturn(
            "boom");
        history.init(sameThreadExecutor());

        verify(history.errorReporter).onException(any(Throwable.class));
    }

    @Test
    public void testShouldNotRemoveOldHistoryOnError() throws Exception {
        when(history.preferences.getString(eq(History.OLD_HISTORY_PREFS_KEY), any())).thenReturn("boom");
        history.init(sameThreadExecutor());

        verify(history.preferences, never()).edit();
        verify(history.errorReporter).onException(any(Throwable.class));
    }

    @Test
    public void testShouldLoadStates() throws Exception {
        final List<HistoryState> states = Json.load(new File(HistoryTest.class.getResource("recent-history.json").getFile()),
            new FileSystem(), HistoryState.JSON_CREATOR);
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

    @Test
    public void testShouldClearSaved() throws Exception {
        history.updateSaved(HistoryState.builder(EditorState.create("text", 0),
            DisplayState.createValid(numeric, null, "result", 0)).build());
        Robolectric.flushForegroundThreadScheduler();
        assertTrue(!history.getSaved().isEmpty());

        // renew counter
        history.fileSystem = mock(FileSystem.class);
        history.clearSaved();
        Robolectric.flushForegroundThreadScheduler();

        assertTrue(history.getSaved().isEmpty());
        verify(history.fileSystem).writeSilently(eq(history.getSavedHistoryFile()), eq("[]"));
    }

    @Test
    public void testShouldClearRecent() throws Exception {
        history.addRecent(HistoryState.builder(EditorState.create("text", 0),
            DisplayState.createValid(numeric, null, "result", 0)).build());
        Robolectric.flushForegroundThreadScheduler();
        assertTrue(!history.getRecent().isEmpty());

        // renew counter
        history.fileSystem = mock(FileSystem.class);
        history.clearRecent();
        Robolectric.flushForegroundThreadScheduler();

        assertTrue(history.getRecent().isEmpty());
        verify(history.fileSystem).writeSilently(eq(history.getRecentHistoryFile()), eq("[]"));
    }

    @Test
    public void testShouldUpdateSaved() throws Exception {
        final HistoryState state = HistoryState.builder(EditorState.create("text", 0),
            DisplayState.createValid(numeric, null, "result", 0)).build();
        history.updateSaved(state);
        assertTrue(history.getSaved().size() == 1);
        assertEquals(state.time, history.getSaved().get(0).time);

        history.updateSaved(HistoryState.builder(state, false).withTime(10).build());
        assertTrue(history.getSaved().size() == 1);
        assertEquals(10, history.getSaved().get(0).time);
    }
}
