package org.solovyev.android.calculator;

import javax.annotation.Nonnull;

/**
 * User: Solovyev_S
 * Date: 21.09.12
 * Time: 13:46
 */
public class CalculatorEditorChangeEventDataImpl implements CalculatorEditorChangeEventData {

	@Nonnull
	private CalculatorEditorViewState oldState;

	@Nonnull
	private CalculatorEditorViewState newState;

	public CalculatorEditorChangeEventDataImpl(@Nonnull CalculatorEditorViewState oldState,
											   @Nonnull CalculatorEditorViewState newState) {
		this.oldState = oldState;
		this.newState = newState;
	}

	@Nonnull
	@Override
	public CalculatorEditorViewState getOldValue() {
		return this.oldState;
	}

	@Nonnull
	@Override
	public CalculatorEditorViewState getNewValue() {
		return this.newState;
	}
}
