/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import jscl.NumeralBase;
import jscl.math.Generic;
import org.solovyev.android.calculator.history.HistoryState;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.common.history.HistoryAction;
import org.solovyev.common.msg.Message;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * User: serso
 * Date: 9/22/12
 * Time: 5:42 PM
 */
public class AndroidCalculator implements Calculator, CalculatorEventListener, SharedPreferences.OnSharedPreferenceChangeListener {

    @Nonnull
    private final CalculatorImpl calculator = new CalculatorImpl();

    @Nonnull
    private final Application context;

    public AndroidCalculator(@Nonnull Application application) {
        this.context = application;
        this.calculator.addCalculatorEventListener(this);

        PreferenceManager.getDefaultSharedPreferences(application).registerOnSharedPreferenceChangeListener(this);
    }

	/*
    **********************************************************************
	*
	*                           DELEGATED TO CALCULATOR
	*
	**********************************************************************
	*/

    @Override
    @Nonnull
    public CalculatorEventData evaluate(@Nonnull JsclOperation operation, @Nonnull String expression) {
        return calculator.evaluate(operation, expression);
    }

    @Override
    @Nonnull
    public CalculatorEventData evaluate(@Nonnull JsclOperation operation, @Nonnull String expression, @Nonnull Long sequenceId) {
        return calculator.evaluate(operation, expression, sequenceId);
    }

    @Override
    public boolean isCalculateOnFly() {
        return calculator.isCalculateOnFly();
    }

    @Override
    public void setCalculateOnFly(boolean calculateOnFly) {
        calculator.setCalculateOnFly(calculateOnFly);
    }

    @Override
    public boolean isConversionPossible(@Nonnull Generic generic, @Nonnull NumeralBase from, @Nonnull NumeralBase to) {
        return calculator.isConversionPossible(generic, from, to);
    }

    @Override
    @Nonnull
    public CalculatorEventData convert(@Nonnull Generic generic, @Nonnull NumeralBase to) {
        return calculator.convert(generic, to);
    }

    @Override
    @Nonnull
    public CalculatorEventData fireCalculatorEvent(@Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {
        return calculator.fireCalculatorEvent(calculatorEventType, data);
    }

    @Nonnull
    @Override
    public CalculatorEventData fireCalculatorEvent(@Nonnull CalculatorEventType calculatorEventType, @Nullable Object data, @Nonnull Object source) {
        return calculator.fireCalculatorEvent(calculatorEventType, data, source);
    }

    @Override
    @Nonnull
    public CalculatorEventData fireCalculatorEvent(@Nonnull CalculatorEventType calculatorEventType, @Nullable Object data, @Nonnull Long sequenceId) {
        return calculator.fireCalculatorEvent(calculatorEventType, data, sequenceId);
    }

    @Nonnull
    @Override
    public PreparedExpression prepareExpression(@Nonnull String expression) throws CalculatorParseException {
        return calculator.prepareExpression(expression);
    }

    @Override
    public void init() {
        this.calculator.init();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.calculator.setCalculateOnFly(Preferences.Calculations.calculateOnFly.getPreference(prefs));
    }

    @Override
    public void addCalculatorEventListener(@Nonnull CalculatorEventListener calculatorEventListener) {
        calculator.addCalculatorEventListener(calculatorEventListener);
    }

    @Override
    public void removeCalculatorEventListener(@Nonnull CalculatorEventListener calculatorEventListener) {
        calculator.removeCalculatorEventListener(calculatorEventListener);
    }

    @Override
    public void fireCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {
        calculator.fireCalculatorEvent(calculatorEventData, calculatorEventType, data);
    }

    @Override
    public void fireCalculatorEvents(@Nonnull List<CalculatorEvent> calculatorEvents) {
        calculator.fireCalculatorEvents(calculatorEvents);
    }

    @Override
    public void doHistoryAction(@Nonnull HistoryAction historyAction) {
        calculator.doHistoryAction(historyAction);
    }

    @Override
    @Nonnull
    public HistoryState getCurrentHistoryState() {
        return calculator.getCurrentHistoryState();
    }

    @Override
    public void setCurrentHistoryState(@Nonnull HistoryState editorHistoryState) {
        calculator.setCurrentHistoryState(editorHistoryState);
    }

    @Override
    public void evaluate() {
        calculator.evaluate();
    }

    @Override
    public void evaluate(@Nonnull Long sequenceId) {
        calculator.evaluate(sequenceId);
    }

    @Override
    public void simplify() {
        calculator.simplify();
    }

    @Override
    public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {
        switch (calculatorEventType) {
            case calculation_messages:
                CalculatorActivityLauncher.showCalculationMessagesDialog(App.getApplication(), (List<Message>) data);
                break;
            case show_history:
                CalculatorActivityLauncher.showHistory(App.getApplication());
                break;
            case show_history_detached:
                CalculatorActivityLauncher.showHistory(App.getApplication(), true);
                break;
            case show_functions:
                CalculatorActivityLauncher.showFunctions(App.getApplication());
                break;
            case show_functions_detached:
                CalculatorActivityLauncher.showFunctions(App.getApplication(), true);
                break;
            case show_operators:
                CalculatorActivityLauncher.showOperators(App.getApplication());
                break;
            case show_operators_detached:
                CalculatorActivityLauncher.showOperators(App.getApplication(), true);
                break;
            case show_vars:
                CalculatorActivityLauncher.showVars(App.getApplication());
                break;
            case show_vars_detached:
                CalculatorActivityLauncher.showVars(App.getApplication(), true);
                break;
            case show_settings:
                CalculatorActivityLauncher.showSettings(App.getApplication());
                break;
            case show_settings_detached:
                CalculatorActivityLauncher.showSettings(App.getApplication(), true);
                break;
            case show_settings_widget:
                CalculatorActivityLauncher.showWidgetSettings(App.getApplication(), true);
                break;
            case show_like_dialog:
                CalculatorActivityLauncher.likeButtonPressed(App.getApplication());
                break;
            case open_app:
                CalculatorActivityLauncher.openApp(App.getApplication());
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(@Nonnull SharedPreferences prefs, @Nonnull String key) {
        if (Preferences.Calculations.calculateOnFly.getKey().equals(key)) {
            this.calculator.setCalculateOnFly(Preferences.Calculations.calculateOnFly.getPreference(prefs));
        }
    }
}
