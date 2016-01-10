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
import org.junit.Test;
import org.solovyev.android.calculator.DisplayState;
import org.solovyev.android.calculator.EditorState;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.common.Objects;
import org.solovyev.common.equals.CollectionEqualizer;
import org.solovyev.common.history.HistoryHelper;
import org.solovyev.common.history.SimpleHistoryHelper;

import javax.annotation.Nonnull;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * User: serso
 * Date: 12/17/11
 * Time: 10:01 PM
 */
public class HistoryUtilsTest {

    private static final String emptyHistory = "<history>\n" +
            "   <historyItems class=\"java.util.ArrayList\"/>\n" +
            "</history>";
    private static final String toXml1 = "<history>\n" +
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
    private static final String toXml2 = "<history>\n" +
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
            "         <time>100000000</time>\n" +
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
    public void testFromXml() throws Exception {

    }

    @Test
    public void testToXml() throws Exception {
        final Date date = new Date(100000000);

        HistoryHelper<OldHistoryState> history = SimpleHistoryHelper.newInstance();

        DisplayState calculatorDisplay = DisplayState.createError(JsclOperation.simplify, "Error", EditorState.NO_SEQUENCE);

        EditorState calculatorEditor = EditorState.create("1+1", 3);

        OldHistoryState state = OldHistoryState.create(calculatorEditor, calculatorDisplay);
        state.setTime(date.getTime());
        history.addState(state);

        assertEquals(emptyHistory, createHistory(history).toXml());


        state.setSaved(true);

        assertEquals(toXml1, createHistory(history).toXml());

        calculatorDisplay = DisplayState.createValid(JsclOperation.numeric, null, "5/6", 3, EditorState.NO_SEQUENCE);

        calculatorEditor = EditorState.create("5/6", 2);

        state = OldHistoryState.create(calculatorEditor, calculatorDisplay);
        state.setSaved(true);
        state.setTime(date.getTime());
        history.addState(state);

        calculatorDisplay = DisplayState.createError(JsclOperation.elementary, "Error", EditorState.NO_SEQUENCE);

        calculatorEditor = EditorState.create("", 1);

        state = OldHistoryState.create(calculatorEditor, calculatorDisplay);
        state.setSaved(true);
        state.setTime(date.getTime());
        history.addState(state);

        calculatorDisplay = DisplayState.createValid(JsclOperation.numeric, null, "4+5/35sin(41)+dfdsfsdfs", 1, EditorState.NO_SEQUENCE);

        calculatorEditor = EditorState.create("4+5/35sin(41)+dfdsfsdfs", 0);

        state = OldHistoryState.create(calculatorEditor, calculatorDisplay);
        state.setSaved(true);
        state.setTime(date.getTime());
        history.addState(state);

        String xml = createHistory(history).toXml();
        assertEquals(toXml2, xml);

        final HistoryHelper<OldHistoryState> historyFromXml = SimpleHistoryHelper.newInstance();
        final OldHistory actual = OldHistory.fromXml(xml);
        for (OldHistoryState historyState : actual.getItems()) {
            historyFromXml.addState(historyState);
        }

        assertEquals(history.getStates().size(), historyFromXml.getStates().size());

        for (OldHistoryState historyState : history.getStates()) {
            historyState.setId(0);
            historyState.setSaved(true);
        }
        for (OldHistoryState historyState : historyFromXml.getStates()) {
            historyState.setId(0);
            historyState.setSaved(true);
        }
        Assert.assertTrue(Objects.areEqual(history.getStates(), historyFromXml.getStates(), new CollectionEqualizer<OldHistoryState>(null)));
    }

    @Nonnull
    private OldHistory createHistory(HistoryHelper<OldHistoryState> history) {
        final OldHistory result = new OldHistory();
        result.addAll(history.getStates());
        return result;
    }
}
