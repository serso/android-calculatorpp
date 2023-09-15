package org.solovyev.android.calculator;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import org.solovyev.android.calculator.keyboard.BaseFloatingKeyboard;
import org.solovyev.android.calculator.keyboard.FloatingKeyboard;
import org.solovyev.android.calculator.view.EditTextLongClickEraser;
import org.solovyev.android.views.dragbutton.*;

import java.util.List;

import static android.view.HapticFeedbackConstants.*;
import static org.solovyev.android.views.dragbutton.DragDirection.down;
import static org.solovyev.android.views.dragbutton.DragDirection.up;


public class FloatingCalculatorKeyboard extends BaseFloatingKeyboard {
    @NonNull
    private final ButtonHandler buttonHandler = new ButtonHandler();
    @NonNull
    private final List<String> parameterNames;
    @NonNull
    private final DirectionDragListener dragListener;

    public FloatingCalculatorKeyboard(@NonNull User user, @NonNull List<String> parameterNames) {
        super(user);
        this.parameterNames = parameterNames;
        this.dragListener = new DirectionDragListener(user.getContext()) {
            @Override
            protected boolean onDrag(@NonNull View view, @NonNull DragEvent event, @NonNull DragDirection direction) {
                return Drag.hasDirectionText(view, direction) && buttonHandler.onDrag(view, direction);
            }
        };
    }

    public void makeView(boolean landscape) {
        if (landscape) {
            makeViewLand();
        } else {
            makeViewPort();
        }
    }

    @NonNull
    @Override
    public User getUser() {
        return (User) super.getUser();
    }

    @Override
    protected void fillButton(@NonNull View button, @IdRes int id) {
        super.fillButton(button, id);
        button.setOnClickListener(buttonHandler);
    }

    @NonNull
    @Override
    protected DirectionDragButton makeButton(@IdRes int id, @NonNull String text) {
        final DirectionDragButton button = super.makeButton(id, text);
        button.setOnDragListener(dragListener);
        return button;
    }

    private void makeViewLand() {
        final int parametersCount = parameterNames.size();

        LinearLayout row = makeRow();
        addImageButton(row, R.id.cpp_kb_button_keyboard, R.drawable.ic_keyboard_white_24dp);
        addButton(row, 0, parametersCount > 0 ? parameterNames.get(0) : "x");
        addButton(row, 0, "7");
        addButton(row, 0, "8");
        addButton(row, 0, "9").setText(up, "π").setText(down, "e");
        addOperationButton(row, R.id.cpp_kb_button_divide, "/").setText(up, "√").setText(down, "%");
        addOperationButton(row, R.id.cpp_kb_button_multiply, "×").setText(up, "^").setText(down, "^2");
        addButton(row, R.id.cpp_kb_button_clear, "C");

        row = makeRow();
        addButton(row, R.id.cpp_kb_button_brackets, "( )").setText(up, "(").setText(down, ")");
        addButton(row, 0, parametersCount > 1 ? parameterNames.get(1) : "y");
        addButton(row, 0, "4");
        addButton(row, 0, "5");
        addButton(row, 0, "6");
        addOperationButton(row, R.id.cpp_kb_button_minus, "−");
        addOperationButton(row, R.id.cpp_kb_button_plus, "+");
        final View backspace = addImageButton(row, R.id.cpp_kb_button_backspace, R.drawable.ic_backspace_white_24dp);
        EditTextLongClickEraser.attachTo(backspace, user.getEditor(), user.isVibrateOnKeypress());

        row = makeRow();
        addButton(row, R.id.cpp_kb_button_functions_constants, "ƒ/π");
        addImageButton(row, R.id.cpp_kb_button_space, R.drawable.ic_space_bar_white_24dp);
        addButton(row, 0, "1");
        addButton(row, 0, "2");
        addButton(row, 0, "3");
        addButton(row, 0, "0").setText(up, "000").setText(down, "00");
        addButton(row, 0, ".").setText(up, ",");
        addImageButton(row, R.id.cpp_kb_button_close, R.drawable.ic_done_white_24dp);
    }

