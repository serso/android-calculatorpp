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
    public void buttonPressed(@Nullable final String text) {

        if (!StringUtils.isEmpty(text)) {
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

                final CalculatorEditor editor = CalculatorLocatorImpl.getInstance().getEditor();
                editor.insert(textToBeInserted.toString(), cursorPositionOffset);
            }
        }
    }

    private boolean processSpecialButtons(@NotNull String text) {
        boolean result = false;

        if (CalculatorButtonActions.MOVE_CURSOR_LEFT.equals(text)) {
            this.moveCursorLeft();
            result = true;
        } else if (CalculatorButtonActions.MOVE_CURSOR_RIGHT.equals(text)) {
            this.moveCursorRight();
            result = true;
        } else if (CalculatorButtonActions.SHOW_HISTORY.equals(text)) {
            CalculatorLocatorImpl.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_history, null);
        } else if (CalculatorButtonActions.ERASE.equals(text)) {
            CalculatorLocatorImpl.getInstance().getEditor().erase();
        } else if (CalculatorButtonActions.COPY.equals(text)) {
            copyButtonPressed();
        } else if (CalculatorButtonActions.PASTE.equals(text)) {
            pasteButtonPressed();
        } else if (CalculatorButtonActions.CLEAR.equals(text)) {
            clearButtonPressed();
        } else if (CalculatorButtonActions.SHOW_FUNCTIONS.equals(text)) {
            CalculatorLocatorImpl.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_functions, null);
        } else if (CalculatorButtonActions.SHOW_OPERATORS.equals(text)) {
            CalculatorLocatorImpl.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_operators, null);
        } else if (CalculatorButtonActions.SHOW_VARS.equals(text)) {
            CalculatorLocatorImpl.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_vars, null);
        }

        return result;
    }

    @Override
    public void roundBracketsButtonPressed() {
        final CalculatorEditor editor = CalculatorLocatorImpl.getInstance().getEditor();
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
        final String text = CalculatorLocatorImpl.getInstance().getClipboard().getText();
        if (text != null) {
            CalculatorLocatorImpl.getInstance().getEditor().insert(text);
        }
    }

    @Override
    public void clearButtonPressed() {
        CalculatorLocatorImpl.getInstance().getEditor().clear();
    }

    @Override
    public void copyButtonPressed() {
        final CalculatorDisplayViewState displayViewState = CalculatorLocatorImpl.getInstance().getDisplay().getViewState();
        if (displayViewState.isValid()) {
            final CharSequence text = displayViewState.getText();
            if (!StringUtils.isEmpty(text)) {
                CalculatorLocatorImpl.getInstance().getClipboard().setText(text);
                CalculatorLocatorImpl.getInstance().getNotifier().showMessage(CalculatorMessage.newInfoMessage(CalculatorMessages.result_copied));
            }
        }
    }

    @Override
    public void moveCursorLeft() {
        CalculatorLocatorImpl.getInstance().getEditor().moveCursorLeft();
    }

    @Override
    public void moveCursorRight() {
        CalculatorLocatorImpl.getInstance().getEditor().moveCursorRight();
    }
}
