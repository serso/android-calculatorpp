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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.solovyev.android.calculator.CalculatorEventType.*;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 8:24 PM
 */
public class CalculatorDisplayImpl implements CalculatorDisplay {

	@Nonnull
	private final CalculatorEventHolder lastEvent;

	@Nullable
	private CalculatorDisplayView view;

	@Nonnull
	private final Object viewLock = new Object();

	@Nonnull
	private CalculatorDisplayViewState viewState = CalculatorDisplayViewStateImpl.newDefaultInstance();

	@Nonnull
	private final Calculator calculator;

	public CalculatorDisplayImpl(@Nonnull Calculator calculator) {
		this.calculator = calculator;
		this.lastEvent = new CalculatorEventHolder(CalculatorUtils.createFirstEventDataId());
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

	@Nonnull
	@Override
	public CalculatorDisplayViewState getViewState() {
		return this.viewState;
	}

	@Override
	public void setViewState(@Nonnull CalculatorDisplayViewState newViewState) {
		synchronized (viewLock) {
			final CalculatorDisplayViewState oldViewState = setViewState0(newViewState);

			this.calculator.fireCalculatorEvent(display_state_changed, new CalculatorDisplayChangeEventDataImpl(oldViewState, newViewState));
		}
	}

	private void setViewStateForSequence(@Nonnull CalculatorDisplayViewState newViewState, @Nonnull Long sequenceId) {
		synchronized (viewLock) {
			final CalculatorDisplayViewState oldViewState = setViewState0(newViewState);

			this.calculator.fireCalculatorEvent(display_state_changed, new CalculatorDisplayChangeEventDataImpl(oldViewState, newViewState), sequenceId);
		}
	}

	// must be synchronized with viewLock
	@Nonnull
	private CalculatorDisplayViewState setViewState0(@Nonnull CalculatorDisplayViewState newViewState) {
		final CalculatorDisplayViewState oldViewState = this.viewState;

		this.viewState = newViewState;
		if (this.view != null) {
			this.view.setState(newViewState);
		}
		return oldViewState;
	}

	@Override
	@Nonnull
	public CalculatorEventData getLastEventData() {
		return lastEvent.getLastEventData();
	}

	@Override
	public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData,
								  @Nonnull CalculatorEventType calculatorEventType,
								  @Nullable Object data) {
		if (calculatorEventType.isOfType(calculation_result, calculation_failed, calculation_cancelled, conversion_result, conversion_failed)) {

			final CalculatorEventHolder.Result result = lastEvent.apply(calculatorEventData);

			if (result.isNewAfter()) {
				switch (calculatorEventType) {
					case conversion_failed:
						processConversationFailed((CalculatorConversionEventData) calculatorEventData, (ConversionFailure) data);
						break;
					case conversion_result:
						processConversationResult((CalculatorConversionEventData) calculatorEventData, (String) data);
						break;
					case calculation_result:
						processCalculationResult((CalculatorEvaluationEventData) calculatorEventData, (CalculatorOutput) data);
						break;
					case calculation_cancelled:
						processCalculationCancelled((CalculatorEvaluationEventData) calculatorEventData);
						break;
					case calculation_failed:
						processCalculationFailed((CalculatorEvaluationEventData) calculatorEventData, (CalculatorFailure) data);
						break;
				}
			}
		}
	}

	private void processConversationFailed(@Nonnull CalculatorConversionEventData calculatorEventData,
										   @Nonnull ConversionFailure data) {
		this.setViewStateForSequence(CalculatorDisplayViewStateImpl.newErrorState(calculatorEventData.getDisplayState().getOperation(), CalculatorMessages.getBundle().getString(CalculatorMessages.syntax_error)), calculatorEventData.getSequenceId());

	}

	private void processCalculationFailed(@Nonnull CalculatorEvaluationEventData calculatorEventData, @Nonnull CalculatorFailure data) {

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

	private void processCalculationCancelled(@Nonnull CalculatorEvaluationEventData calculatorEventData) {
		final String errorMessage = CalculatorMessages.getBundle().getString(CalculatorMessages.syntax_error);

		this.setViewStateForSequence(CalculatorDisplayViewStateImpl.newErrorState(calculatorEventData.getOperation(), errorMessage), calculatorEventData.getSequenceId());
	}

	private void processCalculationResult(@Nonnull CalculatorEvaluationEventData calculatorEventData, @Nonnull CalculatorOutput data) {
		final String stringResult = data.getStringResult();
		this.setViewStateForSequence(CalculatorDisplayViewStateImpl.newValidState(calculatorEventData.getOperation(), data.getResult(), stringResult, 0), calculatorEventData.getSequenceId());
	}

	private void processConversationResult(@Nonnull CalculatorConversionEventData calculatorEventData, @Nonnull String result) {
		// add prefix
		if (calculatorEventData.getFromNumeralBase() != calculatorEventData.getToNumeralBase()) {
			result = calculatorEventData.getToNumeralBase().getJsclPrefix() + result;
		}

		final CalculatorDisplayViewState displayState = calculatorEventData.getDisplayState();
		this.setViewStateForSequence(CalculatorDisplayViewStateImpl.newValidState(displayState.getOperation(), displayState.getResult(), result, 0), calculatorEventData.getSequenceId());
	}
}
