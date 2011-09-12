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
	private EditorHistoryState resultEditorState;

	public CalculatorHistoryState(@NotNull EditorHistoryState editorState, @NotNull EditorHistoryState resultEditorState) {
		this.editorState = editorState;
		this.resultEditorState = resultEditorState;
	}

	@NotNull
	public EditorHistoryState getEditorState() {
		return editorState;
	}

	public void setEditorState(@NotNull EditorHistoryState editorState) {
		this.editorState = editorState;
	}

	@NotNull
	public EditorHistoryState getResultEditorState() {
		return resultEditorState;
	}

	public void setResultEditorState(@NotNull EditorHistoryState resultEditorState) {
		this.resultEditorState = resultEditorState;
	}
}
