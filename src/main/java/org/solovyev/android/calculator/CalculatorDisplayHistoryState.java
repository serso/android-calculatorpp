/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 9/17/11
 * Time: 11:05 PM
 */
public class CalculatorDisplayHistoryState {

	private boolean valid = true;

	@NotNull
	private final EditorHistoryState editorHistoryState;

	public CalculatorDisplayHistoryState() {
		this.editorHistoryState = new EditorHistoryState();
	}

	public CalculatorDisplayHistoryState(boolean valid) {
		this.editorHistoryState = new EditorHistoryState();
		this.valid = valid;
	}

	public CalculatorDisplayHistoryState(int cursorPosition, @Nullable String text, boolean valid) {
		this.editorHistoryState = new EditorHistoryState(cursorPosition, text);
		this.valid = valid;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public EditorHistoryState getEditorHistoryState() {
		return editorHistoryState;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CalculatorDisplayHistoryState)) return false;

		CalculatorDisplayHistoryState that = (CalculatorDisplayHistoryState) o;

		if (valid != that.valid) return false;
		if (editorHistoryState != null ? !editorHistoryState.equals(that.editorHistoryState) : that.editorHistoryState != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (valid ? 1 : 0);
		result = 31 * result + (editorHistoryState != null ? editorHistoryState.hashCode() : 0);
		return result;
	}
}
