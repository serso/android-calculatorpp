/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.history;

import org.jetbrains.annotations.NotNull;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.solovyev.android.calculator.CalculatorDisplay;
import org.solovyev.android.calculator.Editor;

/**
 * User: serso
 * Date: 9/11/11
 * Time: 12:16 AM
 */

@Root
public class CalculatorHistoryState extends AbstractHistoryState {

	@Element
	@NotNull
	private EditorHistoryState editorState;

	@Element
	@NotNull
	private CalculatorDisplayHistoryState displayState;

	private CalculatorHistoryState() {
		// for xml
	}

	private CalculatorHistoryState(@NotNull EditorHistoryState editorState,
								  @NotNull CalculatorDisplayHistoryState displayState) {
		this.editorState = editorState;
		this.displayState = displayState;
	}

	public static CalculatorHistoryState newInstance(@NotNull Editor editor, @NotNull CalculatorDisplay display) {
		final EditorHistoryState editorHistoryState = EditorHistoryState.newInstance(editor);
		final CalculatorDisplayHistoryState displayHistoryState = CalculatorDisplayHistoryState.newInstance(display);
		return new CalculatorHistoryState(editorHistoryState, displayHistoryState);
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

		if (this.isSaved() != that.isSaved()) return false;
		if (this.getId() != that.getId()) return false;
		if (!displayState.equals(that.displayState)) return false;
		if (!editorState.equals(that.editorState)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = Boolean.valueOf(isSaved()).hashCode();
		result = 31 * result + getId();
		result = 31 * result + editorState.hashCode();
		result = 31 * result + displayState.hashCode();
		return result;
	}

	public void setValuesFromHistory(@NotNull Editor editor, @NotNull CalculatorDisplay display) {
		this.getEditorState().setValuesFromHistory(editor);
		this.getDisplayState().setValuesFromHistory(display);
	}

	@Override
	protected CalculatorHistoryState clone() {
		final CalculatorHistoryState clone = (CalculatorHistoryState)super.clone();

		clone.editorState = this.editorState.clone();
		clone.displayState = this.displayState.clone();

		return clone;
	}
}
