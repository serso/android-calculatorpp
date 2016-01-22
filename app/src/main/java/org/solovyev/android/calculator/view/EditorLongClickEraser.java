package org.solovyev.android.calculator.view;

import android.view.View;
import org.solovyev.android.calculator.*;

import javax.annotation.Nonnull;

import static android.text.TextUtils.isEmpty;

public class EditorLongClickEraser extends BaseLongClickEraser {

    @Nonnull
    private final Editor editor = App.getEditor();

    @Nonnull
    private final Calculator calculator = Locator.getInstance().getCalculator();

    private boolean wasCalculatingOnFly;

    private EditorLongClickEraser(@Nonnull View view) {
        super(view);
    }

    public static void attachTo(@Nonnull View view) {
        new EditorLongClickEraser(view);
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
