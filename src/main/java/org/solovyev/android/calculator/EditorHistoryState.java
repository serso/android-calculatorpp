/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import org.jetbrains.annotations.Nullable;

public class EditorHistoryState {
	
	private int cursorPosition;
	
	@Nullable
	private String text;
	
	public EditorHistoryState() {
	}
	
	public EditorHistoryState( int cursorPosition, @Nullable String text ) {
		this.cursorPosition = cursorPosition;
		this.text = text;
	}

	public void setText(@Nullable String text) {
		this.text = text;
	}

	@Nullable
	public String getText() {
		return text;
	}

	public void setCursorPosition(int cursorPosition) {
		this.cursorPosition = cursorPosition;
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
}
