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

import org.solovyev.android.calculator.jscl.JsclOperation;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 10:01 PM
 */
public class CalculatorEvaluationEventDataImpl implements CalculatorEvaluationEventData {

	@Nonnull
	private final CalculatorEventData calculatorEventData;

	@Nonnull
	private final JsclOperation operation;

	@Nonnull
	private final String expression;

	public CalculatorEvaluationEventDataImpl(@Nonnull CalculatorEventData calculatorEventData,
											 @Nonnull JsclOperation operation,
											 @Nonnull String expression) {
		this.calculatorEventData = calculatorEventData;
		this.operation = operation;
		this.expression = expression;
	}

	@Nonnull
	@Override
	public JsclOperation getOperation() {
		return this.operation;
	}

	@Nonnull
	@Override
	public String getExpression() {
		return this.expression;
	}

	@Override
	public long getEventId() {
		return calculatorEventData.getEventId();
	}

	@Nonnull
	@Override
	public Long getSequenceId() {
		return calculatorEventData.getSequenceId();
	}

	@Override
	public Object getSource() {
		return calculatorEventData.getSource();
	}

	@Override
	public boolean isAfter(@Nonnull CalculatorEventData that) {
		return calculatorEventData.isAfter(that);
	}

	@Override
	public boolean isSameSequence(@Nonnull CalculatorEventData that) {
		return this.calculatorEventData.isSameSequence(that);
	}

	@Override
	public boolean isAfterSequence(@Nonnull CalculatorEventData that) {
		return this.calculatorEventData.isAfterSequence(that);
	}
}
