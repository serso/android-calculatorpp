package org.solovyev.android.calculator.keyboard;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.view.EditTextCompat;
import org.solovyev.android.calculator.view.EditTextLongClickEraser;
import org.solovyev.android.views.dragbutton.*;

import static org.solovyev.android.views.dragbutton.DragDirection.left;

public class FloatingNumberKeyboard extends BaseFloatingKeyboard {

    @NonNull
    private final ButtonHandler buttonHandler = new ButtonHandler();
    private final DirectionDragListener dragListener;

    public FloatingNumberKeyboard(@NonNull User user) {
        super(user);
        dragListener = new DirectionDragListener(user.getContext()) {
            @Override
            protected boolean onDrag(@NonNull View view, @NonNull DragEvent event, @NonNull DragDirection direction) {
                if (!Drag.hasDirectionText(view, direction)) {
                    return false;
                }
                insertText(((DirectionDragView) view).getText(direction).getValue());
                return true;
            }
        };
    }

    @Override
    public int getRowsCount(boolean landscape) {
        return 4;
    }

    @Override
    public int getColumnsCount(boolean landscape) {
        return 4;
    }

    @Override
    public void makeView(boolean landscape) {
        LinearLayout row = makeRow();
        addButton(row, 0, "7");
        addButton(row, 0, "8");
        addButton(row, 0, "9");
        final View backspace = addImageButton(row, R.id.cpp_kb_button_backspace, R.drawable.ic_backspace_white_24dp);
        EditTextLongClickEraser.attachTo(backspace, user.getEditor(), user.isVibrateOnKeypress());

        row = makeRow();
        addButton(row, 0, "4").setText(left, "A");
        addButton(row, 0, "5").setText(left, "B");
        addButton(row, 0, "6").setText(left, "C");
        addButton(row, R.id.cpp_kb_button_clear, "C");

        row = makeRow();
        addButton(row, 0, "1").setText(left, "D");
        addButton(row, 0, "2").setText(left, "E");
        addButton(row, 0, "3").setText(left, "F");
        addButton(row, 0, "E");

        row = makeRow();
        addButton(row, 0, "-");
        addButton(row, 0, "0");
        addButton(row, 0, ".");
        addImageButton(row, R.id.cpp_kb_button_close, R.drawable.ic_done_white_24dp);
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

    private void insertText(CharSequence text) {
        EditTextCompat.insert(text, getUser().getEditor());
    }

    private class ButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final EditText editor = getUser().getEditor();
            switch (v.getId()) {
                case R.id.cpp_kb_button_clear:
                    editor.setText("");
                    return;
                case R.id.cpp_kb_button_close:
                    getUser().done();
                    return;
            }
            if (v instanceof TextView) {
                insertText(((TextView) v).getText());
            }
        }
    }
}
