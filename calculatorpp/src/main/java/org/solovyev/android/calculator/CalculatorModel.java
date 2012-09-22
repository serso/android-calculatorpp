/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.CursorControl;
import org.solovyev.android.calculator.history.AndroidCalculatorHistoryImpl;
import org.solovyev.android.calculator.history.CalculatorHistoryState;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.history.HistoryControl;
import org.solovyev.common.history.HistoryAction;

/**
 * User: serso
 * Date: 9/12/11
 * Time: 11:15 PM
 */
public enum CalculatorModel implements HistoryControl<CalculatorHistoryState>, CalculatorEngineControl, CursorControl {

    instance;

    // millis to wait before evaluation after user edit action
    public static final int EVAL_DELAY_MILLIS = 0;

    @NotNull
    private final CalculatorEditor editor;

    @NotNull
    private final CalculatorDisplay display;

    private CalculatorModel() {
        display = CalculatorLocatorImpl.getInstance().getCalculatorDisplay();
        editor = CalculatorLocatorImpl.getInstance().getCalculatorEditor();
    }

    public CalculatorModel init(@NotNull final Activity activity, @NotNull SharedPreferences preferences) {
        Log.d(this.getClass().getName(), "CalculatorModel initialization with activity: " + activity);

        final AndroidCalculatorEditorView editorView = (AndroidCalculatorEditorView) activity.findViewById(R.id.calculatorEditor);
        editorView.init(preferences);
        preferences.registerOnSharedPreferenceChangeListener(editorView);
        editor.setView(editorView);

        final AndroidCalculatorDisplayView displayView = (AndroidCalculatorDisplayView) activity.findViewById(R.id.calculatorDisplay);
        displayView.setOnClickListener(new CalculatorDisplayOnClickListener(activity));
        display.setView(displayView);

        return this;
    }

    public static void showEvaluationError(@NotNull Activity activity, @NotNull final String errorMessage) {
        final LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        final View errorMessageView = layoutInflater.inflate(R.layout.display_error_message, null);
        ((TextView) errorMessageView.findViewById(R.id.error_message_text_view)).setText(errorMessage);

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setPositiveButton(R.string.c_cancel, null)
                .setView(errorMessageView);

        builder.create().show();
    }

    public void copyResult(@NotNull Context context) {
        copyResult(context, display.getViewState());
    }

    public static void copyResult(@NotNull Context context,
                                  @NotNull final CalculatorDisplayViewState viewState) {
        CalculatorLocatorImpl.getInstance().getCalculatorKeyboard().copyButtonPressed();
    }

    private void saveHistoryState() {
        AndroidCalculatorHistoryImpl.instance.addState(getCurrentHistoryState());
    }

    @Override
    public void setCursorOnStart() {
        this.editor.setCursorOnStart();
    }

    @Override
    public void setCursorOnEnd() {
        this.editor.setCursorOnEnd();
    }

    @Override
    public void moveCursorLeft() {
        this.editor.moveCursorLeft();
    }

    @Override
    public void moveCursorRight() {
        this.editor.moveCursorRight();
    }

    @Override
    public void evaluate() {
        CalculatorLocatorImpl.getInstance().getCalculator().evaluate(JsclOperation.numeric, this.editor.getViewState().getText());
    }

    @Override
    public void simplify() {
        CalculatorLocatorImpl.getInstance().getCalculator().evaluate(JsclOperation.simplify, this.editor.getViewState().getText());
    }

    @Override
    public void doHistoryAction(@NotNull HistoryAction historyAction) {
        synchronized (AndroidCalculatorHistoryImpl.instance) {
            if (AndroidCalculatorHistoryImpl.instance.isActionAvailable(historyAction)) {
                final CalculatorHistoryState newState = AndroidCalculatorHistoryImpl.instance.doAction(historyAction, getCurrentHistoryState());
                if (newState != null) {
                    setCurrentHistoryState(newState);
                }
            }
        }
    }

    @Override
    public void setCurrentHistoryState(@NotNull CalculatorHistoryState editorHistoryState) {
        synchronized (AndroidCalculatorHistoryImpl.instance) {
            Log.d(this.getClass().getName(), "Saved history found: " + editorHistoryState);

            editorHistoryState.setValuesFromHistory(this.editor, this.display);
        }
    }

    @Override
    @NotNull
    public CalculatorHistoryState getCurrentHistoryState() {
        synchronized (AndroidCalculatorHistoryImpl.instance) {
            return CalculatorHistoryState.newInstance(this.editor, display);
        }
    }

    @NotNull
    public CalculatorDisplay getDisplay() {
        return display;
    }

}
