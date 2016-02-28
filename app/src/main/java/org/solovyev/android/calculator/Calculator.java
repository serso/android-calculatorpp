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
import jscl.JsclArithmeticException;
import jscl.JsclMathEngine;
import jscl.NumeralBase;
import jscl.NumeralBaseException;
import jscl.math.Generic;
import jscl.math.function.Constants;
import jscl.math.function.IConstant;
import jscl.text.ParseInterruptedException;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.calculations.*;
import org.solovyev.android.calculator.functions.FunctionsRegistry;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.calculator.variables.CppVariable;
import org.solovyev.common.msg.ListMessageRegistry;
import org.solovyev.common.msg.Message;
import org.solovyev.common.msg.MessageRegistry;
import org.solovyev.common.msg.MessageType;
import org.solovyev.common.text.Strings;
import org.solovyev.common.units.ConversionException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

@Singleton
public class Calculator implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final long NO_SEQUENCE = -1;

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
        preferences.registerOnSharedPreferenceChangeListener(this);
    }


    @Nonnull
    private static String convert(@Nonnull Generic generic, @Nonnull NumeralBase to) throws ConversionException {
        final BigInteger value = generic.toBigInteger();
        if (value == null) {
            throw new ConversionException();
        }
        return to.toString(value);
    }

    @Nonnull
    private CalculatorEventData nextEventData() {
        final long eventId = nextSequence();
        return CalculatorEventDataImpl.newInstance(eventId, eventId);
    }

    public void evaluate() {
        final EditorState state = editor.getState();
        evaluate(JsclOperation.numeric, state.getTextString());
    }

    public void simplify() {
        final EditorState state = editor.getState();
        evaluate(JsclOperation.simplify, state.getTextString());
    }

    public long evaluate(@Nonnull final JsclOperation operation,
            @Nonnull final String expression) {
        return evaluate(operation, expression, nextSequence());
    }

    public long evaluate(@Nonnull final JsclOperation operation, @Nonnull final String expression,
            final long sequence) {
        background.execute(new Runnable() {
            @Override
            public void run() {
                evaluateAsync(sequence, operation, expression);
            }
        });

        return sequence;
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

        preferredPreferences.check(false);
        PreparedExpression pe = null;
        try {
            pe = prepare(e);

            try {
                mathEngine.setMessageRegistry(mr);

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

    public void convert(@Nonnull final DisplayState state,  @Nonnull final NumeralBase to) {
        final Generic value = state.getResult();
        Check.isNotNull(value);
        final NumeralBase from = mathEngine.getNumeralBase();
        if (from == to) {
            return;
        }

        background.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final String result = convert(value, to);
                    bus.post(new ConversionFinishedEvent(result, to, state));
                } catch (ConversionException e) {
                    bus.post(new ConversionFailedEvent(state));
                }
            }
        });
    }

    public boolean canConvert(@Nonnull Generic generic, @NonNull NumeralBase from, @Nonnull NumeralBase to) {
        if(from == to) {
            return false;
        }
        try {
            convert(generic, to);
            return true;
        } catch (ConversionException e) {
            return false;
        }
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
    public void onSharedPreferenceChanged(@Nonnull SharedPreferences prefs, @Nonnull String key) {
        if (Preferences.Calculations.calculateOnFly.getKey().equals(key)) {
            setCalculateOnFly(Preferences.Calculations.calculateOnFly.getPreference(prefs));
        }
    }

    public static long nextSequence() {
        return SEQUENCER.incrementAndGet();
    }

}
