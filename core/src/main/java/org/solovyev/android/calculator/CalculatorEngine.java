package org.solovyev.android.calculator;

import jscl.AngleUnit;
import jscl.MathEngine;
import jscl.NumeralBase;
import jscl.math.function.Function;
import jscl.math.function.IConstant;
import jscl.math.operator.Operator;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormatSymbols;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 12:43
 */
public interface CalculatorEngine {

	/*
	**********************************************************************
	*
	*                           INIT
	*
	**********************************************************************
	*/

	void init();

	void reset();

	void softReset();

	/*
	**********************************************************************
	*
	*                           REGISTRIES
	*
	**********************************************************************
	*/

	@NotNull
	CalculatorMathRegistry<IConstant> getVarsRegistry();

	@NotNull
	CalculatorMathRegistry<Function> getFunctionsRegistry();

	@NotNull
	CalculatorMathRegistry<Operator> getOperatorsRegistry();

	@NotNull
	CalculatorMathRegistry<Operator> getPostfixFunctionsRegistry();

	@NotNull
	CalculatorMathEngine getMathEngine();

	@Deprecated
	@NotNull
	MathEngine getMathEngine0();

	/*
	**********************************************************************
	*
	*                           PREFERENCES
	*
	**********************************************************************
	*/

	@NotNull
	String getMultiplicationSign();

	void setUseGroupingSeparator(boolean useGroupingSeparator);

	void setGroupingSeparator(char groupingSeparator);

	void setPrecision(@NotNull Integer precision);

	void setRoundResult(@NotNull Boolean round);

	@NotNull
	AngleUnit getAngleUnits();

	void setAngleUnits(@NotNull AngleUnit angleUnits);

	@NotNull
	NumeralBase getNumeralBase();

	void setNumeralBase(@NotNull NumeralBase numeralBase);

	void setMultiplicationSign(@NotNull String multiplicationSign);

	void setScienceNotation(@NotNull Boolean scienceNotation);

	void setTimeout(@NotNull Integer timeout);

	void setDecimalGroupSymbols(@NotNull DecimalFormatSymbols decimalGroupSymbols);
}
