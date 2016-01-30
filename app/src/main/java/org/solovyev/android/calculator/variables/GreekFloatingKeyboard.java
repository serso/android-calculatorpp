package org.solovyev.android.calculator.variables;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.solovyev.android.Views;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.keyboard.BaseFloatingKeyboard;
import org.solovyev.android.calculator.view.EditTextLongClickEraser;

import javax.annotation.Nonnull;
import java.util.Locale;


public class GreekFloatingKeyboard extends BaseFloatingKeyboard implements View.OnClickListener {
    private final static String GREEK_ALPHABET = "αβγδεζηθικλμνξοπρστυφχψω";

    public GreekFloatingKeyboard(@NonNull User user) {
        super(user);
    }

    public void makeView(boolean landscape) {
        LinearLayout rowView = null;
        final int columns = getColumnsCount(landscape);
        final int rows = getRowsCount(landscape);
        for (int i = 0, letter = 0; i < columns * rows; i++) {
            final int column = i % columns;
            final int row = i / columns;
            if (column == 0) {
                rowView = makeRow();
            }
            if (column == columns - 1) {
                if (!landscape) {
                    makeLastColumnPort(rowView, row);
                } else {
                    makeLastColumnLand(rowView, row);
                }
            } else if (letter < GREEK_ALPHABET.length()) {
                final Button button = addButton(rowView, View.NO_ID, String.valueOf(GREEK_ALPHABET.charAt(letter)));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    fixCapitalization(button);
                }
                letter++;
            } else {
                addButton(rowView, View.NO_ID, "");
            }
        }
    }

    private void makeLastColumnLand(@NonNull LinearLayout rowView, int row) {
        switch (row) {
            case 0:
                final View backspace = addImageButton(rowView, R.id.cpp_kb_button_backspace, R.drawable.ic_backspace_white_24dp);
                EditTextLongClickEraser.attachTo(backspace, user.getEditor());
                break;
            case 1:
                addButton(rowView, R.id.cpp_kb_button_change_case, "↑");
                break;
            case 2:
                addImageButton(rowView, R.id.cpp_kb_button_keyboard, R.drawable.ic_keyboard_white_24dp);
                break;
            case 3:
                addImageButton(rowView, R.id.cpp_kb_button_close, R.drawable.ic_done_white_24dp);
                break;
            default:
                addButton(rowView, View.NO_ID, "");
                break;
        }
    }

    private void makeLastColumnPort(@NonNull LinearLayout rowView, int row) {
        switch (row) {
            case 0:
                addButton(rowView, R.id.cpp_kb_button_clear, "C");
                break;
            case 1:
                final View backspace = addImageButton(rowView, R.id.cpp_kb_button_backspace, R.drawable.ic_backspace_white_24dp);
                EditTextLongClickEraser.attachTo(backspace, user.getEditor());
                break;
            case 2:
                addButton(rowView, R.id.cpp_kb_button_change_case, "↑");
                break;
            case 3:
                addImageButton(rowView, R.id.cpp_kb_button_keyboard, R.drawable.ic_keyboard_white_24dp);
                break;
            case 4:
                addImageButton(rowView, R.id.cpp_kb_button_close, R.drawable.ic_done_white_24dp);
                break;
            default:
                addButton(rowView, View.NO_ID, "");
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void fixCapitalization(Button button) {
        button.setAllCaps(false);
    }

    protected void fillButton(@NonNull View button, @IdRes int id) {
        super.fillButton(button, id);
        button.setOnClickListener(this);
    }

    public int getRowsCount(boolean landscape) {
        return landscape ? 4 : 5;
    }

    public int getColumnsCount(boolean landscape) {
        return landscape ? 7 : 6;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cpp_kb_button_close:
                user.done();
                break;
            case R.id.cpp_kb_button_change_case:
                changeCase((Button) v);
                break;
            case R.id.cpp_kb_button_keyboard:
                user.showIme();
                break;
            case R.id.cpp_kb_button_clear:
                user.getEditor().setText("");
                user.getEditor().setSelection(0);
                break;
            default:
                user.getEditor().append(((TextView) v).getText());
                break;
        }
        user.getEditor().requestFocus();
        v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
    }

    private void changeCase(@NonNull Button button) {
        final boolean upperCase = button.getText().equals("↑");
        Views.processViewsOfType(user.getKeyboard(), Button.class, new Views.ViewProcessor<Button>() {
            @Override
            public void process(@Nonnull Button key) {
                final String letter = key.getText().toString();
                if (!GREEK_ALPHABET.contains(letter.toLowerCase(Locale.US))) {
                    return;
                }
                if (upperCase) {
                    key.setText(letter.toUpperCase(Locale.US));
                } else {
                    key.setText(letter.toLowerCase(Locale.US));
                }
            }
        });
        if (upperCase) {
            button.setText("↓");
        } else {
            button.setText("↑");
        }
    }
}