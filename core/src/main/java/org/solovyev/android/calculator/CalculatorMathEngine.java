package org.solovyev.android.calculator;

import jscl.math.Generic;
import jscl.text.ParseException;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 9/23/12
 * Time: 6:05 PM
 */
public interface CalculatorMathEngine {

	@NotNull
	String evaluate(@NotNull String expression) throws ParseException;

	@NotNull
	String simplify(@NotNull String expression) throws ParseException;

	@NotNull
	String elementary(@NotNull String expression) throws ParseException;

	@NotNull
	Generic evaluateGeneric(@NotNull String expression) throws ParseException;

	@NotNull
	Generic simplifyGeneric(@NotNull String expression) throws ParseException;

	@NotNull
	Generic elementaryGeneric(@NotNull String expression) throws ParseException;
}
