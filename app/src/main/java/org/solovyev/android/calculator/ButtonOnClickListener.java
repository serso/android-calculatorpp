package org.solovyev.android.calculator;

import android.support.annotation.IdRes;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;

import org.solovyev.android.calculator.view.ViewsCache;

import javax.annotation.Nonnull;

final class ButtonOnClickListener implements View.OnClickListener {

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
            case R.id.cpp_button_left:
            case R.id.cpp_button_subtraction:
            case R.id.cpp_button_multiplication:
            case R.id.cpp_button_plus:
            case R.id.cpp_button_right:
            case R.id.cpp_button_round_brackets:
                onClick(v, ((Button) v).getText().toString());
                break;
            case R.id.cpp_button_clear:
                onClick(v, CalculatorSpecialButton.clear);
                break;
            case R.id.cpp_button_functions:
                onClick(v, CalculatorSpecialButton.functions);
                break;
            case R.id.cpp_button_history:
                onClick(v, CalculatorSpecialButton.history);
                break;
            case R.id.cpp_button_erase:
                onClick(v, CalculatorSpecialButton.erase);
                break;
            case R.id.cpp_button_paste:
                onClick(v, CalculatorSpecialButton.paste);
                break;
            case R.id.cpp_button_copy:
                onClick(v, CalculatorSpecialButton.copy);
                break;
            case R.id.cpp_button_like:
                onClick(v, CalculatorSpecialButton.like);
                break;
            case R.id.cpp_button_operators:
                onClick(v, CalculatorSpecialButton.operators);
                break;
            case R.id.cpp_button_vars:
                onClick(v, CalculatorSpecialButton.vars);
                break;
            case R.id.cpp_button_equals:
                onClick(v, CalculatorSpecialButton.equals);
                break;
        }
    }

    private void onClick(@Nonnull View v, @Nonnull CalculatorSpecialButton b) {
        onClick(v, b.getActionCode());
    }

    private void onClick(@Nonnull View v, @Nonnull String s) {
        if (Locator.getInstance().getKeyboard().buttonPressed(s)) {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }
    }

    public void attachToViews(@Nonnull ViewsCache views) {
        attachToView(views, R.id.cpp_button_0);
        attachToView(views, R.id.cpp_button_1);
        attachToView(views, R.id.cpp_button_2);
        attachToView(views, R.id.cpp_button_3);
        attachToView(views, R.id.cpp_button_4);
        attachToView(views, R.id.cpp_button_5);
        attachToView(views, R.id.cpp_button_6);
        attachToView(views, R.id.cpp_button_7);
        attachToView(views, R.id.cpp_button_8);
        attachToView(views, R.id.cpp_button_9);
        attachToView(views, R.id.cpp_button_division);
        attachToView(views, R.id.cpp_button_period);
        attachToView(views, R.id.cpp_button_left);
        attachToView(views, R.id.cpp_button_subtraction);
        attachToView(views, R.id.cpp_button_multiplication);
        attachToView(views, R.id.cpp_button_plus);
        attachToView(views, R.id.cpp_button_right);
        attachToView(views, R.id.cpp_button_round_brackets);
        attachToView(views, R.id.cpp_button_clear);
        attachToView(views, R.id.cpp_button_functions);
        attachToView(views, R.id.cpp_button_history);
        attachToView(views, R.id.cpp_button_erase);
        attachToView(views, R.id.cpp_button_paste);
        attachToView(views, R.id.cpp_button_copy);
        attachToView(views, R.id.cpp_button_like);
        attachToView(views, R.id.cpp_button_operators);
        attachToView(views, R.id.cpp_button_vars);
        attachToView(views, R.id.cpp_button_equals);
    }

    private void attachToView(@Nonnull ViewsCache views, @IdRes int viewId) {
        final View view = views.findViewById(viewId);
        if (view != null) {
            view.setOnClickListener(this);
        }
    }
}
