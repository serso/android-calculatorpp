package org.solovyev.android.calculator.keyboard;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import jscl.NumeralBase;
import jscl.math.Expression;
import jscl.math.Generic;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.buttons.CppSpecialButton;
import org.solovyev.android.calculator.history.History;
import org.solovyev.android.calculator.view.AngleUnitsButton;
import org.solovyev.android.calculator.view.NumeralBasesButton;
import org.solovyev.android.views.dragbutton.DirectionDragButton;
import org.solovyev.android.views.dragbutton.DirectionDragImageButton;
import org.solovyev.android.views.dragbutton.DirectionDragView;
import org.solovyev.android.views.dragbutton.DragDirection;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static org.solovyev.android.calculator.Engine.Preferences.*;
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
    History history;
    @Inject
    ActivityLauncher launcher;
    @Inject
    Engine engine;
    @Inject
    Display display;
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
    DirectionDragImageButton historyButton;
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
    @Bind(R.id.cpp_button_copy)
    NumeralBasesButton copyButton;
    @Nullable
    @Bind(R.id.cpp_button_paste)
    AngleUnitsButton pasteButton;
    @Nullable
    @Bind(R.id.cpp_button_like)
    ImageButton likeButton;
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

    public void toggleNumericDigits() {
        final boolean hex = numeralBase.getPreference(preferences) == NumeralBase.hex;
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

        if (copyButton != null) {
            prepareButton(copyButton);
            copyButton.setNumeralBase(numeralBase.getPreference(preferences));
        }
        if (pasteButton != null) {
            prepareButton(pasteButton);
            pasteButton.setAngleUnit(angleUnit.getPreference(preferences));
        }
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
        toggleNumericDigits();
    }

    @Override
    public void onDestroyView() {
        partialUi.onDestroyView();
        super.onDestroyView();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        super.onSharedPreferenceChanged(preferences, key);
        if (angleUnit.isSameKey(key) && pasteButton != null) {
            pasteButton.setAngleUnit(angleUnit.getPreference(preferences));
        }
        if (numeralBase.isSameKey(key)) {
            toggleNumericDigits();
            if (copyButton != null) {
                copyButton.setNumeralBase(numeralBase.getPreference(preferences));
            }
        }
        if (multiplicationSign.isSameKey(key)) {
            multiplicationButton.setText(multiplicationSign.getPreference(preferences));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cpp_button_0:
            case R.id.cpp_button_1:
            case R.id.cpp_button_2:
            case R.id.cpp_button_3:
            case R.id.cpp_button_4:
            case R.id.cpp_button_5:
            case R.id.cpp_button_6:
            case R.id.cpp_button_7:
            case R.id.cpp_button_8:
            case R.id.cpp_button_9:
            case R.id.cpp_button_division:
            case R.id.cpp_button_period:
            case R.id.cpp_button_percent:
            case R.id.cpp_button_subtraction:
            case R.id.cpp_button_multiplication:
            case R.id.cpp_button_plus:
            case R.id.cpp_button_round_brackets:
                onClick(v, ((Button) v).getText().toString());
                break;
            case R.id.cpp_button_functions:
                onClick(v, CppSpecialButton.functions);
                break;
            case R.id.cpp_button_history:
                onClick(v, CppSpecialButton.history);
                break;
            case R.id.cpp_button_paste:
                onClick(v, CppSpecialButton.paste);
                break;
            case R.id.cpp_button_copy:
                onClick(v, CppSpecialButton.copy);
                break;
            case R.id.cpp_button_like:
                onClick(v, CppSpecialButton.like);
                break;
            case R.id.cpp_button_memory:
                onClick(v, CppSpecialButton.memory);
                break;
            case R.id.cpp_button_operators:
                onClick(v, CppSpecialButton.operators);
                break;
            case R.id.cpp_button_vars:
                onClick(v, CppSpecialButton.vars);
                break;
        }
    }

    @Override
    protected boolean onDrag(@NonNull View view, @NonNull DragDirection direction) {
        switch (view.getId()) {
            case R.id.cpp_button_functions:
                if (direction == up) {
                    launcher.showFunctionEditor();
                    return true;
                } else if (direction == down) {
                    launcher.showConstantEditor();
                    return true;
                }
                return false;
            case R.id.cpp_button_history:
                if (direction == up) {
                    history.undo();
                    return true;
                } else if (direction == down) {
                    history.redo();
                    return true;
                }
                return false;
            case R.id.cpp_button_memory:
                return processMemoryButton(direction);
            case R.id.cpp_button_round_brackets:
                if (direction == left) {
                    keyboard.roundBracketsButtonPressed();
                    return true;
                }
                return processDefault(direction, (DirectionDragView) view);
            default:
                return processDefault(direction, (DirectionDragView) view);
        }
    }

    private boolean processMemoryButton(@NonNull DragDirection direction) {
        final DisplayState state = display.getState();
        if (!state.valid) {
            return false;
        }
        Generic value = state.getResult();
        if (value == null) {
            try {
                value = Expression.valueOf(state.text);
            } catch (jscl.text.ParseException e) {
                Log.w(App.TAG, e.getMessage(), e);
            }
        }
        if (value == null) {
            memory.get().requestShow();
            return false;
        }
        switch (direction) {
            case up:
                memory.get().add(value);
                return true;
            case down:
                memory.get().subtract(value);
                return true;
        }
        return false;
    }

    private boolean processDefault(@Nonnull DragDirection direction, @Nonnull DirectionDragView button) {
        final String text = button.getText(direction).getValue();
        return keyboard.buttonPressed(text);
    }
}