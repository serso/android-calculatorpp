/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 9/11/11
 * Time: 12:16 AM
 */
public class CalculatorHistoryState extends AbstractHistoryState{

	@NotNull
	private EditorHistoryState editorState;

	@NotNull
	private CalculatorDisplayHistoryState displayState;

	public CalculatorHistoryState(@NotNull EditorHistoryState editorState,
								  @NotNull CalculatorDisplayHistoryState displayState) {
		this.editorState = editorState;
		this.displayState = displayState;
	}

	@NotNull
	public EditorHistoryState getEditorState() {
		return editorState;
	}

	public void setEditorState(@NotNull EditorHistoryState editorState) {
		this.editorState = editorState;
	}

	@NotNull
	public CalculatorDisplayHistoryState getDisplayState() {
		return displayState;
	}

	public void setDisplayState(@NotNull CalculatorDisplayHistoryState displayState) {
		this.displayState = displayState;
	}

	@Override
	public String toString() {
		return "CalculatorHistoryState{" +
				"editorState=" + editorState +
				", displayState=" + displayState +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CalculatorHistoryState that = (CalculatorHistoryState) o;

		if (!displayState.equals(that.displayState)) return false;
		if (!editorState.equals(that.editorState)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = editorState.hashCode();
		result = 31 * result + displayState.hashCode();
		return result;
	}
}
