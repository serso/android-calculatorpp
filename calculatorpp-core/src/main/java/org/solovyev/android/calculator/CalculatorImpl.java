package org.solovyev.android.calculator;

import jscl.AbstractJsclArithmeticException;
import jscl.NumeralBase;
import jscl.NumeralBaseException;
import jscl.math.Expression;
import jscl.math.Generic;
import jscl.text.ParseInterruptedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.history.CalculatorHistory;
import org.solovyev.android.calculator.history.CalculatorHistoryState;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.calculator.text.TextProcessor;
import org.solovyev.common.history.HistoryAction;
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

    @NotNull
    private final CalculatorEventContainer calculatorEventContainer = new ListCalculatorEventContainer();

    @NotNull
    private final AtomicLong counter = new AtomicLong(CalculatorUtils.FIRST_ID);

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

    @Override
    public void evaluate() {
        final CalculatorEditorViewState viewState = getEditor().getViewState();
        fireCalculatorEvent(CalculatorEventType.manual_calculation_requested, viewState);
        this.evaluate(JsclOperation.numeric, viewState.getText());
    }

    @Override
    public void evaluate(@NotNull Long sequenceId) {
        final CalculatorEditorViewState viewState = getEditor().getViewState();
        fireCalculatorEvent(CalculatorEventType.manual_calculation_requested, viewState, sequenceId);
        this.evaluate(JsclOperation.numeric, viewState.getText(), sequenceId);
    }

    @Override
    public void simplify() {
        final CalculatorEditorViewState viewState = getEditor().getViewState();
        fireCalculatorEvent(CalculatorEventType.manual_calculation_requested, viewState);
        this.evaluate(JsclOperation.simplify, viewState.getText());
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

                final NumeralBase from = CalculatorLocatorImpl.getInstance().getEngine().getNumeralBase();

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

    @Override
    public void init() {
        CalculatorLocatorImpl.getInstance().getEngine().init();
        CalculatorLocatorImpl.getInstance().getHistory().load();
    }

    @NotNull
    private CalculatorEventData newConversionEventData(@NotNull Long sequenceId) {
        return CalculatorEventDataImpl.newInstance(nextEventDataId(sequenceId));
    }

    private void evaluate(@NotNull Long sequenceId,
                          @NotNull JsclOperation operation,
                          @NotNull String expression,
                          @Nullable MessageRegistry mr) {

        PreparedExpression preparedExpression = null;

        fireCalculatorEvent(newCalculationEventData(operation, expression, sequenceId), CalculatorEventType.calculation_started, new CalculatorInputImpl(expression, operation));

        try {

            expression = expression.trim();

            if (StringUtils.isEmpty(expression)) {
                final CalculatorOutputImpl data = new CalculatorOutputImpl("", operation, Expression.valueOf(""));
                fireCalculatorEvent(newCalculationEventData(operation, expression, sequenceId), CalculatorEventType.calculation_result, data);
            } else {
                preparedExpression = preprocessor.process(expression);

                final String jsclExpression = preparedExpression.toString();

                try {

                    final Generic result = operation.evaluateGeneric(jsclExpression, CalculatorLocatorImpl.getInstance().getEngine().getMathEngine());

                    // NOTE: toString() method must be called here as ArithmeticOperationException may occur in it (just to avoid later check!)
                    result.toString();

                    final CalculatorOutputImpl data = new CalculatorOutputImpl(operation.getFromProcessor().process(result), operation, result);
                    fireCalculatorEvent(newCalculationEventData(operation, expression, sequenceId), CalculatorEventType.calculation_result, data);

                } catch (AbstractJsclArithmeticException e) {
                    handleException(sequenceId, operation, expression, mr, new CalculatorEvalException(e, e, jsclExpression));
                }
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

    @NotNull
    private CalculatorEventData newCalculationEventData(@NotNull JsclOperation operation,
                                                        @NotNull String expression,
                                                        @NotNull Long calculationId) {
        return new CalculatorEvaluationEventDataImpl(CalculatorEventDataImpl.newInstance(nextEventDataId(calculationId)), operation, expression);
    }

    private void handleException(@NotNull Long sequenceId,
                                 @NotNull JsclOperation operation,
                                 @NotNull String expression,
                                 @Nullable MessageRegistry mr,
                                 @Nullable PreparedExpression preparedExpression,
                                 @NotNull CalculatorParseException parseException) {

        if (operation == JsclOperation.numeric
                && preparedExpression != null
                && preparedExpression.isExistsUndefinedVar()) {

            evaluate(sequenceId, JsclOperation.simplify, expression, mr);
        } else {

            fireCalculatorEvent(newCalculationEventData(operation, expression, sequenceId), CalculatorEventType.calculation_failed, new CalculatorFailureImpl(parseException));
        }
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

        switch (calculatorEventType) {
            case editor_state_changed:
                final CalculatorEditorChangeEventData changeEventData = (CalculatorEditorChangeEventData) data;

                final String newText = changeEventData.getNewState().getText();
                final String oldText = changeEventData.getOldState().getText();

                if (!newText.equals(oldText)) {
                    evaluate(JsclOperation.numeric, changeEventData.getNewState().getText(), calculatorEventData.getSequenceId());
                }
                break;
            case engine_preferences_changed:
                evaluate(calculatorEventData.getSequenceId());
                break;
        }
    }

    @Override
    public void doHistoryAction(@NotNull HistoryAction historyAction) {
        final CalculatorHistory history = CalculatorLocatorImpl.getInstance().getHistory();
        if (history.isActionAvailable(historyAction)) {
            final CalculatorHistoryState newState = history.doAction(historyAction, getCurrentHistoryState());
            if (newState != null) {
                setCurrentHistoryState(newState);
            }
        }
    }

    @Override
    public void setCurrentHistoryState(@NotNull CalculatorHistoryState editorHistoryState) {
        editorHistoryState.setValuesFromHistory(getEditor(), getDisplay());
    }

    @NotNull
    private CalculatorEditor getEditor() {
        return CalculatorLocatorImpl.getInstance().getEditor();
    }

    @NotNull
    @Override
    public CalculatorHistoryState getCurrentHistoryState() {
        return CalculatorHistoryState.newInstance(getEditor(), getDisplay());
    }

    @NotNull
    private CalculatorDisplay getDisplay() {
        return CalculatorLocatorImpl.getInstance().getDisplay();
    }

    public static final class ConversionException extends Exception {
        private ConversionException() {
        }

        private ConversionException(Throwable throwable) {
            super(throwable);
        }
    }
}
