/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.CursorControl;
import org.solovyev.android.calculator.history.AndroidCalculatorHistoryImpl;
import org.solovyev.android.calculator.history.CalculatorHistoryState;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.history.HistoryControl;
import org.solovyev.common.history.HistoryAction;
import org.solovyev.common.text.StringUtils;

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

    @NotNull
    private CalculatorEngine calculatorEngine;

    private CalculatorModel() {
        display = CalculatorLocatorImpl.getInstance().getCalculatorDisplay();
        editor = CalculatorLocatorImpl.getInstance().getCalculatorEditor();
    }

    public CalculatorModel init(@NotNull final Activity activity, @NotNull SharedPreferences preferences, @NotNull CalculatorEngine calculator) {
        Log.d(this.getClass().getName(), "CalculatorModel initialization with activity: " + activity);
        this.calculatorEngine = calculator;

        final AndroidCalculatorEditorView editorView = (AndroidCalculatorEditorView) activity.findViewById(R.id.calculatorEditor);
        editorView.init(preferences);
        preferences.registerOnSharedPreferenceChangeListener(editorView);
        editor.setView(editorView);

        final AndroidCalculatorDisplayView displayView = (AndroidCalculatorDisplayView) activity.findViewById(R.id.calculatorDisplay);
        displayView.setOnClickListener(new CalculatorDisplayOnClickListener(activity));
        display.setView(displayView);

        final CalculatorHistoryState lastState = AndroidCalculatorHistoryImpl.instance.getLastHistoryState();
        if (lastState == null) {
            saveHistoryState();
        } else {
            setCurrentHistoryState(lastState);
        }


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
        if (viewState.isValid()) {
            final CharSequence text = viewState.getText();
            if (!StringUtils.isEmpty(text)) {
                final ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
                clipboard.setText(text.toString());
                Toast.makeText(context, context.getText(R.string.c_result_copied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveHistoryState() {
        AndroidCalculatorHistoryImpl.instance.addState(getCurrentHistoryState());
    }

    public void doTextOperation(@NotNull TextOperation operation) {
        operation.doOperation(CalculatorLocatorImpl.getInstance().getCalculatorEditor());
    }

    public void processDigitButtonAction(@Nullable final String text) {

        if (!StringUtils.isEmpty(text)) {
            doTextOperation(new CalculatorModel.TextOperation() {

                @Override
                public void doOperation(@NotNull CalculatorEditor editor) {
                    int cursorPositionOffset = 0;
                    final StringBuilder textToBeInserted = new StringBuilder(text);

                    final MathType.Result mathType = MathType.getType(text, 0, false);
                    switch (mathType.getMathType()) {
                        case function:
                            textToBeInserted.append("()");
                            cursorPositionOffset = -1;
                            break;
                        case operator:
                            textToBeInserted.append("()");
                            cursorPositionOffset = -1;
                            break;
                        case comma:
                            textToBeInserted.append(" ");
                            break;
                    }

                    if (cursorPositionOffset == 0) {
                        if (MathType.openGroupSymbols.contains(text)) {
                            cursorPositionOffset = -1;
                        }
                    }

                    editor.insert(textToBeInserted.toString());
                    editor.moveSelection(cursorPositionOffset);
                }
            });
        }
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

    public void clear() {
        // todo serso:
    }

    public static interface TextOperation {

        void doOperation(@NotNull CalculatorEditor editor);

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
