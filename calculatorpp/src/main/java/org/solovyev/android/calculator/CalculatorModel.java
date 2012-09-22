/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.common.gui.CursorControl;

/**
 * User: serso
 * Date: 9/12/11
 * Time: 11:15 PM
 */
public enum CalculatorModel implements CalculatorEngineControl, CursorControl {

    instance;

    @NotNull
    private final CalculatorEditor editor;

    @NotNull
    private final CalculatorDisplay display;

    private CalculatorModel() {
        display = CalculatorLocatorImpl.getInstance().getDisplay();
        editor = CalculatorLocatorImpl.getInstance().getEditor();
    }

    public CalculatorModel attachViews(@NotNull final Activity activity, @NotNull SharedPreferences preferences) {
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


    @NotNull
    public CalculatorDisplay getDisplay() {
        return display;
    }

}
