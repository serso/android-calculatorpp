package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.SharedPreferences;
import jscl.NumeralBase;
import jscl.math.Generic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.history.CalculatorHistoryState;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.common.history.HistoryAction;

import java.util.List;

/**
 * User: serso
 * Date: 9/22/12
 * Time: 5:42 PM
 */
public class AndroidCalculator implements Calculator {

    @NotNull
    private final Calculator calculator = new CalculatorImpl();

    public void init(@NotNull final Activity activity, @NotNull SharedPreferences preferences) {
        final AndroidCalculatorEditorView editorView = (AndroidCalculatorEditorView) activity.findViewById(R.id.calculatorEditor);
        editorView.init(preferences);
        preferences.registerOnSharedPreferenceChangeListener(editorView);
        CalculatorLocatorImpl.getInstance().getEditor().setView(editorView);

        final AndroidCalculatorDisplayView displayView = (AndroidCalculatorDisplayView) activity.findViewById(R.id.calculatorDisplay);
        displayView.setOnClickListener(new CalculatorDisplayOnClickListener(activity));
        CalculatorLocatorImpl.getInstance().getDisplay().setView(displayView);
    }


    /*
    **********************************************************************
    *
    *                           DELEGATED TO CALCULATOR
    *
    **********************************************************************
    */

    @Override
    @NotNull
    public CalculatorEventDataId evaluate(@NotNull JsclOperation operation, @NotNull String expression) {
        return calculator.evaluate(operation, expression);
    }

    @Override
    @NotNull
    public CalculatorEventDataId evaluate(@NotNull JsclOperation operation, @NotNull String expression, @NotNull Long sequenceId) {
        return calculator.evaluate(operation, expression, sequenceId);
    }

    @Override
    @NotNull
    public CalculatorEventDataId convert(@NotNull Generic generic, @NotNull NumeralBase to) {
        return calculator.convert(generic, to);
    }

    @Override
    @NotNull
    public CalculatorEventDataId fireCalculatorEvent(@NotNull CalculatorEventType calculatorEventType, @Nullable Object data) {
        return calculator.fireCalculatorEvent(calculatorEventType, data);
    }

    @Override
    @NotNull
    public CalculatorEventDataId fireCalculatorEvent(@NotNull CalculatorEventType calculatorEventType, @Nullable Object data, @NotNull Long sequenceId) {
        return calculator.fireCalculatorEvent(calculatorEventType, data, sequenceId);
    }

    @Override
    public void init() {
        this.calculator.init();
    }

    @Override
    public void addCalculatorEventListener(@NotNull CalculatorEventListener calculatorEventListener) {
        calculator.addCalculatorEventListener(calculatorEventListener);
    }

    @Override
    public void removeCalculatorEventListener(@NotNull CalculatorEventListener calculatorEventListener) {
        calculator.removeCalculatorEventListener(calculatorEventListener);
    }

    @Override
    public void fireCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable Object data) {
        calculator.fireCalculatorEvent(calculatorEventData, calculatorEventType, data);
    }

    @Override
    public void fireCalculatorEvents(@NotNull List<CalculatorEvent> calculatorEvents) {
        calculator.fireCalculatorEvents(calculatorEvents);
    }

    @Override
    public void doHistoryAction(@NotNull HistoryAction historyAction) {
        calculator.doHistoryAction(historyAction);
    }

    @Override
    public void setCurrentHistoryState(@NotNull CalculatorHistoryState editorHistoryState) {
        calculator.setCurrentHistoryState(editorHistoryState);
    }

    @Override
    @NotNull
    public CalculatorHistoryState getCurrentHistoryState() {
        return calculator.getCurrentHistoryState();
    }

    @Override
    public void evaluate() {
        calculator.evaluate();
    }

    @Override
    public void simplify() {
        calculator.simplify();
    }

}
