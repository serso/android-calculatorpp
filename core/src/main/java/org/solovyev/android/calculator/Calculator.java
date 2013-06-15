package org.solovyev.android.calculator;

import jscl.NumeralBase;
import jscl.math.Generic;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.android.calculator.history.CalculatorHistoryState;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.common.history.HistoryControl;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 16:38
 */
public interface Calculator extends CalculatorEventContainer, HistoryControl<CalculatorHistoryState> {

	void init();

	/*
	**********************************************************************
	*
	*                           CALCULATIONS
	*
	**********************************************************************
	*/

	void evaluate();

	void evaluate(@Nonnull Long sequenceId);

	void simplify();

	@Nonnull
	CalculatorEventData evaluate(@Nonnull JsclOperation operation,
								 @Nonnull String expression);

	@Nonnull
	CalculatorEventData evaluate(@Nonnull JsclOperation operation,
								 @Nonnull String expression,
								 @Nonnull Long sequenceId);

	/*
	**********************************************************************
	*
	*                           CONVERSION
	*
	**********************************************************************
	*/

	boolean isConversionPossible(@Nonnull Generic generic, @Nonnull NumeralBase from, @Nonnull NumeralBase to);

	@Nonnull
	CalculatorEventData convert(@Nonnull Generic generic, @Nonnull NumeralBase to);

	/*
	**********************************************************************
	*
	*                           EVENTS
	*
	**********************************************************************
	*/
	@Nonnull
	CalculatorEventData fireCalculatorEvent(@Nonnull CalculatorEventType calculatorEventType, @Nullable Object data);

	@Nonnull
	CalculatorEventData fireCalculatorEvent(@Nonnull CalculatorEventType calculatorEventType, @Nullable Object data, @Nonnull Object source);

	@Nonnull
	CalculatorEventData fireCalculatorEvent(@Nonnull CalculatorEventType calculatorEventType, @Nullable Object data, @Nonnull Long sequenceId);

	@Nonnull
	PreparedExpression prepareExpression(@Nonnull String expression) throws CalculatorParseException;
}
