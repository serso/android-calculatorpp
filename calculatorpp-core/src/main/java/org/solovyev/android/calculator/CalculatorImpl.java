package org.solovyev.android.calculator;

import jscl.AbstractJsclArithmeticException;
import jscl.NumeralBase;
import jscl.NumeralBaseException;
import jscl.math.Generic;
import jscl.text.ParseInterruptedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.calculator.text.TextProcessor;
import org.solovyev.common.msg.MessageRegistry;
import org.solovyev.common.msg.MessageType;
import org.solovyev.common.text.StringUtils;
import org.solovyev.math.units.UnitConverter;
import org.solovyev.math.units.UnitImpl;
import org.solovyev.math.units.UnitType;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 16:42
 */
public class CalculatorImpl implements Calculator, CalculatorEventListener {

    private static final long FIRST_ID = 0;

    @NotNull
    private final CalculatorEventContainer calculatorEventContainer = new ListCalculatorEventContainer();

    @NotNull
    private final AtomicLong counter = new AtomicLong(FIRST_ID);

    @NotNull
    private final Object lock = new Object();

    @NotNull
    private final TextProcessor<PreparedExpression, String> preprocessor = ToJsclTextProcessor.getInstance();

    @NotNull
    private final Executor threadPoolExecutor = Executors.newFixedThreadPool(10);

    public CalculatorImpl() {
        this.addCalculatorEventListener(this);
    }

    @NotNull
    public static String doConversion(@NotNull UnitConverter<String> converter,
                                      @Nullable String from,
                                      @NotNull UnitType<String> fromUnitType,
                                      @NotNull UnitType<String> toUnitType) throws ConversionException {
        final String result;

        if (StringUtils.isEmpty(from)) {
            result = "";
        } else {

            String to = null;
            try {
                if (converter.isSupported(fromUnitType, toUnitType)) {
                    to = converter.convert(UnitImpl.newInstance(from, fromUnitType), toUnitType).getValue();
                }
            } catch (RuntimeException e) {
                throw new ConversionException(e);
            }

            result = to;
        }

        return result;
    }

    @NotNull
    private CalculatorEventDataId nextEventDataId() {
        long eventId = counter.incrementAndGet();
        return CalculatorEventDataIdImpl.newInstance(eventId, eventId);
    }

    @NotNull
    private CalculatorEventDataId nextEventDataId(@NotNull Long sequenceId) {
        long eventId = counter.incrementAndGet();
        return CalculatorEventDataIdImpl.newInstance(eventId, sequenceId);
    }

    /*
    **********************************************************************
    *
    *                           CALCULATION
    *
    **********************************************************************
    */

    @NotNull
    @Override
    public CalculatorEventDataId createFirstEventDataId() {
        return CalculatorEventDataIdImpl.newInstance(FIRST_ID, FIRST_ID);
    }

