package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

/**
 * User: Solovyev_S
 * Date: 21.09.12
 * Time: 12:02
 */
public class CalculatorEditorViewStateImpl implements CalculatorEditorViewState {

	@NotNull
	private String text = "";

	private int selection = 0;

	private CalculatorEditorViewStateImpl() {
	}

	public CalculatorEditorViewStateImpl(@NotNull CalculatorEditorViewState viewState) {
		this.text = viewState.getText();
		this.selection = viewState.getSelection();
	}

	@NotNull
	@Override
	public String getText() {
		return this.text;
	}

	@Override
	public int getSelection() {
		return this.selection;
	}

	@NotNull
	public static CalculatorEditorViewState newDefaultInstance() {
		return new CalculatorEditorViewStateImpl();
	}

	@NotNull
	public static CalculatorEditorViewState newSelection(@NotNull CalculatorEditorViewState viewState, int newSelection) {
		final CalculatorEditorViewStateImpl result = new CalculatorEditorViewStateImpl(viewState);

		result.selection = newSelection;

		return result;
	}

	@NotNull
	public static CalculatorEditorViewState newInstance(@NotNull String text, int selection) {
		final CalculatorEditorViewStateImpl result = new CalculatorEditorViewStateImpl();
		result.text = text;
		result.selection = selection;
		return result;
	}
}
