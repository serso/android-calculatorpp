package org.solovyev.android.calculator.keyboard;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.view.View;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import jscl.NumeralBase;
import org.solovyev.android.calculator.Display;
import org.solovyev.android.calculator.Engine;
import org.solovyev.android.calculator.Keyboard;
import org.solovyev.android.calculator.R;
import org.solovyev.android.views.dragbutton.DirectionDragButton;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static org.solovyev.android.calculator.Engine.Preferences.multiplicationSign;
import static org.solovyev.android.views.dragbutton.DragDirection.*;

public class KeyboardUi extends BaseKeyboardUi {

    @Bind(R.id.cpp_button_0)
    public DirectionDragButton button0;
    @Bind(R.id.cpp_button_1)
    public DirectionDragButton button1;
    @Bind(R.id.cpp_button_2)
    public DirectionDragButton button2;
    @Bind(R.id.cpp_button_3)
    public DirectionDragButton button3;
    @Bind(R.id.cpp_button_4)
    public DirectionDragButton button4;
    @Bind(R.id.cpp_button_5)
    public DirectionDragButton button5;
    @Bind(R.id.cpp_button_6)
    public DirectionDragButton button6;
    @Bind(R.id.cpp_button_7)
    public DirectionDragButton button7;
    @Bind(R.id.cpp_button_8)
    public DirectionDragButton button8;
    @Bind(R.id.cpp_button_9)
    public DirectionDragButton button9;
    @Inject
    Engine engine;
    @Inject
    Display display;
    @Inject
    Bus bus;
    @Inject
    PartialKeyboardUi partialUi;
    @Bind(R.id.cpp_button_vars)
    DirectionDragButton variablesButton;
    @Nullable
    @Bind(R.id.cpp_button_operators)
    DirectionDragButton operatorsButton;
    @Bind(R.id.cpp_button_functions)
    DirectionDragButton functionsButton;
    @Bind(R.id.cpp_button_history)
    DirectionDragButton historyButton;
    @Bind(R.id.cpp_button_multiplication)
    DirectionDragButton multiplicationButton;
    @Bind(R.id.cpp_button_plus)
    DirectionDragButton plusButton;
    @Bind(R.id.cpp_button_subtraction)
    DirectionDragButton subtractionButton;
    @Bind(R.id.cpp_button_division)
    DirectionDragButton divisionButton;
    @Bind(R.id.cpp_button_period)
    DirectionDragButton periodButton;
    @Bind(R.id.cpp_button_round_brackets)
    DirectionDragButton bracketsButton;
    @Nullable
    @Bind(R.id.cpp_button_like)
    DirectionDragButton likeButton;
    @Nullable
    @Bind(R.id.cpp_button_percent)
    DirectionDragButton percentButton;
    @Nullable
    @Bind(R.id.cpp_button_memory)
    DirectionDragButton memoryButton;

    @Inject
    public KeyboardUi(@Nonnull Application application) {
        super(application);
    }

    public void updateNumberMode(@Nonnull NumeralBase mode) {
        final boolean hex = mode == NumeralBase.hex;
        button1.setShowDirectionText(left, hex);
        button2.setShowDirectionText(left, hex);
        button3.setShowDirectionText(left, hex);
        button4.setShowDirectionText(left, hex);
        button5.setShowDirectionText(left, hex);
        button6.setShowDirectionText(left, hex);
    }

    public void onCreateView(@Nonnull Activity activity, @Nonnull View view) {
        super.onCreateView(activity, view);
        partialUi.onCreateView(activity, view);
        ButterKnife.bind(this, view);

        prepareButton(variablesButton);
        prepareButton(operatorsButton);
        prepareButton(functionsButton);
        prepareButton(historyButton);

        prepareButton(multiplicationButton);
        prepareButton(plusButton);
        prepareButton(subtractionButton);
        prepareButton(divisionButton);

        prepareButton(periodButton);
        prepareButton(bracketsButton);
        prepareButton(percentButton);

        prepareButton(button0);
        prepareButton(button1);
        prepareButton(button2);
        prepareButton(button3);
        prepareButton(button4);
        prepareButton(button5);
        prepareButton(button6);
        prepareButton(button7);
        prepareButton(button8);
        prepareButton(button9);

        prepareButton(likeButton);
        prepareButton(memoryButton);

        if (isSimpleMode()) {
            hideText(button1, down);
            hideText(button2, down);
            hideText(button3, down);
            hideText(button4, down);
            hideText(button5, down);
            hideText(button6, up);
            hideText(button7, left, up, down);
            hideText(button8, left, up, down);
            hideText(button9, left);
            hideText(multiplicationButton, left);
            hideText(plusButton, up);
            hideText(functionsButton, up, down);
        }
        multiplicationButton.setText(engine.getMultiplicationSign());
        updateNumberMode(keyboard.getNumberMode());
        bus.register(this);
    }

    @Override
    public void onDestroyView() {
        bus.unregister(this);
        partialUi.onDestroyView();
        super.onDestroyView();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        super.onSharedPreferenceChanged(preferences, key);
        if (multiplicationSign.isSameKey(key)) {
            multiplicationButton.setText(multiplicationSign.getPreference(preferences));
        }
    }

    @Subscribe
    public void onNumberModeChanged(@Nonnull Keyboard.NumberModeChangedEvent e) {
        updateNumberMode(e.mode);
    }
}