    @NotNull
    @Override
    public CalculatorEventDataId evaluate(@NotNull final JsclOperation operation,
                                          @NotNull final String expression) {

        final CalculatorEventDataId eventDataId = nextEventDataId();

        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                CalculatorImpl.this.evaluate(eventDataId.getSequenceId(), operation, expression, null);
            }
        });

        return eventDataId;
    }

    @NotNull
    @Override
    public CalculatorEventDataId evaluate(@NotNull final JsclOperation operation, @NotNull final String expression, @NotNull Long sequenceId) {
        final CalculatorEventDataId eventDataId = nextEventDataId(sequenceId);

        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                CalculatorImpl.this.evaluate(eventDataId.getSequenceId(), operation, expression, null);
            }
        });

        return eventDataId;
    }

    @NotNull
    @Override
    public CalculatorEventDataId convert(@NotNull final Generic generic,
                                         @NotNull final NumeralBase to) {
        final CalculatorEventDataId eventDataId = nextEventDataId();

        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final Long sequenceId = eventDataId.getSequenceId();

                fireCalculatorEvent(newConversionEventData(sequenceId), CalculatorEventType.conversion_started, null);

                final NumeralBase from = CalculatorLocatorImpl.getInstance().getCalculatorEngine().getEngine().getNumeralBase();

                if (from != to) {
                    String fromString = generic.toString();
                    if (!StringUtils.isEmpty(fromString)) {
                        try {
                            fromString = ToJsclTextProcessor.getInstance().process(fromString).getExpression();
                        } catch (CalculatorParseException e) {
                            // ok, problems while processing occurred
                        }
                    }

                    // todo serso: continue
                    //doConversion(AndroidNumeralBase.getConverter(), fromString, AndroidNumeralBase.valueOf(fromString), AndroidNumeralBase.valueOf(to));
                } else {
                    fireCalculatorEvent(newConversionEventData(sequenceId), CalculatorEventType.conversion_finished, generic.toString());
                }
            }
        });

        return eventDataId;
    }

    @NotNull
    @Override
    public CalculatorEventDataId fireCalculatorEvent(@NotNull final CalculatorEventType calculatorEventType, @Nullable final Object data) {
        final CalculatorEventDataId eventDataId = nextEventDataId();

        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                fireCalculatorEvent(CalculatorEventDataImpl.newInstance(eventDataId), calculatorEventType, data);
            }
        });

        return eventDataId;
    }

    @NotNull
    @Override
    public CalculatorEventDataId fireCalculatorEvent(@NotNull final CalculatorEventType calculatorEventType, @Nullable final Object data, @NotNull Long sequenceId) {
        final CalculatorEventDataId eventDataId = nextEventDataId(sequenceId);

        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                fireCalculatorEvent(CalculatorEventDataImpl.newInstance(eventDataId), calculatorEventType, data);
            }
        });

        return eventDataId;
    }

    @NotNull
    private CalculatorEventData newConversionEventData(@NotNull Long sequenceId) {
        return CalculatorEventDataImpl.newInstance(nextEventDataId(sequenceId));
    }

    private void evaluate(@NotNull Long sequenceId,
                          @NotNull JsclOperation operation,
                          @NotNull String expression,
                          @Nullable MessageRegistry mr) {
        synchronized (lock) {

            PreparedExpression preparedExpression = null;

            fireCalculatorEvent(newCalculationEventData(operation, expression, sequenceId), CalculatorEventType.calculation_started, new CalculatorInputImpl(expression, operation));

            try {
                preparedExpression = preprocessor.process(expression);

                final String jsclExpression = preparedExpression.toString();

                try {

                    final Generic result = operation.evaluateGeneric(jsclExpression);

                    // NOTE: toString() method must be called here as ArithmeticOperationException may occur in it (just to avoid later check!)
                    result.toString();

                    final CalculatorOutputImpl data = new CalculatorOutputImpl(operation.getFromProcessor().process(result), operation, result);
                    fireCalculatorEvent(newCalculationEventData(operation, expression, sequenceId), CalculatorEventType.calculation_result, data);

                } catch (AbstractJsclArithmeticException e) {
                    handleException(sequenceId, operation, expression, mr, new CalculatorEvalException(e, e, jsclExpression));
                }

            } catch (ArithmeticException e) {
                handleException(sequenceId, operation, expression, mr, preparedExpression, new CalculatorParseException(expression, new CalculatorMessage(CalculatorMessages.msg_001, MessageType.error, e.getMessage())));
            } catch (StackOverflowError e) {
                handleException(sequenceId, operation, expression, mr, preparedExpression, new CalculatorParseException(expression, new CalculatorMessage(CalculatorMessages.msg_002, MessageType.error)));
            } catch (jscl.text.ParseException e) {
                handleException(sequenceId, operation, expression, mr, preparedExpression, new CalculatorParseException(e));
            } catch (ParseInterruptedException e) {

                // do nothing - we ourselves interrupt the calculations
                fireCalculatorEvent(newCalculationEventData(operation, expression, sequenceId), CalculatorEventType.calculation_cancelled, null);

            } catch (CalculatorParseException e) {
                handleException(sequenceId, operation, expression, mr, preparedExpression, e);
            } finally {
                fireCalculatorEvent(newCalculationEventData(operation, expression, sequenceId), CalculatorEventType.calculation_finished, null);
            }
        }
    }

    @NotNull
    private CalculatorEventData newCalculationEventData(@NotNull JsclOperation operation,
                                                        @NotNull String expression,
                                                        @NotNull Long calculationId) {
        return new CalculatorEvaluationEventDataImpl(CalculatorEventDataImpl.newInstance(nextEventDataId(calculationId)), operation, expression);
    }

    private void handleException(@NotNull Long calculationId,
                                 @NotNull JsclOperation operation,
                                 @NotNull String expression,
                                 @Nullable MessageRegistry mr,
                                 @Nullable PreparedExpression preparedExpression,
                                 @NotNull CalculatorParseException parseException) {

        if (operation == JsclOperation.numeric
                && preparedExpression != null
                && preparedExpression.isExistsUndefinedVar()) {

            evaluate(calculationId, JsclOperation.simplify, expression, mr);

        }

        fireCalculatorEvent(newCalculationEventData(operation, expression, calculationId), CalculatorEventType.calculation_failed, new CalculatorFailureImpl(parseException));
    }

    private void handleException(@NotNull Long calculationId,
                                 @NotNull JsclOperation operation,
                                 @NotNull String expression,
                                 @Nullable MessageRegistry mr,
                                 @NotNull CalculatorEvalException evalException) {

        if (operation == JsclOperation.numeric && evalException.getCause() instanceof NumeralBaseException) {
            evaluate(calculationId, JsclOperation.simplify, expression, mr);
        }

        fireCalculatorEvent(newCalculationEventData(operation, expression, calculationId), CalculatorEventType.calculation_failed, new CalculatorFailureImpl(evalException));
    }

    /*
    **********************************************************************
    *
    *                           EVENTS
    *
    **********************************************************************
    */

    @Override
    public void addCalculatorEventListener(@NotNull CalculatorEventListener calculatorEventListener) {
        calculatorEventContainer.addCalculatorEventListener(calculatorEventListener);
    }

    @Override
    public void removeCalculatorEventListener(@NotNull CalculatorEventListener calculatorEventListener) {
        calculatorEventContainer.removeCalculatorEventListener(calculatorEventListener);
    }

    @Override
    public void fireCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable Object data) {
        calculatorEventContainer.fireCalculatorEvent(calculatorEventData, calculatorEventType, data);
    }

    @Override
    public void fireCalculatorEvents(@NotNull List<CalculatorEvent> calculatorEvents) {
        calculatorEventContainer.fireCalculatorEvents(calculatorEvents);
    }

    @Override
    public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable Object data) {
        if (calculatorEventType == CalculatorEventType.editor_state_changed) {
            final CalculatorEditorChangeEventData changeEventData = (CalculatorEditorChangeEventData) data;

            evaluate(JsclOperation.numeric, changeEventData.getNewState().getText(), calculatorEventData.getSequenceId());
        }
    }

    public static final class ConversionException extends Exception {
        private ConversionException() {
        }

        private ConversionException(Throwable throwable) {
            super(throwable);
        }
    }
}
