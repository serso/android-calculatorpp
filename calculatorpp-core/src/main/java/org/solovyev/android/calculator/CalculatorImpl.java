package org.solovyev.android.calculator;

import jscl.AbstractJsclArithmeticException;
import jscl.NumeralBase;
import jscl.NumeralBaseException;
import jscl.math.Generic;
import jscl.math.function.Function;
import jscl.math.function.IConstant;
import jscl.math.operator.Operator;
import jscl.text.ParseInterruptedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.history.CalculatorHistory;
import org.solovyev.android.calculator.history.CalculatorHistoryState;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.calculator.model.Var;
import org.solovyev.android.calculator.text.TextProcessor;
import org.solovyev.android.calculator.units.CalculatorNumeralBase;
import org.solovyev.common.history.HistoryAction;
import org.solovyev.common.msg.MessageRegistry;
import org.solovyev.common.msg.MessageType;
import org.solovyev.common.text.StringUtils;
import org.solovyev.math.units.ConversionException;
import org.solovyev.math.units.ConversionUtils;

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
    private final Executor calculationsExecutor = Executors.newFixedThreadPool(10);

    // NOTE: only one thread is responsible for events as all events must be done in order of their creating
    @NotNull
    private final Executor eventExecutor = Executors.newFixedThreadPool(1);

    private volatile boolean calculateOnFly = true;

    public CalculatorImpl() {
        this.addCalculatorEventListener(this);
    }

    @NotNull
    private CalculatorEventData nextEventData() {
        long eventId = counter.incrementAndGet();
        return CalculatorEventDataImpl.newInstance(eventId, eventId);
    }

    @NotNull
    private CalculatorEventData nextEventData(@NotNull Object source) {
        long eventId = counter.incrementAndGet();
        return CalculatorEventDataImpl.newInstance(eventId, eventId, source);
    }

    @NotNull
    private CalculatorEventData nextEventData(@NotNull Long sequenceId) {
        long eventId = counter.incrementAndGet();
        return CalculatorEventDataImpl.newInstance(eventId, sequenceId);
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
        final CalculatorEventData eventData = fireCalculatorEvent(CalculatorEventType.manual_calculation_requested, viewState);
        this.evaluate(JsclOperation.numeric, viewState.getText(), eventData.getSequenceId());
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
        final CalculatorEventData eventData = fireCalculatorEvent(CalculatorEventType.manual_calculation_requested, viewState);
        this.evaluate(JsclOperation.simplify, viewState.getText(), eventData.getSequenceId());
    }

    @NotNull
    @Override
    public CalculatorEventData evaluate(@NotNull final JsclOperation operation,
                                          @NotNull final String expression) {

        final CalculatorEventData eventDataId = nextEventData();

        calculationsExecutor.execute(new Runnable() {
            @Override
            public void run() {
                CalculatorImpl.this.evaluate(eventDataId.getSequenceId(), operation, expression, null);
            }
        });

        return eventDataId;
    }

    @NotNull
    @Override
    public CalculatorEventData evaluate(@NotNull final JsclOperation operation, @NotNull final String expression, @NotNull Long sequenceId) {
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
    public void init() {
        CalculatorLocatorImpl.getInstance().getEngine().init();
        CalculatorLocatorImpl.getInstance().getHistory().load();
    }

    public void setCalculateOnFly(boolean calculateOnFly) {
        this.calculateOnFly = calculateOnFly;
    }

    @NotNull
    private CalculatorConversionEventData newConversionEventData(@NotNull Long sequenceId,
                                                                 @NotNull Generic value,
                                                                 @NotNull NumeralBase from,
                                                                 @NotNull NumeralBase to,
                                                                 @NotNull CalculatorDisplayViewState displayViewState) {
        return CalculatorConversionEventDataImpl.newInstance(nextEventData(sequenceId), value, from, to, displayViewState);
    }

    private void evaluate(@NotNull Long sequenceId,
                          @NotNull JsclOperation operation,
                          @NotNull String expression,
                          @Nullable MessageRegistry mr) {

        PreparedExpression preparedExpression = null;

        try {

            expression = expression.trim();

            if (StringUtils.isEmpty(expression)) {
                fireCalculatorEvent(newCalculationEventData(operation, expression, sequenceId), CalculatorEventType.calculation_result, CalculatorOutputImpl.newEmptyOutput(operation));
            } else {
				preparedExpression = prepareExpression(expression);

				final String jsclExpression = preparedExpression.toString();

                try {

                    final Generic result = operation.evaluateGeneric(jsclExpression, CalculatorLocatorImpl.getInstance().getEngine().getMathEngine());

                    // NOTE: toString() method must be called here as ArithmeticOperationException may occur in it (just to avoid later check!)
                    result.toString();

                    final CalculatorOutput data = CalculatorOutputImpl.newOutput(operation.getFromProcessor().process(result), operation, result);
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
        }
    }

	@NotNull
	@Override
	public PreparedExpression prepareExpression(@NotNull String expression) throws CalculatorParseException {
		return preprocessor.process(expression);
	}

	@NotNull
    private CalculatorEventData newCalculationEventData(@NotNull JsclOperation operation,
                                                        @NotNull String expression,
                                                        @NotNull Long calculationId) {
        return new CalculatorEvaluationEventDataImpl(nextEventData(calculationId), operation, expression);
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
        } else {
            fireCalculatorEvent(newCalculationEventData(operation, expression, calculationId), CalculatorEventType.calculation_failed, new CalculatorFailureImpl(evalException));
        }
    }

    /*
    **********************************************************************
    *
    *                           CONVERSION
    *
    **********************************************************************
    */

    @NotNull
    @Override
    public CalculatorEventData convert(@NotNull final Generic value,
                                         @NotNull final NumeralBase to) {
        final CalculatorEventData eventDataId = nextEventData();

        final CalculatorDisplayViewState displayViewState = CalculatorLocatorImpl.getInstance().getDisplay().getViewState();
        final NumeralBase from = CalculatorLocatorImpl.getInstance().getEngine().getNumeralBase();

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

    @NotNull
    private static String doConversion(@NotNull Generic generic,
                                       @NotNull NumeralBase from,
                                       @NotNull NumeralBase to) throws ConversionException {
        final String result;

        if (from != to) {
            String fromString = generic.toString();
            if (!StringUtils.isEmpty(fromString)) {
                try {
                    fromString = ToJsclTextProcessor.getInstance().process(fromString).getExpression();
                } catch (CalculatorParseException e) {
                    // ok, problems while processing occurred
                }
            }

            result = ConversionUtils.doConversion(CalculatorNumeralBase.getConverter(), fromString, CalculatorNumeralBase.valueOf(from), CalculatorNumeralBase.valueOf(to));
        } else {
            result = generic.toString();
        }

        return result;
    }

    @Override
    public boolean isConversionPossible(@NotNull Generic generic, NumeralBase from, @NotNull NumeralBase to) {
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
    public void addCalculatorEventListener(@NotNull CalculatorEventListener calculatorEventListener) {
        calculatorEventContainer.addCalculatorEventListener(calculatorEventListener);
    }

    @Override
    public void removeCalculatorEventListener(@NotNull CalculatorEventListener calculatorEventListener) {
        calculatorEventContainer.removeCalculatorEventListener(calculatorEventListener);
    }

    @Override
    public void fireCalculatorEvent(@NotNull final CalculatorEventData calculatorEventData, @NotNull final CalculatorEventType calculatorEventType, @Nullable final Object data) {
        eventExecutor.execute(new Runnable() {
            @Override
            public void run() {
                calculatorEventContainer.fireCalculatorEvent(calculatorEventData, calculatorEventType, data);
            }
        });
    }

    @Override
    public void fireCalculatorEvents(@NotNull final List<CalculatorEvent> calculatorEvents) {
        eventExecutor.execute(new Runnable() {
            @Override
            public void run() {
                calculatorEventContainer.fireCalculatorEvents(calculatorEvents);
            }
        });
    }

    @NotNull
    @Override
    public CalculatorEventData fireCalculatorEvent(@NotNull final CalculatorEventType calculatorEventType, @Nullable final Object data) {
        final CalculatorEventData eventData = nextEventData();

        fireCalculatorEvent(eventData, calculatorEventType, data);

        return eventData;
    }

    @NotNull
    @Override
    public CalculatorEventData fireCalculatorEvent(@NotNull final CalculatorEventType calculatorEventType, @Nullable final Object data, @NotNull Object source) {
        final CalculatorEventData eventData = nextEventData(source);

        fireCalculatorEvent(eventData, calculatorEventType, data);

        return eventData;
    }

    @NotNull
    @Override
    public CalculatorEventData fireCalculatorEvent(@NotNull final CalculatorEventType calculatorEventType, @Nullable final Object data, @NotNull Long sequenceId) {
        final CalculatorEventData eventData = nextEventData(sequenceId);

        fireCalculatorEvent(eventData, calculatorEventType, data);

        return eventData;
    }

    /*
    **********************************************************************
    *
    *                           EVENTS HANDLER
    *
    **********************************************************************
    */

    @Override
    public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable Object data) {

        switch (calculatorEventType) {
            case editor_state_changed:
                if (calculateOnFly) {
                    final CalculatorEditorChangeEventData editorChangeEventData = (CalculatorEditorChangeEventData) data;

                    final String newText = editorChangeEventData.getNewValue().getText();
                    final String oldText = editorChangeEventData.getOldValue().getText();

                    if (!newText.equals(oldText)) {
                        evaluate(JsclOperation.numeric, editorChangeEventData.getNewValue().getText(), calculatorEventData.getSequenceId());
                    }
                }
                break;

            case display_state_changed:
                onDisplayStateChanged((CalculatorDisplayChangeEventData) data);
                break;

            case constant_changed:
                final IConstant newConstant = ((Change<IConstant>)data) .getNewValue();
                if (!newConstant.getName().equals(CalculatorVarsRegistry.ANS)) {
                    evaluate();
                }
                break;

            case constant_added:
            case constant_removed:
            case function_added:
            case function_changed:
            case function_removed:
                evaluate();
                break;

            case engine_preferences_changed:
                evaluate(calculatorEventData.getSequenceId());
                break;

            case use_constant:
                final IConstant constant = (IConstant)data;
                CalculatorLocatorImpl.getInstance().getKeyboard().buttonPressed(constant.getName());
                break;

            case use_operator:
                final Operator operator = (Operator)data;
                CalculatorLocatorImpl.getInstance().getKeyboard().buttonPressed(operator.getName());
                break;

            case use_function:
                final Function function = (Function)data;
                CalculatorLocatorImpl.getInstance().getKeyboard().buttonPressed(function.getName());
                break;

        }
    }

    private void onDisplayStateChanged(@NotNull CalculatorDisplayChangeEventData displayChangeEventData) {
        final CalculatorDisplayViewState newState = displayChangeEventData.getNewValue();
        if (newState.isValid()) {
            final String result = newState.getStringResult();
            if ( !StringUtils.isEmpty(result) ) {
                final CalculatorMathRegistry<IConstant> varsRegistry = CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry();
                final IConstant ansVar = varsRegistry.get(CalculatorVarsRegistry.ANS);

                final Var.Builder varBuilder;
                if (ansVar != null) {
                    varBuilder = new Var.Builder(ansVar);
                } else {
                    varBuilder = new Var.Builder();
                }

                varBuilder.setName(CalculatorVarsRegistry.ANS);
                varBuilder.setValue(result);
                varBuilder.setDescription(CalculatorMessages.getBundle().getString("ans_description"));

                CalculatorVarsRegistry.saveVariable(varsRegistry, varBuilder, ansVar, this, false);
            }
        }
    }

    /*
    **********************************************************************
    *
    *                           HISTORY
    *
    **********************************************************************
    */

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
    @Override
    public CalculatorHistoryState getCurrentHistoryState() {
        return CalculatorHistoryState.newInstance(getEditor(), getDisplay());
    }

    /*
    **********************************************************************
    *
    *                           OTHER
    *
    **********************************************************************
    */

    @NotNull
    private CalculatorEditor getEditor() {
        return CalculatorLocatorImpl.getInstance().getEditor();
    }

    @NotNull
    private CalculatorDisplay getDisplay() {
        return CalculatorLocatorImpl.getInstance().getDisplay();
    }
}
