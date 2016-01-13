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

import com.squareup.otto.Bus;

import org.solovyev.android.Check;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import static org.solovyev.android.calculator.CalculatorEventType.calculation_cancelled;
import static org.solovyev.android.calculator.CalculatorEventType.calculation_failed;
import static org.solovyev.android.calculator.CalculatorEventType.calculation_result;
import static org.solovyev.android.calculator.CalculatorEventType.conversion_failed;
import static org.solovyev.android.calculator.CalculatorEventType.conversion_result;

@Singleton
public class Display implements CalculatorEventListener {

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

    @Nonnull
    private final CalculatorEventHolder lastEvent;
    @Nullable
    private DisplayView view;
    @Nonnull
    private DisplayState state = DisplayState.empty();

    @Inject
    Bus bus;

    @Inject
    public Display(@Nonnull Calculator calculator) {
        this.lastEvent = new CalculatorEventHolder(CalculatorUtils.createFirstEventDataId());
        calculator.addCalculatorEventListener(this);
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
            final CalculatorParseException calculationParseException = data.getCalculationParseException();
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
}
