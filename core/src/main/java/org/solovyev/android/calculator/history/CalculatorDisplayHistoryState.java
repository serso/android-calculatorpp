/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.history;

import jscl.math.Generic;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Transient;
import org.solovyev.android.calculator.CalculatorDisplay;
import org.solovyev.android.calculator.CalculatorDisplayViewState;
import org.solovyev.android.calculator.CalculatorDisplayViewStateImpl;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.common.text.Strings;

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
	@Nonnull
	private EditorHistoryState editorState;

	@Element
	@Nonnull
	private JsclOperation jsclOperation;

	@Transient
	@Nullable
	private Generic genericResult;

	private CalculatorDisplayHistoryState() {
		// for xml
	}

	@Nonnull
	public static CalculatorDisplayHistoryState newInstance(@Nonnull CalculatorDisplayViewState viewState) {
		final CalculatorDisplayHistoryState result = new CalculatorDisplayHistoryState();

		result.editorState = EditorHistoryState.newInstance(viewState);

		result.valid = viewState.isValid();
		result.jsclOperation = viewState.getOperation();
		result.genericResult = viewState.getResult();
		result.errorMessage = viewState.getErrorMessage();

		return result;
	}

	public void setValuesFromHistory(@Nonnull CalculatorDisplay display) {
		if (this.isValid()) {
			display.setViewState(CalculatorDisplayViewStateImpl.newValidState(this.getJsclOperation(), this.getGenericResult(), Strings.getNotEmpty(this.getEditorState().getText(), ""), this.getEditorState().getCursorPosition()));
		} else {
			display.setViewState(CalculatorDisplayViewStateImpl.newErrorState(this.getJsclOperation(), Strings.getNotEmpty(this.getErrorMessage(), "")));
		}
	}


	public boolean isValid() {
		return valid;
	}

	@Nonnull
	public EditorHistoryState getEditorState() {
		return editorState;
	}

	@Nonnull
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
