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

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import jscl.*;
import jscl.math.Generic;
import jscl.math.function.Constants;
import jscl.math.function.IConstant;
import jscl.text.ParseInterruptedException;
import org.solovyev.android.calculator.calculations.CalculationCancelledEvent;
import org.solovyev.android.calculator.calculations.CalculationFailedEvent;
import org.solovyev.android.calculator.calculations.CalculationFinishedEvent;
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
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Singleton
public class Calculator implements CalculatorEventListener, SharedPreferences.OnSharedPreferenceChangeListener {

    public static final long NO_SEQUENCE = -1;
    private static final long PREFERENCE_CHECK_INTERVAL = TimeUnit.MINUTES.toMillis(15);

    @Nonnull
    private final CalculatorEventContainer calculatorEventContainer = new ListCalculatorEventContainer();
    @Nonnull
    private static final AtomicLong SEQUENCER = new AtomicLong(NO_SEQUENCE);
    @Nonnull
    private final ToJsclTextProcessor preprocessor = ToJsclTextProcessor.getInstance();
    @Nonnull
    private final SharedPreferences preferences;
    @Nonnull
    private final Bus bus;
    @Nonnull
    private final Executor ui;
    @Nonnull
    private final Executor background;

    private volatile boolean calculateOnFly = true;

    private long lastPreferredPreferenceCheck = 0L;

    @Inject
    PreferredPreferences preferredPreferences;
    @Inject
    Editor editor;
    @Inject
    JsclMathEngine mathEngine;

    @Inject
    public Calculator(@Nonnull SharedPreferences preferences, @Nonnull Bus bus, @Named(AppModule.THREAD_UI) @Nonnull Executor ui, @Named(AppModule.THREAD_BACKGROUND) @Nonnull Executor background) {
        this.preferences = preferences;
        this.bus = bus;
        this.ui = ui;
        this.background = background;
        bus.register(this);
        addCalculatorEventListener(this);
        preferences.registerOnSharedPreferenceChangeListener(this);
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
                    fromString = ToJsclTextProcessor.getInstance().process(fromString).getValue();
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
        final long eventId = nextSequence();
        return CalculatorEventDataImpl.newInstance(eventId, eventId);
    }

    @Nonnull
    private CalculatorEventData nextEventData(@Nonnull Object source) {
        long eventId = nextSequence();
        return CalculatorEventDataImpl.newInstance(eventId, eventId, source);
    }

	@Nonnull
    private CalculatorEventData nextEventData(@Nonnull Long sequenceId) {
        long eventId = nextSequence();
        return CalculatorEventDataImpl.newInstance(eventId, sequenceId);
    }

    public void evaluate() {
        final EditorState state = editor.getState();
        final CalculatorEventData eventData = fireCalculatorEvent(CalculatorEventType.manual_calculation_requested, state);
        evaluate(JsclOperation.numeric, state.getTextString(), eventData.getSequenceId());
    }

    public void simplify() {
        final EditorState state = editor.getState();
        final CalculatorEventData eventData = fireCalculatorEvent(CalculatorEventType.manual_calculation_requested, state);
        evaluate(JsclOperation.simplify, state.getTextString(), eventData.getSequenceId());
    }

    @Nonnull
    public CalculatorEventData evaluate(@Nonnull final JsclOperation operation,
                                        @Nonnull final String expression) {

        final CalculatorEventData eventDataId = nextEventData();

        background.execute(new Runnable() {
            @Override
            public void run() {
                evaluateAsync(eventDataId.getSequenceId(), operation, expression);
            }
        });

        return eventDataId;
    }

    @Nonnull
    public CalculatorEventData evaluate(@Nonnull final JsclOperation operation, @Nonnull final String expression, long sequenceId) {
        final CalculatorEventData eventDataId = nextEventData(sequenceId);

        background.execute(new Runnable() {
            @Override
            public void run() {
                evaluateAsync(eventDataId.getSequenceId(), operation, expression);
            }
        });

        return eventDataId;
    }

