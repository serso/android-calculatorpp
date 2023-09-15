package org.solovyev.android.calculator.keyboard;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import androidx.annotation.Nullable;
import android.view.View;
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

    public DirectionDragButton button0;
    public DirectionDragButton button1;
    public DirectionDragButton button2;
    public DirectionDragButton button3;
    public DirectionDragButton button4;
    public DirectionDragButton button5;
    public DirectionDragButton button6;
    public DirectionDragButton button7;
    public DirectionDragButton button8;
    public DirectionDragButton button9;
    @Inject
    Engine engine;
    @Inject
    Display display;
    @Inject
    Bus bus;
    @Inject
    PartialKeyboardUi partialUi;
    DirectionDragButton variablesButton;
    @Nullable
    DirectionDragButton operatorsButton;
    DirectionDragButton functionsButton;
    DirectionDragButton historyButton;
    DirectionDragButton multiplicationButton;
    DirectionDragButton plusButton;
    DirectionDragButton subtractionButton;
    DirectionDragButton divisionButton;
    DirectionDragButton periodButton;
    DirectionDragButton bracketsButton;
    @Nullable
    DirectionDragButton likeButton;
    @Nullable
    DirectionDragButton percentButton;
    @Nullable
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
        button0 = view.findViewById(R.id.cpp_button_0);
        button1 = view.findViewById(R.id.cpp_button_1);
        button2 = view.findViewById(R.id.cpp_button_2);
        button3 = view.findViewById(R.id.cpp_button_3);
        button4 = view.findViewById(R.id.cpp_button_4);
        button5 = view.findViewById(R.id.cpp_button_5);
        button6 = view.findViewById(R.id.cpp_button_6);
        button7 = view.findViewById(R.id.cpp_button_7);
        button8 = view.findViewById(R.id.cpp_button_8);
        button9 = view.findViewById(R.id.cpp_button_9);

        variablesButton = view.findViewById(R.id.cpp_button_vars);
        operatorsButton = view.findViewById(R.id.cpp_button_operators);
        functionsButton = view.findViewById(R.id.cpp_button_functions);
        historyButton = view.findViewById(R.id.cpp_button_history);
        multiplicationButton = view.findViewById(R.id.cpp_button_multiplication);
        plusButton = view.findViewById(R.id.cpp_button_plus);
        subtractionButton = view.findViewById(R.id.cpp_button_subtraction);
        divisionButton = view.findViewById(R.id.cpp_button_division);
        periodButton = view.findViewById(R.id.cpp_button_period);
        bracketsButton = view.findViewById(R.id.cpp_button_round_brackets);
        likeButton = view.findViewById(R.id.cpp_button_like);
        percentButton = view.findViewById(R.id.cpp_button_percent);
        memoryButton = view.findViewById(R.id.cpp_button_memory);

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
