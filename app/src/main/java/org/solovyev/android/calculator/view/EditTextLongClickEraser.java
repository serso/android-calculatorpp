package org.solovyev.android.calculator.view;

import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import javax.annotation.Nonnull;

import static android.view.HapticFeedbackConstants.*;

public class EditTextLongClickEraser extends BaseLongClickEraser implements View.OnClickListener {
    @Nonnull
    private final EditText editView;

    private EditTextLongClickEraser(@Nonnull View view, @Nonnull EditText editView, boolean vibrateOnKeypress) {
        super(view, vibrateOnKeypress);
        this.editView = editView;
        view.setOnClickListener(this);
    }

    public static void attachTo(@Nonnull View view, @Nonnull EditText editView, boolean vibrateOnKeypress) {
        new EditTextLongClickEraser(view, editView, vibrateOnKeypress);
    }

    @Override
    protected void onStopErase() {
    }

    @Override
    protected void onStartErase() {
    }

    @Override
    protected boolean erase() {
        final int start = editView.getSelectionStart();
        final int end = editView.getSelectionEnd();
        if (start < 0 || end < 0) {
            return false;
        }
        final Editable text = editView.getText();
        if (start != end) {
            text.delete(Math.min(start, end), Math.max(start, end));
        } else if (start > 0) {
            text.delete(start - 1, start);
        }
        return text.length() != 0;
    }
    @Override
    public void onClick(View v) {
        erase();
        if (vibrateOnKeypress) {
            v.performHapticFeedback(KEYBOARD_TAP, FLAG_IGNORE_GLOBAL_SETTING | FLAG_IGNORE_VIEW_SETTING);
        }
    }
}
