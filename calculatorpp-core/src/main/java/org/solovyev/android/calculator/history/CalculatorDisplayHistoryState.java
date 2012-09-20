/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.history;

import jscl.math.Generic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Transient;
import org.solovyev.android.calculator.JCalculatorDisplay;
import org.solovyev.android.calculator.jscl.JsclOperation;

/**
 * User: serso
 * Date: 9/17/11
 * Time: 11:05 PM
 */

@Root
public class CalculatorDisplayHistoryState implements Cloneable {

	@Transient
	private boolean valid = true;

	@Transient
	@Nullable
	private String errorMessage = null;

	@Element
	@NotNull
	private EditorHistoryState editorState;

	@Element
	@NotNull
	private JsclOperation jsclOperation;

	@Transient
	@Nullable
	private Generic genericResult;

	private CalculatorDisplayHistoryState() {
		// for xml
	}

	@NotNull
	public static CalculatorDisplayHistoryState newInstance(@NotNull JCalculatorDisplay display) {
		final CalculatorDisplayHistoryState result = new CalculatorDisplayHistoryState();

		result.editorState = EditorHistoryState.newInstance(display);
		result.valid = display.isValid();
		result.jsclOperation = display.getJsclOperation();
		result.genericResult = display.getGenericResult();
		result.errorMessage = display.getErrorMessage();

		return result;
	}

	public void setValuesFromHistory(@NotNull JCalculatorDisplay display) {
		this.getEditorState().setValuesFromHistory(display);
		display.setValid(this.isValid());
		display.setErrorMessage(this.getErrorMessage());
		display.setJsclOperation(this.getJsclOperation());
		display.setGenericResult(this.getGenericResult());
	}


	public boolean isValid() {
		return valid;
	}

	@NotNull
	public EditorHistoryState getEditorState() {
		return editorState;
	}

	@NotNull
	public JsclOperation getJsclOperation() {
		return jsclOperation;
	}

	@Nullable
	public String getErrorMessage() {
		return errorMessage;
	}

	@Nullable
	public Generic getGenericResult() {
		return genericResult;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CalculatorDisplayHistoryState that = (CalculatorDisplayHistoryState) o;

		if (!editorState.equals(that.editorState)) return false;
		if (jsclOperation != that.jsclOperation) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = editorState.hashCode();
		result = 31 * result + jsclOperation.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "CalculatorDisplayHistoryState{" +
				"valid=" + valid +
				", errorMessage='" + errorMessage + '\'' +
				", editorHistoryState=" + editorState +
				", jsclOperation=" + jsclOperation +
				'}';
	}

	@Override
	protected CalculatorDisplayHistoryState clone() {
		try {
			final CalculatorDisplayHistoryState clone = (CalculatorDisplayHistoryState) super.clone();

			clone.editorState = this.editorState.clone();

			return clone;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
