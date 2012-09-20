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

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 16:42
 */
public class CalculatorImpl implements Calculator {

    @NotNull
    private final CalculatorEventContainer calculatorEventContainer = new ListCalculatorEventContainer();

    @NotNull
    private static final Calculator instance = new CalculatorImpl();

    @NotNull
    private final AtomicLong counter = new AtomicLong(0);

    @NotNull
    private final Object lock = new Object();

    @NotNull
    private final TextProcessor<PreparedExpression, String> preprocessor = ToJsclTextProcessor.getInstance();

    @NotNull
    private final Executor threadPoolExecutor = Executors.newFixedThreadPool(10);

    private CalculatorImpl() {
    }

    @NotNull
    public static Calculator getInstance() {
        return instance;
    }

    @NotNull
    private CalculatorEventDataId nextCalculatorEventDataId() {
        long eventId = counter.incrementAndGet();
        return CalculatorEventDataIdImpl.newInstance(eventId, eventId);
    }

    /*
    **********************************************************************
    *
    *                           CALCULATION
    *
    **********************************************************************
    */

    public void evaluate(@NotNull JsclOperation operation,
                         @NotNull String expression) {
        evaluate(operation, expression, null);
    }

    public void evaluate(@NotNull final JsclOperation operation,
                         @NotNull final String expression,
                         @Nullable final MessageRegistry mr) {

        final CalculatorEventDataId eventDataId = nextCalculatorEventDataId();

        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                CalculatorImpl.this.evaluate(eventDataId, operation, expression, mr);
            }
        });
    }

    private void evaluate(@NotNull CalculatorEventDataId eventDataId,
                          @NotNull JsclOperation operation,
                          @NotNull String expression,
                          @Nullable MessageRegistry mr) {
        synchronized (lock) {
            PreparedExpression preparedExpression = null;

            try {
                preparedExpression = preprocessor.process(expression);

                final String jsclExpression = preparedExpression.toString();
                try {

                    final Generic genericResult = operation.evaluateGeneric(jsclExpression);

                    // NOTE: toString() method must be called here as ArithmeticOperationException may occur in it (just to avoid later check!)
                    genericResult.toString();

                    //return new Result(operation.getFromProcessor().process(genericResult), operation, genericResult);
                } catch (AbstractJsclArithmeticException e) {
                    handleException(eventDataId, operation, expression, mr, preparedExpression, null, new CalculatorEvalException(e, e, jsclExpression));
                }

            } catch (ArithmeticException e) {
                //final AndroidMessage androidMessage = new AndroidMessage(R.string.msg_1, MessageType.error, CalculatorApplication.getInstance(), e.getMessage());
                handleException(operation, expression, mr, preparedExpression, new CalculatorParseException(jsclExpression, androidMessage));
            } catch (StackOverflowError e) {
                //final AndroidMessage androidMessage = new AndroidMessage(R.string.msg_2, MessageType.error, CalculatorApplication.getInstance());
                handleException(eventDataId, operation, expression, mr, preparedExpression, new CalculatorParseException(e), null);
            } catch (jscl.text.ParseException e) {
                //System.out.println(e.getMessage());
                handleException(eventDataId, operation, expression, mr, preparedExpression, new CalculatorParseException(e), null);
            } catch (ParseInterruptedException e) {
                // do nothing - we ourselves interrupt the calculations
            } catch (CalculatorParseException e) {
                handleException(eventDataId, operation, expression, mr, preparedExpression, e, null);
            }
        }
    }

    private void handleException(@NotNull CalculatorEventDataId eventDataId,
                                 @NotNull JsclOperation operation,
                                 @NotNull String expression,
                                 @Nullable MessageRegistry mr,
                                 @Nullable PreparedExpression preparedExpression,
                                 @Nullable CalculatorParseException parseException,
                                 @Nullable CalculatorEvalException evalException) {
        if (operation == JsclOperation.numeric && (preparedExpression != null && preparedExpression.isExistsUndefinedVar() || (evalException != null && evalException.getCause() instanceof NumeralBaseException))) {
            evaluate(eventDataId, JsclOperation.simplify, expression, mr);
        }

        if (parseException != null) {
            throw parseException;
        } else {
            throw evalException;
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
