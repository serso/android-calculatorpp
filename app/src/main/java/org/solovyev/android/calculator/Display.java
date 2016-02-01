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

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import dagger.Lazy;
import jscl.math.Generic;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.calculator.view.NumeralBaseConverterDialog;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import static org.solovyev.android.calculator.BaseFragment.addMenu;
import static org.solovyev.android.calculator.CalculatorEventType.*;

@Singleton
public class Display implements CalculatorEventListener, View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

    @Nonnull
    private final CalculatorEventHolder lastEvent;
    @Nonnull
    private final Bus bus;
    @Inject
    Lazy<Keyboard> keyboard;
    @Inject
    Lazy<Clipboard> clipboard;
    @Inject
    Lazy<Notifier> notifier;
    @Nullable
    private DisplayView view;
    @Nonnull
    private DisplayState state = DisplayState.empty();

    @Inject
    public Display(@Nonnull Calculator calculator, @Nonnull Bus bus) {
        this.bus = bus;
        lastEvent = new CalculatorEventHolder(CalculatorUtils.createFirstEventDataId());
        calculator.addCalculatorEventListener(this);
        bus.register(this);
    }

    @Subscribe
    public void onCopy(@Nonnull CopyOperation o) {
        if (!state.valid) {
            return;
        }
        clipboard.get().setText(state.text);
        notifier.get().showMessage(CalculatorMessage.newInfoMessage(CalculatorMessages.result_copied));
    }

    public void clearView(@Nonnull DisplayView view) {
        Check.isMainThread();
        if (this.view != view) {
            return;
        }
        this.view = null;
    }

    public void setView(@Nonnull DisplayView view) {
        Check.isMainThread();
        this.view = view;
        this.view.setOnClickListener(this);
        this.view.setState(state);
    }

    @Nonnull
    public DisplayState getState() {
        Check.isMainThread();
        return state;
    }

    public void setState(@Nonnull DisplayState newState) {
        Check.isMainThread();

        final DisplayState oldState = state;
        state = newState;
        if (view != null) {
            view.setState(newState);
        }
        bus.post(new ChangedEvent(oldState, newState));
    }

    @Override
    public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData,
                                  @Nonnull CalculatorEventType calculatorEventType,
                                  @Nullable Object data) {
        if (calculatorEventType.isOfType(calculation_result, calculation_failed, calculation_cancelled, conversion_result, conversion_failed)) {

            final CalculatorEventHolder.Result result = lastEvent.apply(calculatorEventData);

            if (result.isNewAfter()) {
                switch (calculatorEventType) {
                    case conversion_failed:
                        processConversationFailed((CalculatorConversionEventData) calculatorEventData, (ConversionFailure) data);
                        break;
                    case conversion_result:
                        processConversationResult((CalculatorConversionEventData) calculatorEventData, (String) data);
                        break;
                    case calculation_result:
                        processCalculationResult((CalculatorEvaluationEventData) calculatorEventData, (CalculatorOutput) data);
                        break;
                    case calculation_cancelled:
                        processCalculationCancelled((CalculatorEvaluationEventData) calculatorEventData);
                        break;
                    case calculation_failed:
                        processCalculationFailed((CalculatorEvaluationEventData) calculatorEventData, (CalculatorFailure) data);
                        break;
                }
            }
        }
    }

    private void processConversationFailed(@Nonnull CalculatorConversionEventData calculatorEventData,
                                           @Nonnull ConversionFailure data) {
        setState(DisplayState.createError(calculatorEventData.getDisplayState().getOperation(), CalculatorMessages.getBundle().getString(CalculatorMessages.syntax_error), calculatorEventData.getSequenceId()));

    }

    private void processCalculationFailed(@Nonnull CalculatorEvaluationEventData calculatorEventData, @Nonnull CalculatorFailure data) {

        final CalculatorEvalException calculatorEvalException = data.getCalculationEvalException();

        final String errorMessage;
        if (calculatorEvalException != null) {
            errorMessage = CalculatorMessages.getBundle().getString(CalculatorMessages.syntax_error);
        } else {
            final ParseException calculationParseException = data.getCalculationParseException();
            if (calculationParseException != null) {
                errorMessage = calculationParseException.getLocalizedMessage();
            } else {
                errorMessage = CalculatorMessages.getBundle().getString(CalculatorMessages.syntax_error);
            }
        }

        setState(DisplayState.createError(calculatorEventData.getOperation(), errorMessage, calculatorEventData.getSequenceId()));
    }

    private void processCalculationCancelled(@Nonnull CalculatorEvaluationEventData calculatorEventData) {
        final String errorMessage = CalculatorMessages.getBundle().getString(CalculatorMessages.syntax_error);
        setState(DisplayState.createError(calculatorEventData.getOperation(), errorMessage, calculatorEventData.getSequenceId()));
    }

    private void processCalculationResult(@Nonnull CalculatorEvaluationEventData calculatorEventData, @Nonnull CalculatorOutput data) {
        final String stringResult = data.getStringResult();
        setState(DisplayState.createValid(calculatorEventData.getOperation(), data.getResult(), stringResult, calculatorEventData.getSequenceId()));
    }

    private void processConversationResult(@Nonnull CalculatorConversionEventData calculatorEventData, @Nonnull String result) {
        // add prefix
        if (calculatorEventData.getFromNumeralBase() != calculatorEventData.getToNumeralBase()) {
            result = calculatorEventData.getToNumeralBase().getJsclPrefix() + result;
        }

        final DisplayState displayState = calculatorEventData.getDisplayState();
        setState(DisplayState.createValid(displayState.getOperation(), displayState.getResult(), result, calculatorEventData.getSequenceId()));
    }

    @Override
    public void onClick(View v) {
        if (state.valid) {
            v.setOnCreateContextMenuListener(this);
            v.showContextMenu();
            v.setOnCreateContextMenuListener(null);
        } else {
            Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_evaluation_error, state.text);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (!state.valid) {
            return;
        }
        addMenu(menu, R.string.c_copy, this);

        final Generic result = state.getResult();
        final JsclOperation operation = state.getOperation();
        if (result != null) {
            if (ConversionMenuItem.convert_to_bin.isItemVisibleFor(result, operation)) {
                addMenu(menu, R.string.convert_to_bin, this);
            }
            if (ConversionMenuItem.convert_to_dec.isItemVisibleFor(result, operation)) {
                addMenu(menu, R.string.convert_to_dec, this);
            }
            if (ConversionMenuItem.convert_to_hex.isItemVisibleFor(result, operation)) {
                addMenu(menu, R.string.convert_to_hex, this);
            }
            if (operation == JsclOperation.numeric && result.getConstants().isEmpty()) {
                addMenu(menu, R.string.c_convert, this);
            }
            if (Locator.getInstance().getPlotter().isPlotPossibleFor(result)) {
                addMenu(menu, R.string.c_plot, this);
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        final Generic result = state.getResult();
        switch (item.getItemId()) {
            case R.string.c_copy:
                keyboard.get().copyButtonPressed();
                return true;
            case R.string.convert_to_bin:
                ConversionMenuItem.convert_to_bin.onClick(state, App.getApplication());
                return true;
            case R.string.convert_to_dec:
                ConversionMenuItem.convert_to_dec.onClick(state, App.getApplication());
                return true;
            case R.string.convert_to_hex:
                ConversionMenuItem.convert_to_hex.onClick(state, App.getApplication());
                return true;
            case R.string.c_convert:
                if (result != null) {
                    new NumeralBaseConverterDialog(result.toString()).show(App.getApplication());
                }
                return true;
            case R.string.c_plot:
                if (result != null) {
                    Locator.getInstance().getPlotter().plot(result);
                }
                return true;
            default:
                return false;
        }
    }

    public static class CopyOperation {
    }

    public static class ChangedEvent {

        @Nonnull
        public final DisplayState oldState;

        @Nonnull
        public final DisplayState newState;

        public ChangedEvent(@Nonnull DisplayState oldState, @Nonnull DisplayState newState) {
            this.oldState = oldState;
            this.newState = newState;
        }
    }
}
