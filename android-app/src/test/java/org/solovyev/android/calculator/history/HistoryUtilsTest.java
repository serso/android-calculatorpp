/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import junit.framework.Assert;
import org.junit.Test;
import org.solovyev.android.calculator.CalculatorDisplayViewState;
import org.solovyev.android.calculator.CalculatorDisplayViewStateImpl;
import org.solovyev.android.calculator.CalculatorEditorViewState;
import org.solovyev.android.calculator.CalculatorEditorViewStateImpl;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.common.equals.CollectionEqualizer;
import org.solovyev.common.Objects;
import org.solovyev.common.history.HistoryHelper;
import org.solovyev.common.history.SimpleHistoryHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: serso
 * Date: 12/17/11
 * Time: 10:01 PM
 */
public class HistoryUtilsTest {

	@Test
	public void testFromXml() throws Exception {

	}

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
	public void testToXml() throws Exception {
		final Date date = new Date(100000000);

		HistoryHelper<CalculatorHistoryState> history = SimpleHistoryHelper.newInstance();

		CalculatorDisplayViewState calculatorDisplay = CalculatorDisplayViewStateImpl.newErrorState(JsclOperation.simplify, "Error");

		CalculatorEditorViewState calculatorEditor = CalculatorEditorViewStateImpl.newInstance("1+1", 3);

		CalculatorHistoryState state = CalculatorHistoryState.newInstance(calculatorEditor, calculatorDisplay);
		state.setTime(date.getTime());
		history.addState(state);

		Assert.assertEquals(emptyHistory, HistoryUtils.toXml(history.getStates()));


		state.setSaved(true);

		Assert.assertEquals(toXml1, HistoryUtils.toXml(history.getStates()));

        calculatorDisplay = CalculatorDisplayViewStateImpl.newValidState(JsclOperation.numeric, null, "5/6", 3);

		calculatorEditor = CalculatorEditorViewStateImpl.newInstance("5/6", 2);

		state = CalculatorHistoryState.newInstance(calculatorEditor, calculatorDisplay);
		state.setSaved(true);
		state.setTime(date.getTime());
		history.addState(state);

        calculatorDisplay = CalculatorDisplayViewStateImpl.newErrorState(JsclOperation.elementary, "Error");

        calculatorEditor = CalculatorEditorViewStateImpl.newInstance("", 1);

		state = CalculatorHistoryState.newInstance(calculatorEditor, calculatorDisplay);
		state.setSaved(true);
		state.setTime(date.getTime());
		history.addState(state);

        calculatorDisplay = CalculatorDisplayViewStateImpl.newValidState(JsclOperation.numeric, null, "4+5/35sin(41)+dfdsfsdfs", 1);

        calculatorEditor = CalculatorEditorViewStateImpl.newInstance("4+5/35sin(41)+dfdsfsdfs", 0);

		state = CalculatorHistoryState.newInstance(calculatorEditor, calculatorDisplay);
		state.setSaved(true);
		state.setTime(date.getTime());
		history.addState(state);

		String xml = HistoryUtils.toXml(history.getStates());
		Assert.assertEquals(toXml2, xml);

		final List<CalculatorHistoryState> fromXml = new ArrayList<CalculatorHistoryState>();
		final HistoryHelper<CalculatorHistoryState> historyFromXml = SimpleHistoryHelper.newInstance();
		HistoryUtils.fromXml(xml, fromXml);
		for (CalculatorHistoryState historyState : fromXml) {
			historyFromXml.addState(historyState);
		}

		Assert.assertEquals(history.getStates().size(), historyFromXml.getStates().size());

		for (CalculatorHistoryState historyState : history.getStates()) {
			historyState.setId(0);
			historyState.setSaved(true);
		}
		for (CalculatorHistoryState historyState : historyFromXml.getStates()) {
			historyState.setId(0);
			historyState.setSaved(true);
		}
		Assert.assertTrue(Objects.areEqual(history.getStates(), historyFromXml.getStates(), new CollectionEqualizer<CalculatorHistoryState>(null)));
	}
}
