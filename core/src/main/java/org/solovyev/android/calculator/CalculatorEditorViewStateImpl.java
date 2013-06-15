package org.solovyev.android.calculator;

import javax.annotation.Nonnull;

/**
 * User: Solovyev_S
 * Date: 21.09.12
 * Time: 12:02
 */
public class CalculatorEditorViewStateImpl implements CalculatorEditorViewState {

	@Nonnull
	private String text = "";

	private int selection = 0;

	private CalculatorEditorViewStateImpl() {
	}

	public CalculatorEditorViewStateImpl(@Nonnull CalculatorEditorViewState viewState) {
		this.text = viewState.getText();
		this.selection = viewState.getSelection();
	}

	@Nonnull
	@Override
	public String getText() {
		return this.text;
	}

	@Override
	public int getSelection() {
		return this.selection;
	}

	@Nonnull
	public static CalculatorEditorViewState newDefaultInstance() {
		return new CalculatorEditorViewStateImpl();
	}

	@Nonnull
	public static CalculatorEditorViewState newSelection(@Nonnull CalculatorEditorViewState viewState, int newSelection) {
		final CalculatorEditorViewStateImpl result = new CalculatorEditorViewStateImpl(viewState);

		result.selection = newSelection;

		return result;
	}

	@Nonnull
	public static CalculatorEditorViewState newInstance(@Nonnull String text, int selection) {
		final CalculatorEditorViewStateImpl result = new CalculatorEditorViewStateImpl();
		result.text = text;
		result.selection = selection;
		return result;
	}
}
