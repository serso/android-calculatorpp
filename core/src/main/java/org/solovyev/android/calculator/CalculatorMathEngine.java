package org.solovyev.android.calculator;

import jscl.math.Generic;
import jscl.text.ParseException;
import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 9/23/12
 * Time: 6:05 PM
 */
public interface CalculatorMathEngine {

	@Nonnull
	String evaluate(@Nonnull String expression) throws ParseException;

	@Nonnull
	String simplify(@Nonnull String expression) throws ParseException;

	@Nonnull
	String elementary(@Nonnull String expression) throws ParseException;

	@Nonnull
	Generic evaluateGeneric(@Nonnull String expression) throws ParseException;

	@Nonnull
	Generic simplifyGeneric(@Nonnull String expression) throws ParseException;

	@Nonnull
	Generic elementaryGeneric(@Nonnull String expression) throws ParseException;
}
