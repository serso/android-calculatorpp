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
    private CalculatorEventData lastCalculatorEventData = CalculatorEventDataImpl.newInstance(CalculatorLocatorImpl.getInstance().getCalculator().createFirstEventDataId());

    @Nullable
    private CalculatorDisplayView view;

    @NotNull
    private final Object viewLock = new Object();

    @NotNull
    private CalculatorDisplayViewState lastViewState = CalculatorDisplayViewStateImpl.newDefaultInstance();

    @Override
    public void setView(@Nullable CalculatorDisplayView view) {
        synchronized (viewLock) {
            this.view = view;

            if (view != null) {
                this.view.setState(lastViewState);
            }
        }
    }

    @NotNull
    @Override
    public CalculatorDisplayViewState getViewState() {
        return this.lastViewState;
    }

    @Override
    public void setViewState(@NotNull CalculatorDisplayViewState viewState) {
        synchronized (viewLock) {
            this.lastViewState = viewState;
            if (this.view != null) {
                this.view.setState(viewState);
            }
        }
    }

/*    @Override
    @Nullable
    public CharSequence getText() {
        synchronized (viewLock) {
            return view != null ? view.getText() : null;
        }
    }

    @Override
    public void setText(@Nullable CharSequence text) {
        synchronized (viewLock) {
            if (view != null) {
                view.setText(text);
            }
        }
    }

    @Override
    public int getSelection() {
        synchronized (viewLock) {
            return view != null ? view.getSelection() : 0;
        }
    }

    @Override
    public void setSelection(int selection) {
        synchronized (viewLock) {
            if (view != null) {
                view.setSelection(selection);
            }
        }
    }*/

    @Override
    @NotNull
    public CalculatorEventData getLastEventData() {
        return lastCalculatorEventData;
    }

    @Override
    public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData,
                                  @NotNull CalculatorEventType calculatorEventType,
                                  @Nullable Object data) {
        if (calculatorEventType.isOfType(calculation_result, calculation_failed, calculation_cancelled)) {

            if (calculatorEventData.isAfter(lastCalculatorEventData)) {
                lastCalculatorEventData = calculatorEventData;
            }

            switch (calculatorEventType) {
                case calculation_result:
                    processCalculationResult((CalculatorEvaluationEventData)calculatorEventData, (CalculatorOutput) data);
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

        this.setViewState(CalculatorDisplayViewStateImpl.newErrorState(calculatorEventData.getOperation(), errorMessage));
    }

    private void processCalculationCancelled(@NotNull CalculatorEvaluationEventData calculatorEventData) {
        final String errorMessage = CalculatorMessages.getBundle().getString(CalculatorMessages.syntax_error);

        this.setViewState(CalculatorDisplayViewStateImpl.newErrorState(calculatorEventData.getOperation(), errorMessage));
    }

    private void processCalculationResult(@NotNull CalculatorEvaluationEventData calculatorEventData, @NotNull CalculatorOutput data) {
        final String stringResult = data.getStringResult();
        this.setViewState(CalculatorDisplayViewStateImpl.newValidState(calculatorEventData.getOperation(), data.getResult(), stringResult, 0));
    }
}
