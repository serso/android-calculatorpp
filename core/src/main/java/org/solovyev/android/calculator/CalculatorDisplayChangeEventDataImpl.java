package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 9/21/12
 * Time: 9:50 PM
 */
public class CalculatorDisplayChangeEventDataImpl implements CalculatorDisplayChangeEventData {

	@NotNull
	private final CalculatorDisplayViewState oldState;

	@NotNull
	private final CalculatorDisplayViewState newState;

	public CalculatorDisplayChangeEventDataImpl(@NotNull CalculatorDisplayViewState oldState, @NotNull CalculatorDisplayViewState newState) {
		this.oldState = oldState;
		this.newState = newState;
	}

	@NotNull
	@Override
	public CalculatorDisplayViewState getOldValue() {
		return this.oldState;
	}

	@NotNull
	@Override
	public CalculatorDisplayViewState getNewValue() {
		return this.newState;
	}
}
