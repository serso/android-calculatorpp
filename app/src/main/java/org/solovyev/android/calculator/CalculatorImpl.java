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

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import jscl.AbstractJsclArithmeticException;
import jscl.MathEngine;
import jscl.NumeralBase;
import jscl.NumeralBaseException;
import jscl.math.Generic;
import jscl.math.function.IConstant;
import jscl.math.operator.Operator;
import jscl.text.ParseInterruptedException;
import org.solovyev.android.calculator.functions.FunctionsRegistry;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.calculator.units.CalculatorNumeralBase;
import org.solovyev.android.calculator.variables.CppVariable;
import org.solovyev.common.msg.ListMessageRegistry;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageRegistry;
import org.solovyev.common.msg.MessageType;
import org.solovyev.common.text.Strings;
import org.solovyev.common.units.ConversionException;
import org.solovyev.common.units.Conversions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class CalculatorImpl implements Calculator, CalculatorEventListener {

    // one minute
    private static final long PREFERENCE_CHECK_INTERVAL = 1000L * 60L;

    @Nonnull
    private final CalculatorEventContainer calculatorEventContainer = new ListCalculatorEventContainer();

    @Nonnull
    private final AtomicLong counter = new AtomicLong(CalculatorUtils.FIRST_ID);

    @Nonnull
    private final ToJsclTextProcessor preprocessor = ToJsclTextProcessor.getInstance();

    @Nonnull
    private final Executor calculationsExecutor = Executors.newFixedThreadPool(10);

    @Nonnull
    private final Executor eventExecutor;

    private volatile boolean calculateOnFly = true;

    private volatile long lastPreferenceCheck = 0L;


    public CalculatorImpl(@Nonnull Bus bus, @Nonnull Executor eventExecutor) {
        this.eventExecutor = eventExecutor;
        bus.register(this);
        this.addCalculatorEventListener(this);
    }


    @Nonnull
    private static String doConversion(@Nonnull Generic generic,
                                       @Nonnull NumeralBase from,
                                       @Nonnull NumeralBase to) throws ConversionException {
        final String result;

        if (from != to) {
            String fromString = generic.toString();
            if (!Strings.isEmpty(fromString)) {
                try {
                    fromString = ToJsclTextProcessor.getInstance().process(fromString).getExpression();
                } catch (ParseException e) {
                    // ok, problems while processing occurred
                }
            }


            result = Conversions.doConversion(CalculatorNumeralBase.getConverter(), fromString, CalculatorNumeralBase.valueOf(from), CalculatorNumeralBase.valueOf(to));
        } else {
            result = generic.toString();
        }

        return result;
    }

    @Nonnull
    private CalculatorEventData nextEventData() {
        long eventId = counter.incrementAndGet();
        return CalculatorEventDataImpl.newInstance(eventId, eventId);
    }

    @Nonnull
    private CalculatorEventData nextEventData(@Nonnull Object source) {
        long eventId = counter.incrementAndGet();
        return CalculatorEventDataImpl.newInstance(eventId, eventId, source);
    }

	@Nonnull
    private CalculatorEventData nextEventData(@Nonnull Long sequenceId) {
        long eventId = counter.incrementAndGet();
        return CalculatorEventDataImpl.newInstance(eventId, sequenceId);
    }

    @Override
    public void evaluate() {
        final  EditorState viewState = getEditor().getState();
        final CalculatorEventData eventData = fireCalculatorEvent(CalculatorEventType.manual_calculation_requested, viewState);
        this.evaluate(JsclOperation.numeric, viewState.getTextString(), eventData.getSequenceId());
    }

    @Override
    public void evaluate(@Nonnull Long sequenceId) {
        final  EditorState viewState = getEditor().getState();
        fireCalculatorEvent(CalculatorEventType.manual_calculation_requested, viewState, sequenceId);
        this.evaluate(JsclOperation.numeric, viewState.getTextString(), sequenceId);
    }

    @Override
    public void simplify() {
        final  EditorState viewState = getEditor().getState();
        final CalculatorEventData eventData = fireCalculatorEvent(CalculatorEventType.manual_calculation_requested, viewState);
        this.evaluate(JsclOperation.simplify, viewState.getTextString(), eventData.getSequenceId());
    }

    @Nonnull
    @Override
    public CalculatorEventData evaluate(@Nonnull final JsclOperation operation,
                                        @Nonnull final String expression) {

        final CalculatorEventData eventDataId = nextEventData();

        calculationsExecutor.execute(new Runnable() {
            @Override
            public void run() {
                CalculatorImpl.this.evaluate(eventDataId.getSequenceId(), operation, expression, null);
            }
        });

        return eventDataId;
    }

    @Nonnull
    @Override
    public CalculatorEventData evaluate(@Nonnull final JsclOperation operation, @Nonnull final String expression, long sequenceId) {
        final CalculatorEventData eventDataId = nextEventData(sequenceId);

        calculationsExecutor.execute(new Runnable() {
            @Override
            public void run() {
                CalculatorImpl.this.evaluate(eventDataId.getSequenceId(), operation, expression, null);
            }
        });

        return eventDataId;
    }

    @Override
    public void init(@Nonnull Executor initThread) {
        Locator.getInstance().getEngine().init(initThread);
    }

    @Override
    public boolean isCalculateOnFly() {
        return calculateOnFly;
    }

    @Override
    public void setCalculateOnFly(boolean calculateOnFly) {
        if (this.calculateOnFly != calculateOnFly) {
            this.calculateOnFly = calculateOnFly;
            if (this.calculateOnFly) {
                evaluate();
            }
        }
    }

    @Nonnull
    private CalculatorConversionEventData newConversionEventData(@Nonnull Long sequenceId,
                                                                 @Nonnull Generic value,
                                                                 @Nonnull NumeralBase from,
                                                                 @Nonnull NumeralBase to,
                                                                 @Nonnull DisplayState displayViewState) {
        return CalculatorConversionEventDataImpl.newInstance(nextEventData(sequenceId), value, from, to, displayViewState);
    }

    private void evaluate(@Nonnull Long sequenceId,
                          @Nonnull JsclOperation operation,
                          @Nonnull String expression,
                          @Nullable MessageRegistry mr) {

        checkPreferredPreferences();

        PreparedExpression preparedExpression = null;

        try {

            expression = expression.trim();

            if (Strings.isEmpty(expression)) {
                fireCalculatorEvent(newCalculationEventData(operation, expression, sequenceId), CalculatorEventType.calculation_result, CalculatorOutputImpl.newEmptyOutput(operation));
            } else {
                preparedExpression = prepareExpression(expression);

                final String jsclExpression = preparedExpression.toString();

                try {

                    final MathEngine mathEngine = Locator.getInstance().getEngine().getMathEngine();

                    final MessageRegistry messageRegistry = new ListMessageRegistry();
                    Locator.getInstance().getEngine().getMathEngine().setMessageRegistry(messageRegistry);

                    final Generic result = operation.evaluateGeneric(jsclExpression, mathEngine);

                    // NOTE: toString() method must be called here as ArithmeticOperationException may occur in it (just to avoid later check!)
                    result.toString();

                    if (messageRegistry.hasMessage()) {
                        final ErrorReporter errorReporter = Locator.getInstance().getErrorReporter();
                        try {
                            final List<Message> messages = new ArrayList<Message>();
                            while (messageRegistry.hasMessage()) {
                                messages.add(messageRegistry.getMessage());
                            }
                            if (!messages.isEmpty()) {
                                fireCalculatorEvent(newCalculationEventData(operation, expression, sequenceId), CalculatorEventType.calculation_messages, messages);
                            }
                        } catch (Throwable e) {
                            // todo serso: not good but we need proper synchronization
                            Log.e("Calculator", e.getMessage(), e);
                        }
                    }

                    final CalculatorOutput data = CalculatorOutputImpl.newOutput(operation.getFromProcessor().process(result), operation, result);
                    fireCalculatorEvent(newCalculationEventData(operation, expression, sequenceId), CalculatorEventType.calculation_result, data);

                } catch (AbstractJsclArithmeticException e) {
                    handleException(sequenceId, operation, expression, mr, new CalculatorEvalException(e, e, jsclExpression));
                }
            }

        } catch (ArithmeticException e) {
            handleException(sequenceId, operation, expression, mr, preparedExpression, new ParseException(expression, new CalculatorMessage(CalculatorMessages.msg_001, MessageType.error, e.getMessage())));
        } catch (StackOverflowError e) {
            handleException(sequenceId, operation, expression, mr, preparedExpression, new ParseException(expression, new CalculatorMessage(CalculatorMessages.msg_002, MessageType.error)));
        } catch (jscl.text.ParseException e) {
            handleException(sequenceId, operation, expression, mr, preparedExpression, new ParseException(e));
        } catch (ParseInterruptedException e) {

            // do nothing - we ourselves interrupt the calculations
            fireCalculatorEvent(newCalculationEventData(operation, expression, sequenceId), CalculatorEventType.calculation_cancelled, null);

        } catch (ParseException e) {
            handleException(sequenceId, operation, expression, mr, preparedExpression, e);
        }
    }

    private void checkPreferredPreferences() {
        final long currentTime = System.currentTimeMillis();

        if (currentTime - lastPreferenceCheck > PREFERENCE_CHECK_INTERVAL) {
            lastPreferenceCheck = currentTime;
            Locator.getInstance().getPreferenceService().checkPreferredPreferences(false);
        }
    }

    @Nonnull
    @Override
    public PreparedExpression prepareExpression(@Nonnull String expression) throws ParseException {
        return preprocessor.process(expression);
    }

    @Nonnull
    private CalculatorEventData newCalculationEventData(@Nonnull JsclOperation operation,
                                                        @Nonnull String expression,
                                                        @Nonnull Long calculationId) {
        return new CalculatorEvaluationEventDataImpl(nextEventData(calculationId), operation, expression);
    }

    private void handleException(@Nonnull Long sequenceId,
                                 @Nonnull JsclOperation operation,
                                 @Nonnull String expression,
                                 @Nullable MessageRegistry mr,
                                 @Nullable PreparedExpression preparedExpression,
                                 @Nonnull ParseException parseException) {

        if (operation == JsclOperation.numeric
                && preparedExpression != null
                && preparedExpression.isExistsUndefinedVar()) {

            evaluate(sequenceId, JsclOperation.simplify, expression, mr);
        } else {

            fireCalculatorEvent(newCalculationEventData(operation, expression, sequenceId), CalculatorEventType.calculation_failed, new CalculatorFailureImpl(parseException));
        }
    }

	/*
	**********************************************************************
	*
	*                           CONVERSION
	*
	**********************************************************************
	*/

    private void handleException(@Nonnull Long calculationId,
                                 @Nonnull JsclOperation operation,
                                 @Nonnull String expression,
                                 @Nullable MessageRegistry mr,
                                 @Nonnull CalculatorEvalException evalException) {

        if (operation == JsclOperation.numeric && evalException.getCause() instanceof NumeralBaseException) {
            evaluate(calculationId, JsclOperation.simplify, expression, mr);
        } else {
            fireCalculatorEvent(newCalculationEventData(operation, expression, calculationId), CalculatorEventType.calculation_failed, new CalculatorFailureImpl(evalException));
        }
    }

    @Nonnull
    @Override
    public CalculatorEventData convert(@Nonnull final Generic value,
                                       @Nonnull final NumeralBase to) {
        final CalculatorEventData eventDataId = nextEventData();

        final DisplayState displayViewState = App.getDisplay().getState();
        final NumeralBase from = Locator.getInstance().getEngine().getMathEngine().getNumeralBase();

        calculationsExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final Long sequenceId = eventDataId.getSequenceId();

                fireCalculatorEvent(newConversionEventData(sequenceId, value, from, to, displayViewState), CalculatorEventType.conversion_started, null);
                try {

                    final String result = doConversion(value, from, to);

                    fireCalculatorEvent(newConversionEventData(sequenceId, value, from, to, displayViewState), CalculatorEventType.conversion_result, result);

                } catch (ConversionException e) {
                    fireCalculatorEvent(newConversionEventData(sequenceId, value, from, to, displayViewState), CalculatorEventType.conversion_failed, new ConversionFailureImpl(e));
                }
            }
        });

        return eventDataId;
    }

    @Override
    public boolean isConversionPossible(@Nonnull Generic generic, NumeralBase from, @Nonnull NumeralBase to) {
        try {
            doConversion(generic, from, to);
            return true;
        } catch (ConversionException e) {
            return false;
        }
    }

	/*
	**********************************************************************
	*
	*                           EVENTS
	*
	**********************************************************************
	*/

    @Override
    public void addCalculatorEventListener(@Nonnull CalculatorEventListener calculatorEventListener) {
        calculatorEventContainer.addCalculatorEventListener(calculatorEventListener);
    }

    @Override
    public void removeCalculatorEventListener(@Nonnull CalculatorEventListener calculatorEventListener) {
        calculatorEventContainer.removeCalculatorEventListener(calculatorEventListener);
    }

    @Override
    public void fireCalculatorEvent(@Nonnull final CalculatorEventData calculatorEventData, @Nonnull final CalculatorEventType calculatorEventType, @Nullable final Object data) {
        eventExecutor.execute(new Runnable() {
            @Override
            public void run() {
                calculatorEventContainer.fireCalculatorEvent(calculatorEventData, calculatorEventType, data);
            }
        });
    }

    @Override
    public void fireCalculatorEvents(@Nonnull final List<CalculatorEvent> calculatorEvents) {
        eventExecutor.execute(new Runnable() {
            @Override
            public void run() {
                calculatorEventContainer.fireCalculatorEvents(calculatorEvents);
            }
        });
    }

    @Nonnull
    @Override
    public CalculatorEventData fireCalculatorEvent(@Nonnull final CalculatorEventType calculatorEventType, @Nullable final Object data) {
        final CalculatorEventData eventData = nextEventData();

        fireCalculatorEvent(eventData, calculatorEventType, data);

        return eventData;
    }

    @Nonnull
    @Override
    public CalculatorEventData fireCalculatorEvent(@Nonnull final CalculatorEventType calculatorEventType, @Nullable final Object data, @Nonnull Object source) {
        final CalculatorEventData eventData = nextEventData(source);

        fireCalculatorEvent(eventData, calculatorEventType, data);

        return eventData;
    }

    @Nonnull
    @Override
    public CalculatorEventData fireCalculatorEvent(@Nonnull final CalculatorEventType calculatorEventType, @Nullable final Object data, @Nonnull Long sequenceId) {
        final CalculatorEventData eventData = nextEventData(sequenceId);

        fireCalculatorEvent(eventData, calculatorEventType, data);

        return eventData;
    }

    @Subscribe
    public void onEditorChanged(@Nonnull Editor.ChangedEvent e) {
        if (!calculateOnFly) {
            return;
        }
        if (TextUtils.equals(e.newState.text, e.oldState.text)) {
            return;
        }
        evaluate(JsclOperation.numeric, e.newState.getTextString(), e.newState.sequence);
    }

    @Subscribe
    public void onDisplayChanged(@Nonnull Display.ChangedEvent e) {
        final DisplayState newState = e.newState;
        if (!newState.valid) {
            return;
        }
        final String text = newState.text;
        if (TextUtils.isEmpty(text)) {
            return;
        }
        updateAnsVariable(text);
    }

    private void updateAnsVariable(@NonNull String value) {
        final VariablesRegistry variablesRegistry = Locator.getInstance().getEngine().getVariablesRegistry();
        final IConstant variable = variablesRegistry.get(VariablesRegistry.ANS);

        final CppVariable.Builder b = variable != null ? CppVariable.builder(variable) : CppVariable.builder(VariablesRegistry.ANS);
        b.withValue(value);
        b.withSystem(true);
        b.withDescription(CalculatorMessages.getBundle().getString(CalculatorMessages.ans_description));

        variablesRegistry.add(b.build().toJsclBuilder(), variable);
    }

    @Subscribe
    public void onFunctionAdded(@Nonnull FunctionsRegistry.AddedEvent event) {
        evaluate();
    }

    @Subscribe
    public void onFunctionsChanged(@Nonnull FunctionsRegistry.ChangedEvent event) {
        evaluate();
    }

    @Subscribe
    public void onFunctionsRemoved(@Nonnull FunctionsRegistry.RemovedEvent event) {
        evaluate();
    }

    @Subscribe
    public void onVariableRemoved(@NonNull VariablesRegistry.RemovedEvent e) {
        evaluate();
    }

    @Subscribe
    public void onVariableAdded(@NonNull VariablesRegistry.AddedEvent e) {
        evaluate();
    }

    @Subscribe
    public void onVariableChanged(@NonNull VariablesRegistry.ChangedEvent e) {
        if (!e.newVariable.getName().equals(VariablesRegistry.ANS)) {
            evaluate();
        }
    }

    @Override
    public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {

        switch (calculatorEventType) {
            case use_operator:
                final Operator operator = (Operator) data;
                Locator.getInstance().getKeyboard().buttonPressed(operator.getName());
                break;

        }
    }

    @Nonnull
    private Editor getEditor() {
        return App.getEditor();
    }

}
