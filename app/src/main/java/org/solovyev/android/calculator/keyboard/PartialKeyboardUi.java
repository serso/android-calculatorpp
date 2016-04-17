package org.solovyev.android.calculator.keyboard;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.view.EditorLongClickEraser;
import org.solovyev.android.views.dragbutton.DirectionDragButton;
import org.solovyev.android.views.dragbutton.DirectionDragImageButton;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static org.solovyev.android.calculator.Preferences.Gui.vibrateOnKeypress;
import static org.solovyev.android.views.dragbutton.DragDirection.down;

public class PartialKeyboardUi extends BaseKeyboardUi {

    @Nullable
    @Bind(R.id.cpp_button_right)
    DirectionDragImageButton rightButton;
    @Nullable
    @Bind(R.id.cpp_button_left)
    DirectionDragImageButton leftButton;
    @Nullable
    @Bind(R.id.cpp_button_clear)
    DirectionDragButton clearButton;
    @Nullable
    @Bind(R.id.cpp_button_erase)
    ImageButton eraseButton;
    @Nullable
    @Bind(R.id.cpp_button_equals)
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
        ButterKnife.bind(this, view);
        prepareButton(rightButton);
        prepareButton(leftButton);
        prepareButton(equalsButton);
        prepareButton(clearButton);
        if (eraseButton != null) {
            // backspace button is too big, scale it more
            prepareButton(eraseButton, IMAGE_SCALE_ERASE);
            longClickEraser = EditorLongClickEraser.attachTo(eraseButton, keyboard.isVibrateOnKeypress(), editor, calculator);
        }
        if(isSimpleMode()) {
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
