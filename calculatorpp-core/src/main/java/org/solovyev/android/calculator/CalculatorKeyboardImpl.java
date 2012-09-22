package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.common.text.StringUtils;

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
    public void digitButtonPressed(@Nullable final String text) {

        if (!StringUtils.isEmpty(text)) {
            assert text != null;

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

            final CalculatorEditor editor = CalculatorLocatorImpl.getInstance().getCalculatorEditor();
            editor.insert(textToBeInserted.toString(), cursorPositionOffset);
        }
    }

    @Override
    public void roundBracketsButtonPressed() {
        final CalculatorEditor editor = CalculatorLocatorImpl.getInstance().getCalculatorEditor();
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
        final String text = CalculatorLocatorImpl.getInstance().getCalculatorClipboard().getText();
        if (text != null) {
            CalculatorLocatorImpl.getInstance().getCalculatorEditor().insert(text);
        }
    }

    @Override
    public void clearButtonPressed() {
        CalculatorLocatorImpl.getInstance().getCalculatorEditor().clear();
    }

    @Override
    public void copyButtonPressed() {
        final CalculatorDisplayViewState displayViewState = CalculatorLocatorImpl.getInstance().getCalculatorDisplay().getViewState();
        if (displayViewState.isValid()) {
            final CharSequence text = displayViewState.getText();
            if (!StringUtils.isEmpty(text)) {
                CalculatorLocatorImpl.getInstance().getCalculatorClipboard().setText(text);
                CalculatorLocatorImpl.getInstance().getCalculatorNotifier().showMessage(CalculatorMessage.newInfoMessage(CalculatorMessages.result_copied));
            }
        }
    }
}
