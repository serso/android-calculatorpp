/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * User: serso
 * Date: 9/17/11
 * Time: 10:58 PM
 */
public class CalculatorDisplay extends TextView {

	private boolean valid = true;

	public CalculatorDisplay(Context context) {
		super(context);
	}

	public CalculatorDisplay(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CalculatorDisplay(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(text, type);

		setValid(true);
	}
}
