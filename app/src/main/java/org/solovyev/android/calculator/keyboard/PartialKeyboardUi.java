package org.solovyev.android.calculator.keyboard;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.view.EditorLongClickEraser;
import org.solovyev.android.views.dragbutton.DirectionDragButton;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static org.solovyev.android.calculator.Preferences.Gui.vibrateOnKeypress;
import static org.solovyev.android.views.dragbutton.DragDirection.down;

public class PartialKeyboardUi extends BaseKeyboardUi {

    @Nullable
    DirectionDragButton rightButton;
    @Nullable
    DirectionDragButton leftButton;
    @Nullable
    DirectionDragButton clearButton;
    @Nullable
    DirectionDragButton eraseButton;
    @Nullable
    DirectionDragButton equalsButton;
    @Nullable
    EditorLongClickEraser longClickEraser;

    @Inject
    public PartialKeyboardUi(@NonNull Application application) {
        super(application);
    }

    @Override
    public void onCreateView(@Nonnull Activity activity, @Nonnull View view) {
        super.onCreateView(activity, view);
        rightButton = view.findViewById(R.id.cpp_button_right);
        leftButton = view.findViewById(R.id.cpp_button_left);
        clearButton = view.findViewById(R.id.cpp_button_clear);
        eraseButton = view.findViewById(R.id.cpp_button_erase);
        equalsButton = view.findViewById(R.id.cpp_button_equals);
        prepareButton(rightButton);
        prepareButton(leftButton);
        prepareButton(equalsButton);
        prepareButton(clearButton);
        if (eraseButton != null) {
            prepareButton(eraseButton);
            longClickEraser = EditorLongClickEraser.attachTo(eraseButton, keyboard.isVibrateOnKeypress(), editor, calculator);
        }
        if (isSimpleMode()) {
            hideText(equalsButton, down);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        super.onSharedPreferenceChanged(preferences, key);
        if (longClickEraser != null && vibrateOnKeypress.isSameKey(key)) {
            longClickEraser.setVibrateOnKeypress(vibrateOnKeypress.getPreference(preferences));
        }
    }
}
