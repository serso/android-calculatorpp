package org.solovyev.android.calculator;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 9/21/12
 * Time: 9:50 PM
 */
public class CalculatorDisplayChangeEventDataImpl implements CalculatorDisplayChangeEventData {

	@Nonnull
	private final CalculatorDisplayViewState oldState;

	@Nonnull
	private final CalculatorDisplayViewState newState;

	public CalculatorDisplayChangeEventDataImpl(@Nonnull CalculatorDisplayViewState oldState, @Nonnull CalculatorDisplayViewState newState) {
		this.oldState = oldState;
		this.newState = newState;
	}

	@Nonnull
	@Override
	public CalculatorDisplayViewState getOldValue() {
		return this.oldState;
	}

	@Nonnull
	@Override
	public CalculatorDisplayViewState getNewValue() {
		return this.newState;
	}
}
