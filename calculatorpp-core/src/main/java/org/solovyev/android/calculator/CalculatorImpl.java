package org.solovyev.android.calculator;

import jscl.AbstractJsclArithmeticException;
import jscl.NumeralBaseException;
import jscl.math.Generic;
import jscl.text.ParseInterruptedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.calculator.text.TextProcessor;
import org.solovyev.common.msg.MessageRegistry;
import org.solovyev.common.msg.MessageType;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 16:42
 */
public class CalculatorImpl implements Calculator {

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
    }

    @NotNull
    private CalculatorEventDataId nextCalculatorEventDataId() {
        long eventId = counter.incrementAndGet();
        return CalculatorEventDataIdImpl.newInstance(eventId, eventId);
    }

    @NotNull
    private CalculatorEventDataId nextEventDataId(@NotNull Long calculationId) {
        long eventId = counter.incrementAndGet();
        return CalculatorEventDataIdImpl.newInstance(eventId, calculationId);
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

    @Override
    public void evaluate(@NotNull JsclOperation operation,
                         @NotNull String expression) {
        evaluate(operation, expression, null);
    }

    @Override
    @NotNull
    public CalculatorEventDataId evaluate(@NotNull final JsclOperation operation,
                                          @NotNull final String expression,
                                          @Nullable final MessageRegistry mr) {

        final CalculatorEventDataId eventDataId = nextCalculatorEventDataId();

        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                CalculatorImpl.this.evaluate(eventDataId.getCalculationId(), operation, expression, mr);
            }
        });

        return eventDataId;
    }

    private void evaluate(@NotNull Long calculationId,
                          @NotNull JsclOperation operation,
                          @NotNull String expression,
                          @Nullable MessageRegistry mr) {
        synchronized (lock) {

            PreparedExpression preparedExpression = null;

            fireCalculatorEvent(newCalculationEventData(operation, expression, calculationId), CalculatorEventType.calculation_started, new CalculatorInputImpl(expression, operation));

            try {
                preparedExpression = preprocessor.process(expression);

                final String jsclExpression = preparedExpression.toString();

                try {

                    final Generic result = operation.evaluateGeneric(jsclExpression);

                    // NOTE: toString() method must be called here as ArithmeticOperationException may occur in it (just to avoid later check!)
                    result.toString();

                    final CalculatorOutputImpl data = new CalculatorOutputImpl(operation.getFromProcessor().process(result), operation, result);
                    fireCalculatorEvent(newCalculationEventData(operation, expression, calculationId), CalculatorEventType.calculation_result, data);

                } catch (AbstractJsclArithmeticException e) {
                    handleException(calculationId, operation, expression, mr, new CalculatorEvalException(e, e, jsclExpression));
                }

            } catch (ArithmeticException e) {
                handleException(calculationId, operation, expression, mr, preparedExpression, new CalculatorParseException(expression, new CalculatorMessage(CalculatorMessages.msg_001, MessageType.error, e.getMessage())));
            } catch (StackOverflowError e) {
                handleException(calculationId, operation, expression, mr, preparedExpression, new CalculatorParseException(expression, new CalculatorMessage(CalculatorMessages.msg_002, MessageType.error)));
            } catch (jscl.text.ParseException e) {
                handleException(calculationId, operation, expression, mr, preparedExpression, new CalculatorParseException(e));
            } catch (ParseInterruptedException e) {

                // do nothing - we ourselves interrupt the calculations
                fireCalculatorEvent(newCalculationEventData(operation, expression, calculationId), CalculatorEventType.calculation_cancelled, null);

            } catch (CalculatorParseException e) {
                handleException(calculationId, operation, expression, mr, preparedExpression, e);
            } finally {
                fireCalculatorEvent(newCalculationEventData(operation, expression, calculationId), CalculatorEventType.calculation_finished, null);
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
}