    public void init(@Nonnull Executor init) {
        Locator.getInstance().getEngine().init(init);
        setCalculateOnFly(Preferences.Calculations.calculateOnFly.getPreference(preferences));
    }

    public boolean isCalculateOnFly() {
        return calculateOnFly;
    }

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

    private void evaluateAsync(long sequence, @Nonnull JsclOperation o, @Nonnull String e) {
        evaluateAsync(sequence, o, e, new ListMessageRegistry());
    }

    private void evaluateAsync(long sequence,
                               @Nonnull JsclOperation o,
                               @Nonnull String e,
                               @Nonnull MessageRegistry mr) {
        e = e.trim();
        if (Strings.isEmpty(e)) {
            bus.post(new CalculationFinishedEvent(o, e, sequence));
            return;
        }

        checkPreferredPreferences();
        PreparedExpression pe = null;
        try {
            pe = prepare(e);

            try {
                Locator.getInstance().getEngine().getMathEngine().setMessageRegistry(mr);

                final Generic result = o.evaluateGeneric(pe.value, mathEngine);

                // NOTE: toString() method must be called here as ArithmeticOperationException may occur in it (just to avoid later check!)
                //noinspection ResultOfMethodCallIgnored
                result.toString();

                final String stringResult = o.getFromProcessor().process(result);
                bus.post(new CalculationFinishedEvent(o, e, sequence, result, stringResult, collectMessages(mr)));

            } catch (JsclArithmeticException exception) {
                if (o == JsclOperation.numeric && exception.getCause() instanceof NumeralBaseException) {
                    evaluateAsync(sequence, JsclOperation.simplify, e, mr);
                } else {
                    bus.post(new CalculationFailedEvent(o, e, sequence, exception));
                }
            }
        } catch (ArithmeticException exception) {
            onException(sequence, o, e, mr, pe, new ParseException(e, new CalculatorMessage(CalculatorMessages.msg_001, MessageType.error, exception.getMessage())));
        } catch (StackOverflowError exception) {
            onException(sequence, o, e, mr, pe, new ParseException(e, new CalculatorMessage(CalculatorMessages.msg_002, MessageType.error)));
        } catch (jscl.text.ParseException exception) {
            onException(sequence, o, e, mr, pe, new ParseException(exception));
        } catch (ParseInterruptedException exception) {
            bus.post(new CalculationCancelledEvent(o, e, sequence));
        } catch (ParseException exception) {
            onException(sequence, o, e, mr, pe, exception);
        }
    }

    @Nonnull
    private List<Message> collectMessages(@Nonnull MessageRegistry mr) {
        if (mr.hasMessage()) {
            try {
                final List<Message> messages = new ArrayList<>();
                while (mr.hasMessage()) {
                    messages.add(mr.getMessage());
                }
                return messages;
            } catch (Throwable exception) {
                // several threads might use the same instance of MessageRegistry, as no proper synchronization is done
                // catch Throwable here
                Log.e("Calculator", exception.getMessage(), exception);
            }
        }
        return Collections.emptyList();
    }

    private void checkPreferredPreferences() {
        if (shouldCheckPreferredPreferences()) {
            preferredPreferences.check(false);
        }
    }

    private synchronized boolean shouldCheckPreferredPreferences() {
        final long now = System.currentTimeMillis();

        if (now - lastPreferredPreferenceCheck > PREFERENCE_CHECK_INTERVAL) {
            lastPreferredPreferenceCheck = now;
            return true;
        }
        return false;
    }

    @Nonnull
    public PreparedExpression prepare(@Nonnull String expression) throws ParseException {
        return preprocessor.process(expression);
    }

    private void onException(long sequence,
                             @Nonnull JsclOperation operation,
                             @Nonnull String e,
                             @Nonnull MessageRegistry mr,
                             @Nullable PreparedExpression pe,
                             @Nonnull ParseException parseException) {
        if (operation == JsclOperation.numeric
                && pe != null
                && pe.hasUndefinedVariables()) {
            evaluateAsync(sequence, JsclOperation.simplify, e, mr);
            return;
        }
        bus.post(new CalculationFailedEvent(operation, e, sequence, parseException));
    }

