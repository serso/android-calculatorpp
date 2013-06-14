package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

/**
 * User: Solovyev_S
 * Date: 21.09.12
 * Time: 13:46
 */
public class CalculatorEditorChangeEventDataImpl implements CalculatorEditorChangeEventData {

	@NotNull
	private CalculatorEditorViewState oldState;

	@NotNull
	private CalculatorEditorViewState newState;

	public CalculatorEditorChangeEventDataImpl(@NotNull CalculatorEditorViewState oldState,
											   @NotNull CalculatorEditorViewState newState) {
		this.oldState = oldState;
		this.newState = newState;
	}

	@NotNull
	@Override
	public CalculatorEditorViewState getOldValue() {
		return this.oldState;
	}

	@NotNull
	@Override
	public CalculatorEditorViewState getNewValue() {
		return this.newState;
	}
}
