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

import jscl.math.Generic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.solovyev.android.calculator.jscl.JsclOperation;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 7:28 PM
 */
public class CalculatorOutputImpl implements CalculatorOutput {

	@Nullable
	private Generic result;

	@Nonnull
	private String stringResult;

	@Nonnull
	private JsclOperation operation;

	private CalculatorOutputImpl(@Nonnull String stringResult,
								 @Nonnull JsclOperation operation,
								 @Nullable Generic result) {
		this.stringResult = stringResult;
		this.operation = operation;
		this.result = result;
	}

	@Nonnull
	public static CalculatorOutput newOutput(@Nonnull String stringResult,
											 @Nonnull JsclOperation operation,
											 @Nonnull Generic result) {
		return new CalculatorOutputImpl(stringResult, operation, result);
	}

	@Nonnull
	public static CalculatorOutput newEmptyOutput(@Nonnull JsclOperation operation) {
		return new CalculatorOutputImpl("", operation, null);
	}

	@Override
	@Nonnull
	public String getStringResult() {
		return stringResult;
	}

	@Override
	@Nonnull
	public JsclOperation getOperation() {
		return operation;
	}

	@Override
	@Nullable
	public Generic getResult() {
		return result;
	}
}
