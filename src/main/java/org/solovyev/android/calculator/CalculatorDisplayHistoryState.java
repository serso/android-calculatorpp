/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 9/17/11
 * Time: 11:05 PM
 */
public class CalculatorDisplayHistoryState extends EditorHistoryState {

	private boolean valid = true;

	public CalculatorDisplayHistoryState() {
	}

	public CalculatorDisplayHistoryState(boolean valid) {
		this.valid = valid;
	}

	public CalculatorDisplayHistoryState(int cursorPosition, @Nullable String text, boolean valid) {
		super(cursorPosition, text);
		this.valid = valid;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
