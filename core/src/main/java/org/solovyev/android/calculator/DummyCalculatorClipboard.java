package org.solovyev.android.calculator;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 9/22/12
 * Time: 2:00 PM
 */
public class DummyCalculatorClipboard implements CalculatorClipboard {

	@Override
	public String getText() {
		return null;
	}

	@Override
	public void setText(@Nonnull String text) {
	}

	@Override
	public void setText(@Nonnull CharSequence text) {
	}
}
