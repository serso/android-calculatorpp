package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.common.text.Strings;

/**
 * User: serso
 * Date: 9/22/12
 * Time: 1:08 PM
 */
public class CalculatorKeyboardImpl implements CalculatorKeyboard {

	@NotNull
	private final Calculator calculator;

	public CalculatorKeyboardImpl(@NotNull Calculator calculator) {
		this.calculator = calculator;
	}

	@Override
	public void buttonPressed(@Nullable final String text) {

		if (!Strings.isEmpty(text)) {
			assert text != null;

			// process special buttons
			boolean processed = processSpecialButtons(text);

			if (!processed) {
				int cursorPositionOffset = 0;
				final StringBuilder textToBeInserted = new StringBuilder(text);

				final MathType.Result mathType = MathType.getType(text, 0, false);
				switch (mathType.getMathType()) {
					case function:
						textToBeInserted.append("()");
						cursorPositionOffset = -1;
						break;
					case operator:
						textToBeInserted.append("()");
						cursorPositionOffset = -1;
						break;
					case comma:
						textToBeInserted.append(" ");
						break;
				}

				if (cursorPositionOffset == 0) {
					if (MathType.openGroupSymbols.contains(text)) {
						cursorPositionOffset = -1;
					}
				}

				final CalculatorEditor editor = Locator.getInstance().getEditor();
				editor.insert(textToBeInserted.toString(), cursorPositionOffset);
			}
		}
	}

	private boolean processSpecialButtons(@NotNull String text) {
		boolean result = false;

		final CalculatorSpecialButton button = CalculatorSpecialButton.getByActionCode(text);
		if (button != null) {
			button.onClick(this);
			result = true;
		}

		return result;
	}

	@Override
	public void roundBracketsButtonPressed() {
		final CalculatorEditor editor = Locator.getInstance().getEditor();
		CalculatorEditorViewState viewState = editor.getViewState();

		final int cursorPosition = viewState.getSelection();
		final String oldText = viewState.getText();

		final StringBuilder newText = new StringBuilder(oldText.length() + 2);
		newText.append("(");
		newText.append(oldText.substring(0, cursorPosition));
		newText.append(")");
		newText.append(oldText.substring(cursorPosition));
		editor.setText(newText.toString(), cursorPosition + 2);
	}

	@Override
	public void pasteButtonPressed() {
		final String text = Locator.getInstance().getClipboard().getText();
		if (text != null) {
			Locator.getInstance().getEditor().insert(text);
		}
	}

	@Override
	public void clearButtonPressed() {
		Locator.getInstance().getEditor().clear();
	}

	@Override
	public void copyButtonPressed() {
		final CalculatorDisplayViewState displayViewState = Locator.getInstance().getDisplay().getViewState();
		if (displayViewState.isValid()) {
			final CharSequence text = displayViewState.getText();
			if (!Strings.isEmpty(text)) {
				Locator.getInstance().getClipboard().setText(text);
				Locator.getInstance().getNotifier().showMessage(CalculatorMessage.newInfoMessage(CalculatorMessages.result_copied));
			}
		}
	}

	@Override
	public void moveCursorLeft() {
		Locator.getInstance().getEditor().moveCursorLeft();
	}

	@Override
	public void moveCursorRight() {
		Locator.getInstance().getEditor().moveCursorRight();
	}
}
