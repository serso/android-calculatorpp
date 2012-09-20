/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.history;

import jscl.math.Generic;
import junit.framework.Assert;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.solovyev.android.calculator.Editor;
import org.solovyev.android.calculator.ICalculatorDisplay;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.common.equals.CollectionEqualizer;
import org.solovyev.common.equals.EqualsTool;
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
			"               <cursorPosition>1</cursorPosition>\n" +
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
			"               <cursorPosition>1</cursorPosition>\n" +
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
			"            <text>null</text>\n" +
			"         </editorState>\n" +
			"         <displayState>\n" +
			"            <editorState>\n" +
			"               <cursorPosition>1</cursorPosition>\n" +
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

		HistoryHelper<CalculatorHistoryState> history = new SimpleHistoryHelper<CalculatorHistoryState>();

		ICalculatorDisplay calculatorDisplay = new TestCalculatorDisplay();
		calculatorDisplay.setErrorMessage("error_msg1");
		calculatorDisplay.setText("Error");
		calculatorDisplay.setSelection(1);
		calculatorDisplay.setJsclOperation(JsclOperation.simplify);

		Editor calculatorEditor = new TestEditor();
		calculatorEditor.setSelection(3);
		calculatorEditor.setText("1+1");

		CalculatorHistoryState state = CalculatorHistoryState.newInstance(calculatorEditor, calculatorDisplay);
		state.setTime(date.getTime());
		history.addState(state);

		Assert.assertEquals(emptyHistory, HistoryUtils.toXml(history.getStates()));


		state.setSaved(true);

		Assert.assertEquals(toXml1, HistoryUtils.toXml(history.getStates()));

		calculatorDisplay = new TestCalculatorDisplay();
		calculatorDisplay.setErrorMessage(null);
		calculatorDisplay.setText("5/6");
		calculatorDisplay.setSelection(3);
		calculatorDisplay.setJsclOperation(JsclOperation.numeric);

		calculatorEditor = new TestEditor();
		calculatorEditor.setSelection(2);
		calculatorEditor.setText("5/6");

		state = CalculatorHistoryState.newInstance(calculatorEditor, calculatorDisplay);
		state.setSaved(true);
		state.setTime(date.getTime());
		history.addState(state);

		calculatorDisplay = new TestCalculatorDisplay();
		calculatorDisplay.setErrorMessage("error_msg2");
		calculatorDisplay.setText("Error");
		calculatorDisplay.setSelection(1);
		calculatorDisplay.setJsclOperation(JsclOperation.elementary);

		calculatorEditor = new TestEditor();
		calculatorEditor.setSelection(1);
		calculatorEditor.setText(null);

		state = CalculatorHistoryState.newInstance(calculatorEditor, calculatorDisplay);
		state.setSaved(true);
		state.setTime(date.getTime());
		history.addState(state);

		calculatorDisplay = new TestCalculatorDisplay();
		calculatorDisplay.setErrorMessage(null);
		calculatorDisplay.setText("4+5/35sin(41)+dfdsfsdfs");
		calculatorDisplay.setSelection(1);
		calculatorDisplay.setJsclOperation(JsclOperation.numeric);

		calculatorEditor = new TestEditor();
		calculatorEditor.setSelection(0);
		calculatorEditor.setText("4+5/35sin(41)+dfdsfsdfs");

		state = CalculatorHistoryState.newInstance(calculatorEditor, calculatorDisplay);
		state.setSaved(true);
		state.setTime(date.getTime());
		history.addState(state);

		String xml = HistoryUtils.toXml(history.getStates());
		Assert.assertEquals(toXml2, xml);

		final List<CalculatorHistoryState> fromXml = new ArrayList<CalculatorHistoryState>();
		final HistoryHelper<CalculatorHistoryState> historyFromXml = new SimpleHistoryHelper<CalculatorHistoryState>();
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
		Assert.assertTrue(EqualsTool.areEqual(history.getStates(), historyFromXml.getStates(), new CollectionEqualizer<CalculatorHistoryState>(null)));
	}


	private static class TestCalculatorDisplay implements ICalculatorDisplay {

		@NotNull
		private final TestEditor testEditor = new TestEditor();

		private boolean valid;

		private String errorMessage;

		private JsclOperation operation;

		private Generic genericResult;

		@Override
		public boolean isValid() {
			return this.valid;
		}

		@Override
		public void setValid(boolean valid) {
			this.valid = valid;
		}

		@Override
		public String getErrorMessage() {
			return this.errorMessage;
		}

		@Override
		public void setErrorMessage(@Nullable String errorMessage) {
			this.errorMessage = errorMessage;
		}

		@Override
		public void setJsclOperation(@NotNull JsclOperation jsclOperation) {
			this.operation = jsclOperation;
		}

		@NotNull
		@Override
		public JsclOperation getJsclOperation() {
			return this.operation;
		}

		@Override
		public void setGenericResult(@Nullable Generic genericResult) {
			this.genericResult = genericResult;
		}

		@Override
		public Generic getGenericResult() {
			return this.genericResult;
		}

		@Override
		public CharSequence getText() {
			return this.testEditor.getText();
		}

		@Override
		public void setText(@Nullable CharSequence text) {
			this.testEditor.setText(text);
		}

		@Override
		public int getSelection() {
			return this.testEditor.getSelection();
		}

		@Override
		public void setSelection(int selection) {
			this.testEditor.setSelection(selection);
		}
	}

	private static class TestEditor implements Editor {

		@Nullable
		private CharSequence text;

		private int selection;

		@Nullable
		@Override
		public CharSequence getText() {
			return this.text;
		}

		@Override
		public void setText(@Nullable CharSequence text) {
			this.text = text;
		}

		@Override
		public int getSelection() {
			return this.selection;
		}

		@Override
		public void setSelection(int selection) {
			this.selection = selection;
		}
	}
}
