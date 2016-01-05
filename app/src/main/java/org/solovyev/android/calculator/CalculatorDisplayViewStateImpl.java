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

import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import jscl.math.Generic;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 9:50 PM
 */
public class CalculatorDisplayViewStateImpl implements CalculatorDisplayViewState {

	/*
    **********************************************************************
	*
	*                           FIELDS
	*
	**********************************************************************
	*/

    @Nonnull
    private JsclOperation operation = JsclOperation.numeric;

    @Nullable
    private transient Generic result;

    @Nullable
    private String stringResult = "";

    private boolean valid = true;

    @Nullable
    private String errorMessage;

    private int selection = 0;

	/*
	**********************************************************************
	*
	*                           CONSTRUCTORS
	*
	**********************************************************************
	*/

    private CalculatorDisplayViewStateImpl() {
    }

    @Nonnull
    public static CalculatorDisplayViewState newDefaultInstance() {
        return new CalculatorDisplayViewStateImpl();
    }

    @Nonnull
    public static CalculatorDisplayViewState newErrorState(@Nonnull JsclOperation operation,
                                                           @Nonnull String errorMessage) {
        final CalculatorDisplayViewStateImpl calculatorDisplayState = new CalculatorDisplayViewStateImpl();
        calculatorDisplayState.valid = false;
        calculatorDisplayState.errorMessage = errorMessage;
        calculatorDisplayState.operation = operation;
        return calculatorDisplayState;
    }

    @Nonnull
    public static CalculatorDisplayViewState newValidState(@Nonnull JsclOperation operation,
                                                           @Nullable Generic result,
                                                           @Nonnull String stringResult,
                                                           int selection) {
        final CalculatorDisplayViewStateImpl calculatorDisplayState = new CalculatorDisplayViewStateImpl();
        calculatorDisplayState.valid = true;
        calculatorDisplayState.result = result;
        calculatorDisplayState.stringResult = stringResult;
        calculatorDisplayState.operation = operation;
        calculatorDisplayState.selection = selection;

        return calculatorDisplayState;
    }

	/*
	**********************************************************************
	*
	*                           METHODS
	*
	**********************************************************************
	*/

    @Nonnull
    @Override
    public String getText() {
        return Strings.getNotEmpty(isValid() ? stringResult : errorMessage, "");
    }

    @Override
    public int getSelection() {
        return selection;
    }

    @Nullable
    @Override
    public Generic getResult() {
        return this.result;
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

    @Nullable
    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    @Nullable
    public String getStringResult() {
        return stringResult;
    }

    @Nonnull
    @Override
    public JsclOperation getOperation() {
        return this.operation;
    }
}
