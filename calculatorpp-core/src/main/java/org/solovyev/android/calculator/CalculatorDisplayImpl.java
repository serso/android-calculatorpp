package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.solovyev.android.calculator.CalculatorEventType.*;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 8:24 PM
 */
public class CalculatorDisplayImpl implements CalculatorDisplay {

    @NotNull
    private volatile CalculatorEventData lastCalculatorEventData;

    @NotNull
    private final Object lastCalculatorEventDataLock = new Object();

    @Nullable
    private CalculatorDisplayView view;

    @NotNull
    private final Object viewLock = new Object();

    @NotNull
    private CalculatorDisplayViewState viewState = CalculatorDisplayViewStateImpl.newDefaultInstance();

    @NotNull
    private final Calculator calculator;

    public CalculatorDisplayImpl(@NotNull Calculator calculator) {
        this.calculator = calculator;
        this.lastCalculatorEventData = CalculatorUtils.createFirstEventDataId();
        this.calculator.addCalculatorEventListener(this);
    }

    @Override
    public void setView(@Nullable CalculatorDisplayView view) {
        synchronized (viewLock) {
            this.view = view;

            if (view != null) {
                this.view.setState(viewState);
            }
        }
    }

    @Nullable
    @Override
    public CalculatorDisplayView getView() {
        return this.view;
    }

    @NotNull
    @Override
    public CalculatorDisplayViewState getViewState() {
        return this.viewState;
    }

    @Override
    public void setViewState(@NotNull CalculatorDisplayViewState newViewState) {
        synchronized (viewLock) {
            final CalculatorDisplayViewState oldViewState = setViewState0(newViewState);

            this.calculator.fireCalculatorEvent(display_state_changed, new CalculatorDisplayChangeEventDataImpl(oldViewState, newViewState));
        }
    }

    private void setViewStateForSequence(@NotNull CalculatorDisplayViewState newViewState, @NotNull Long sequenceId) {
        synchronized (viewLock) {
            final CalculatorDisplayViewState oldViewState = setViewState0(newViewState);

            this.calculator.fireCalculatorEvent(display_state_changed, new CalculatorDisplayChangeEventDataImpl(oldViewState, newViewState), sequenceId);
        }
    }

    // must be synchronized with viewLock
    @NotNull
    private CalculatorDisplayViewState setViewState0(@NotNull CalculatorDisplayViewState newViewState) {
        final CalculatorDisplayViewState oldViewState = this.viewState;

        this.viewState = newViewState;
        if (this.view != null) {
            this.view.setState(newViewState);
        }
        return oldViewState;
    }

    @Override
    @NotNull
    public CalculatorEventData getLastEventData() {
        synchronized (lastCalculatorEventDataLock) {
            return lastCalculatorEventData;
        }
    }

    @Override
    public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData,
                                  @NotNull CalculatorEventType calculatorEventType,
                                  @Nullable Object data) {
        if (calculatorEventType.isOfType(calculation_result, calculation_failed, calculation_cancelled, conversion_result, conversion_failed)) {

            boolean processEvent = false;
            boolean sameSequence = false;

            synchronized (lastCalculatorEventDataLock) {
                if (calculatorEventData.isAfter(lastCalculatorEventData)) {
                    sameSequence = calculatorEventData.isSameSequence(lastCalculatorEventData);
                    lastCalculatorEventData = calculatorEventData;
                    processEvent = true;
                }
            }

            if (processEvent) {
                switch (calculatorEventType) {
                    case conversion_failed:
                        processConversationFailed((CalculatorConversionEventData) calculatorEventData, (ConversionFailure) data);
                        break;
                    case conversion_result:
                        processConversationResult((CalculatorConversionEventData)calculatorEventData,  (String)data);
                        break;
                    case calculation_result:
                        processCalculationResult((CalculatorEvaluationEventData) calculatorEventData, (CalculatorOutput) data);
                        break;
                    case calculation_cancelled:
                        processCalculationCancelled((CalculatorEvaluationEventData)calculatorEventData);
                        break;
                    case calculation_failed:
                        processCalculationFailed((CalculatorEvaluationEventData)calculatorEventData, (CalculatorFailure) data);
                        break;
                }
            }
        }
    }

    private void processConversationFailed(@NotNull CalculatorConversionEventData calculatorEventData,
                                           @NotNull ConversionFailure data) {
        this.setViewStateForSequence(CalculatorDisplayViewStateImpl.newErrorState(calculatorEventData.getDisplayState().getOperation(), CalculatorMessages.getBundle().getString(CalculatorMessages.syntax_error)), calculatorEventData.getSequenceId());

    }

    private void processCalculationFailed(@NotNull CalculatorEvaluationEventData calculatorEventData, @NotNull CalculatorFailure data) {

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

        this.setViewStateForSequence(CalculatorDisplayViewStateImpl.newErrorState(calculatorEventData.getOperation(), errorMessage), calculatorEventData.getSequenceId());
    }

    private void processCalculationCancelled(@NotNull CalculatorEvaluationEventData calculatorEventData) {
        final String errorMessage = CalculatorMessages.getBundle().getString(CalculatorMessages.syntax_error);

        this.setViewStateForSequence(CalculatorDisplayViewStateImpl.newErrorState(calculatorEventData.getOperation(), errorMessage), calculatorEventData.getSequenceId());
    }

    private void processCalculationResult(@NotNull CalculatorEvaluationEventData calculatorEventData, @NotNull CalculatorOutput data) {
        final String stringResult = data.getStringResult();
        this.setViewStateForSequence(CalculatorDisplayViewStateImpl.newValidState(calculatorEventData.getOperation(), data.getResult(), stringResult, 0), calculatorEventData.getSequenceId());
    }

    private void processConversationResult(@NotNull CalculatorConversionEventData calculatorEventData, @NotNull String result) {
        // add prefix
        if (calculatorEventData.getFromNumeralBase() != calculatorEventData.getToNumeralBase()) {
            result = calculatorEventData.getToNumeralBase().getJsclPrefix() + result;
        }

        final CalculatorDisplayViewState displayState = calculatorEventData.getDisplayState();
        this.setViewStateForSequence(CalculatorDisplayViewStateImpl.newValidState(displayState.getOperation(), displayState.getResult(), result, 0), calculatorEventData.getSequenceId());
    }
}
