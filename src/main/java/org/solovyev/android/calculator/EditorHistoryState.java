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

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setCursorPosition(int cursorPosition) {
		this.cursorPosition = cursorPosition;
	}

	public int getCursorPosition() {
		return cursorPosition;
	}


}
