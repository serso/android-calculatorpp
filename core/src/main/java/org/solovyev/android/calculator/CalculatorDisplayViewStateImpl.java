package org.solovyev.android.calculator;

import jscl.math.Generic;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.common.text.Strings;

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
