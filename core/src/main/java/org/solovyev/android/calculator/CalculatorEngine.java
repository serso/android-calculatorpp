package org.solovyev.android.calculator;

import jscl.AngleUnit;
import jscl.MathEngine;
import jscl.NumeralBase;
import jscl.math.function.Function;
import jscl.math.function.IConstant;
import jscl.math.operator.Operator;
import javax.annotation.Nonnull;

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

	@Nonnull
	CalculatorMathRegistry<IConstant> getVarsRegistry();

	@Nonnull
	CalculatorMathRegistry<Function> getFunctionsRegistry();

	@Nonnull
	CalculatorMathRegistry<Operator> getOperatorsRegistry();

	@Nonnull
	CalculatorMathRegistry<Operator> getPostfixFunctionsRegistry();

	@Nonnull
	CalculatorMathEngine getMathEngine();

	@Deprecated
	@Nonnull
	MathEngine getMathEngine0();

	/*
	**********************************************************************
	*
	*                           PREFERENCES
	*
	**********************************************************************
	*/

	@Nonnull
	String getMultiplicationSign();

	void setUseGroupingSeparator(boolean useGroupingSeparator);

	void setGroupingSeparator(char groupingSeparator);

	void setPrecision(@Nonnull Integer precision);

	void setRoundResult(@Nonnull Boolean round);

	@Nonnull
	AngleUnit getAngleUnits();

	void setAngleUnits(@Nonnull AngleUnit angleUnits);

	@Nonnull
	NumeralBase getNumeralBase();

	void setNumeralBase(@Nonnull NumeralBase numeralBase);

	void setMultiplicationSign(@Nonnull String multiplicationSign);

	void setScienceNotation(@Nonnull Boolean scienceNotation);

	void setTimeout(@Nonnull Integer timeout);

	void setDecimalGroupSymbols(@Nonnull DecimalFormatSymbols decimalGroupSymbols);
}
