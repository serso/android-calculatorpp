package org.solovyev.android.calculator.view;

import android.view.View;
import org.solovyev.android.calculator.*;

import javax.annotation.Nonnull;

import static android.text.TextUtils.isEmpty;

public class EditorLongClickEraser extends BaseLongClickEraser {

    @Nonnull
    private final Editor editor;

    @Nonnull
    private final Calculator calculator;

    private boolean wasCalculatingOnFly;

    private EditorLongClickEraser(@Nonnull View view, boolean vibrateOnKeypress, @Nonnull Editor editor, @Nonnull Calculator calculator) {
        super(view, vibrateOnKeypress);
        this.editor = editor;
        this.calculator = calculator;
    }

    @Nonnull
    public static EditorLongClickEraser attachTo(@Nonnull View view, boolean vibrateOnKeypress, @Nonnull Editor editor, @Nonnull Calculator calculator) {
        return new EditorLongClickEraser(view, vibrateOnKeypress, editor, calculator);
    }

    protected boolean erase() {
        final EditorState state = editor.erase();
        return !isEmpty(state.text);
    }

    @Override
    protected void onStartErase() {
        wasCalculatingOnFly = calculator.isCalculateOnFly();
        if (wasCalculatingOnFly) {
            calculator.setCalculateOnFly(false);
        }
    }

    @Override
    protected void onStopErase() {
        if (wasCalculatingOnFly) {
            calculator.setCalculateOnFly(true);
        }
    }
}