    private void makeViewPort() {
        final int parametersCount = parameterNames.size();

        LinearLayout row = makeRow();
        addButton(row, R.id.cpp_kb_button_constants, "π…");
        addButton(row, R.id.cpp_kb_button_functions, "ƒ");
        addImageButton(row, R.id.cpp_kb_button_space, R.drawable.ic_space_bar_white_24dp);
        final View backspace = addImageButton(row, R.id.cpp_kb_button_backspace, R.drawable.ic_backspace_white_24dp);
        EditTextLongClickEraser.attachTo(backspace, user.getEditor(), user.isVibrateOnKeypress());
        addButton(row, R.id.cpp_kb_button_clear, "C");

        row = makeRow();
        addButton(row, 0, "7");
        addButton(row, 0, "8");
        addButton(row, 0, "9").setText(up, "π").setText(down, "e");
        addOperationButton(row, R.id.cpp_kb_button_divide, "/").setText(up, "√").setText(down, "%");
        addButton(row, 0, parametersCount > 0 ? parameterNames.get(0) : "x");

        row = makeRow();
        addButton(row, 0, "4");
        addButton(row, 0, "5");
        addButton(row, 0, "6");
        addOperationButton(row, R.id.cpp_kb_button_multiply, "×").setText(up, "^").setText(down, "^2");
        addButton(row, 0, parametersCount > 1 ? parameterNames.get(1) : "y");

        row = makeRow();
        addButton(row, 0, "1");
        addButton(row, 0, "2");
        addButton(row, 0, "3");
        addOperationButton(row, R.id.cpp_kb_button_minus, "−");
        addImageButton(row, R.id.cpp_kb_button_keyboard, R.drawable.ic_keyboard_white_24dp);

        row = makeRow();
        addButton(row, R.id.cpp_kb_button_brackets, "( )").setText(up, "(").setText(down, ")");
        addButton(row, 0, "0").setText(up, "000").setText(down, "00");
        addButton(row, 0, ".").setText(up, ",");
        addOperationButton(row, R.id.cpp_kb_button_plus, "+");
        addImageButton(row, R.id.cpp_kb_button_close, R.drawable.ic_done_white_24dp);
    }

    public int getRowsCount(boolean landscape) {
        return landscape ? 3 : 5;
    }

    public int getColumnsCount(boolean landscape) {
        return landscape ? 8 : 5;
    }

    public interface User extends FloatingKeyboard.User {

        void insertOperator(char operator);

        void insertOperator(@NonNull String operator);

        void showFunctions(@NonNull View v);

        void showConstants(@NonNull View v);

        void showFunctionsConstants(@NonNull View v);

        void insertText(@NonNull CharSequence text, int offset);

    }

    private class ButtonHandler implements View.OnClickListener {

        @NonNull
        private final User user = getUser();

        @Override
        public void onClick(@NonNull View v) {
            if (user.isVibrateOnKeypress()) {
                v.performHapticFeedback(KEYBOARD_TAP, FLAG_IGNORE_GLOBAL_SETTING | FLAG_IGNORE_VIEW_SETTING);
            }
            int id = v.getId();
            if (id == R.id.cpp_kb_button_divide) {
                user.insertOperator('/');
            } else if (id == R.id.cpp_kb_button_plus) {
                user.insertOperator('+');
            } else if (id == R.id.cpp_kb_button_minus) {
                user.insertOperator('-');
            } else if (id == R.id.cpp_kb_button_multiply) {
                user.insertOperator("×");
            } else if (id == R.id.cpp_kb_button_functions_constants) {
                user.showFunctionsConstants(v);
            } else if (id == R.id.cpp_kb_button_functions) {
                user.showFunctions(v);
            } else if (id == R.id.cpp_kb_button_constants) {
                user.showConstants(v);
            } else if (id == R.id.cpp_kb_button_space) {
                user.insertText(" ", 0);
            } else if (id == R.id.cpp_kb_button_keyboard) {
                user.showIme();
            } else if (id == R.id.cpp_kb_button_clear) {
                user.getEditor().setText("");
                user.getEditor().setSelection(0);
            } else if (id == R.id.cpp_kb_button_brackets) {
                user.insertText("()", -1);
            } else if (id == R.id.cpp_kb_button_close) {
                user.done();
            } else {
                onDefaultClick(v);
            }
            user.getEditor().requestFocus();
        }

        private void onDefaultClick(@NonNull View v) {
            user.insertText(((Button) v).getText(), 0);
        }

        private boolean onDrag(@NonNull View button, @NonNull DragDirection direction) {
            final String text = ((DirectionDragButton) button).getTextValue(direction);
            if (TextUtils.isEmpty(text)) {
                return false;
            }
            switch (text) {
                case "√":
                    user.insertText("√()", -1);
                    break;
                case ",":
                    user.insertText(", ", 0);
                    break;
                case "^":
                    user.insertOperator('^');
                    break;
                case "^2":
                    user.insertOperator("^ 2");
                    break;
                case "?":
                case ">":
                case "<":
                case ">=":
                case "<=":
                case ":":
                    user.insertOperator(text);
                    break;
                default:
                    user.insertText(text, 0);
                    break;
            }
            return true;
        }
    }
}
