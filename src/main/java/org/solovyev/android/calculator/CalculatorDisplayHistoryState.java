/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.jscl.JsclOperation;

/**
 * User: serso
 * Date: 9/17/11
 * Time: 11:05 PM
 */
public class CalculatorDisplayHistoryState {

	private boolean valid = true;

	@Nullable
	private String errorMessage = null;

	@NotNull
	private EditorHistoryState editorHistoryState;

	@NotNull
	private JsclOperation jsclOperation;

	private CalculatorDisplayHistoryState() {
	}

	@NotNull
	public static CalculatorDisplayHistoryState newInstance(@NotNull CalculatorDisplay display) {
		final CalculatorDisplayHistoryState result = new CalculatorDisplayHistoryState();

		result.editorHistoryState = EditorHistoryState.newInstance(display);
		result.valid = display.isValid();
		result.jsclOperation = display.getJsclOperation();
		result.errorMessage = display.getErrorMessage();

		return result;
	}

	public boolean isValid() {
		return valid;
	}

	@NotNull
	public EditorHistoryState getEditorHistoryState() {
		return editorHistoryState;
	}

	@NotNull
	public JsclOperation getJsclOperation() {
		return jsclOperation;
	}

	@Nullable
	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CalculatorDisplayHistoryState that = (CalculatorDisplayHistoryState) o;

		if (valid != that.valid) return false;
		if (!editorHistoryState.equals(that.editorHistoryState)) return false;
		if (errorMessage != null ? !errorMessage.equals(that.errorMessage) : that.errorMessage != null) return false;
		if (jsclOperation != that.jsclOperation) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (valid ? 1 : 0);
		result = 31 * result + (errorMessage != null ? errorMessage.hashCode() : 0);
		result = 31 * result + editorHistoryState.hashCode();
		result = 31 * result + jsclOperation.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "CalculatorDisplayHistoryState{" +
				"valid=" + valid +
				", errorMessage='" + errorMessage + '\'' +
				", editorHistoryState=" + editorHistoryState +
				", jsclOperation=" + jsclOperation +
				'}';
	}
}
