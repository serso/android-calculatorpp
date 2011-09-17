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
public class CalculatorHistoryState {

	@NotNull
	private EditorHistoryState editorState;

	@NotNull
	private CalculatorDisplayHistoryState displayState;

	public CalculatorHistoryState(@NotNull EditorHistoryState editorState, @NotNull CalculatorDisplayHistoryState displayState) {
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
}