    @Nonnull
    public CalculatorEventData convert(@Nonnull final Generic value,
                                       @Nonnull final NumeralBase to) {
        final CalculatorEventData eventDataId = nextEventData();

        final DisplayState displayViewState = App.getDisplay().getState();
        final NumeralBase from = Locator.getInstance().getEngine().getMathEngine().getNumeralBase();

        background.execute(new Runnable() {
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

    public boolean isConversionPossible(@Nonnull Generic generic, NumeralBase from, @Nonnull NumeralBase to) {
        try {
            doConversion(generic, from, to);
            return true;
        } catch (ConversionException e) {
            return false;
        }
    }

    public void addCalculatorEventListener(@Nonnull CalculatorEventListener calculatorEventListener) {
        calculatorEventContainer.addCalculatorEventListener(calculatorEventListener);
    }

    public void removeCalculatorEventListener(@Nonnull CalculatorEventListener calculatorEventListener) {
        calculatorEventContainer.removeCalculatorEventListener(calculatorEventListener);
    }

    public void fireCalculatorEvent(@Nonnull final CalculatorEventData calculatorEventData, @Nonnull final CalculatorEventType calculatorEventType, @Nullable final Object data) {
        ui.execute(new Runnable() {
            @Override
            public void run() {
                calculatorEventContainer.fireCalculatorEvent(calculatorEventData, calculatorEventType, data);
            }
        });
    }

    @Nonnull
    public CalculatorEventData fireCalculatorEvent(@Nonnull final CalculatorEventType calculatorEventType, @Nullable final Object data) {
        final CalculatorEventData eventData = nextEventData();

        fireCalculatorEvent(eventData, calculatorEventType, data);

        return eventData;
    }

    @Nonnull
    public CalculatorEventData fireCalculatorEvent(@Nonnull final CalculatorEventType calculatorEventType, @Nullable final Object data, @Nonnull Object source) {
        final CalculatorEventData eventData = nextEventData(source);

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
        final IConstant variable = variablesRegistry.get(Constants.ANS);

        final CppVariable.Builder b = variable != null ? CppVariable.builder(variable) : CppVariable.builder(Constants.ANS);
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
        if (!e.newVariable.getName().equals(Constants.ANS)) {
            evaluate();
        }
    }

    @Override
    public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {
        switch (calculatorEventType) {
            case show_history:
                ActivityLauncher.showHistory(App.getApplication());
                break;
            case show_history_detached:
                ActivityLauncher.showHistory(App.getApplication(), true);
                break;
            case show_functions:
                ActivityLauncher.showFunctions(App.getApplication());
                break;
            case show_functions_detached:
                ActivityLauncher.showFunctions(App.getApplication(), true);
                break;
            case show_operators:
                ActivityLauncher.showOperators(App.getApplication());
                break;
            case show_operators_detached:
                ActivityLauncher.showOperators(App.getApplication(), true);
                break;
            case show_vars:
                ActivityLauncher.showVars(App.getApplication());
                break;
            case show_vars_detached:
                ActivityLauncher.showVars(App.getApplication(), true);
                break;
            case show_settings:
                ActivityLauncher.showSettings(App.getApplication());
                break;
            case show_settings_detached:
                ActivityLauncher.showSettings(App.getApplication(), true);
                break;
            case show_settings_widget:
                ActivityLauncher.showWidgetSettings(App.getApplication(), true);
                break;
            case show_like_dialog:
                ActivityLauncher.likeButtonPressed(App.getApplication());
                break;
            case open_app:
                ActivityLauncher.openApp(App.getApplication());
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(@Nonnull SharedPreferences prefs, @Nonnull String key) {
        if (Preferences.Calculations.calculateOnFly.getKey().equals(key)) {
            setCalculateOnFly(Preferences.Calculations.calculateOnFly.getPreference(prefs));
        }
    }

    public static long nextSequence() {
        return SEQUENCER.incrementAndGet();
    }
}
