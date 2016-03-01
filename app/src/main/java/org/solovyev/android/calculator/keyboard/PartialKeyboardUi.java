package org.solovyev.android.calculator.keyboard;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import org.solovyev.android.calculator.Engine;
import org.solovyev.android.calculator.Preferences;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.buttons.CppSpecialButton;
import org.solovyev.android.calculator.view.EditorLongClickEraser;
import org.solovyev.android.calculator.view.NumeralBasesButton;
import org.solovyev.android.views.dragbutton.DirectionDragButton;
import org.solovyev.android.views.dragbutton.DragButton;
import org.solovyev.android.views.dragbutton.DragDirection;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import jscl.NumeralBase;

import static org.solovyev.android.calculator.Engine.Preferences.numeralBase;
import static org.solovyev.android.calculator.Preferences.Gui.showEqualsButton;
import static org.solovyev.android.calculator.Preferences.Gui.vibrateOnKeypress;
import static org.solovyev.android.views.dragbutton.DragDirection.down;
import static org.solovyev.android.views.dragbutton.DragDirection.left;
import static org.solovyev.android.views.dragbutton.DragDirection.up;

public class PartialKeyboardUi extends BaseKeyboardUi {

    @Nullable
    @Bind(R.id.cpp_button_right)
    DirectionDragButton rightButton;
    @Nullable
    @Bind(R.id.cpp_button_left)
    DirectionDragButton leftButton;
    @Nullable
    @Bind(R.id.cpp_button_clear)
    NumeralBasesButton clearButton;
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
        if (clearButton != null) {
            clearButton.setNumeralBase(numeralBase.getPreference(preferences));
        }
        if (eraseButton != null) {
            // backspace button is too big, scale it more
            prepareButton(eraseButton, IMAGE_SCALE_ERASE);
            longClickEraser = EditorLongClickEraser.attachTo(eraseButton, keyboard.isVibrateOnKeypress(), editor, calculator);
        }
        if (isSimpleLayout()) {
            hideText(clearButton, left, up, down);
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
        if (clearButton != null && numeralBase.isSameKey(key)) {
            clearButton.setNumeralBase(numeralBase.getPreference(preferences));
        }
        if (equalsButton != null && showEqualsButton.isSameKey(key)) {
            toggleEqualsButton();
        }
        if (longClickEraser != null && vibrateOnKeypress.isSameKey(key)) {
            longClickEraser.setVibrateOnKeypress(vibrateOnKeypress.getPreference(preferences));
        }
    }

    @Override
    public boolean processDragEvent(@Nonnull DragDirection direction, @Nonnull DragButton button, @Nonnull PointF point, @Nonnull MotionEvent event) {
        switch (button.getId()) {
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
            case R.id.cpp_button_clear:
                return processNumeralBaseButton(direction, (DirectionDragButton) button);
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cpp_button_left:
            case R.id.cpp_button_right:
                onClick(v, ((Button) v).getText().toString());
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

    private boolean processNumeralBaseButton(@Nonnull DragDirection direction, @Nonnull DirectionDragButton button) {
        final String text = button.getText(direction);
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        try {
            final NumeralBase newNumeralBase = NumeralBase.valueOf(text);
            final NumeralBase oldNumeralBase = Engine.Preferences.numeralBase.getPreference(preferences);
            if (oldNumeralBase != newNumeralBase) {
                preferredPreferences.setNumeralBase(newNumeralBase);
                return true;
            }
        } catch (IllegalArgumentException e) {
            Log.d(this.getClass().getName(), "Unsupported numeral base: " + text);
        }
        return false;
    }
}
