/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.history;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.solovyev.android.calculator.CalculatorDisplayViewState;
import org.solovyev.android.calculator.CalculatorEditor;
import org.solovyev.android.calculator.CalculatorEditorViewState;
import org.solovyev.common.text.Strings;

@Root
public class EditorHistoryState implements Cloneable {

	@Element
	private int cursorPosition;

	@Element(required = false)
	@Nullable
	private String text = "";

	private EditorHistoryState() {
		// for xml
	}

	@Nonnull
	public static EditorHistoryState newInstance(@Nonnull CalculatorEditorViewState viewState) {
		final EditorHistoryState result = new EditorHistoryState();

		result.text = String.valueOf(viewState.getText());
		result.cursorPosition = viewState.getSelection();

		return result;
	}

	@Nonnull
	public static EditorHistoryState newInstance(@Nonnull CalculatorDisplayViewState viewState) {
		final EditorHistoryState result = new EditorHistoryState();

		result.text = viewState.getText();
		result.cursorPosition = viewState.getSelection();

		return result;
	}

	public void setValuesFromHistory(@Nonnull CalculatorEditor editor) {
		editor.setText(Strings.getNotEmpty(this.getText(), ""));
		editor.setSelection(this.getCursorPosition());
	}

	@Nullable
	public String getText() {
		return text;
	}

	public int getCursorPosition() {
		return cursorPosition;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EditorHistoryState)) return false;

		EditorHistoryState that = (EditorHistoryState) o;

		if (cursorPosition != that.cursorPosition) return false;
		if (text != null ? !text.equals(that.text) : that.text != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = cursorPosition;
		result = 31 * result + (text != null ? text.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "EditorHistoryState{" +
				"cursorPosition=" + cursorPosition +
				", text='" + text + '\'' +
				'}';
	}

	@Override
	protected EditorHistoryState clone() {
		try {
			return (EditorHistoryState) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new UnsupportedOperationException(e);
		}
	}
}
