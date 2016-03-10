package org.solovyev.android.calculator.keyboard;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import org.solovyev.android.calculator.Preferences;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.buttons.CppSpecialButton;
import org.solovyev.android.calculator.view.EditorLongClickEraser;
import org.solovyev.android.views.dragbutton.DirectionDragButton;
import org.solovyev.android.views.dragbutton.DirectionDragImageButton;
import org.solovyev.android.views.dragbutton.DragDirection;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

import static org.solovyev.android.calculator.Preferences.Gui.showEqualsButton;
import static org.solovyev.android.calculator.Preferences.Gui.vibrateOnKeypress;
import static org.solovyev.android.views.dragbutton.DragDirection.down;
import static org.solovyev.android.views.dragbutton.DragDirection.up;

public class PartialKeyboardUi extends BaseKeyboardUi {

    @Nullable
    @Bind(R.id.cpp_button_right)
    DirectionDragImageButton rightButton;
    @Nullable
    @Bind(R.id.cpp_button_left)
    DirectionDragImageButton leftButton;
    @Nullable
    @Bind(R.id.cpp_button_clear)
    Button clearButton;
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
        toggleEqualsButton();
    }

    public void toggleEqualsButton() {
        if (equalsButton == null) {
            return;
        }
        if (orientation != Configuration.ORIENTATION_PORTRAIT && Preferences.Gui.rotateScreen.getPreference(preferences)) {
            return;
        }

        if (Preferences.Gui.showEqualsButton.getPreference(preferences)) {
            equalsButton.setVisibility(View.VISIBLE);
        } else {
            equalsButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        super.onSharedPreferenceChanged(preferences, key);
        if (equalsButton != null && showEqualsButton.isSameKey(key)) {
            toggleEqualsButton();
        }
        if (longClickEraser != null && vibrateOnKeypress.isSameKey(key)) {
            longClickEraser.setVibrateOnKeypress(vibrateOnKeypress.getPreference(preferences));
        }
    }

    @Override
    protected boolean onDrag(@NonNull View view, @NonNull DragDirection direction) {
        switch (view.getId()) {
            case R.id.cpp_button_right:
                editor.setCursorOnEnd();
                return true;
            case R.id.cpp_button_left:
                editor.setCursorOnStart();
                return true;
            case R.id.cpp_button_equals:
                if (direction == down) {
                    launcher.plotDisplayedExpression();
                    return true;
                } else if (direction == up) {
                    calculator.simplify();
                    return true;
                }

                return false;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cpp_button_left:
                onClick(v, CppSpecialButton.cursor_left);
                break;
            case R.id.cpp_button_right:
                onClick(v, CppSpecialButton.cursor_right);
                break;
            case R.id.cpp_button_clear:
                onClick(v, CppSpecialButton.clear);
                break;
            case R.id.cpp_button_erase:
                onClick(v, CppSpecialButton.erase);
                break;
            case R.id.cpp_button_equals:
                onClick(v, CppSpecialButton.equals);
                break;
        }
    }
}
