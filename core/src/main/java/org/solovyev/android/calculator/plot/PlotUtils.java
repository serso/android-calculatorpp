/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.plot;

import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.NumericWrapper;
import jscl.math.function.Constant;
import jscl.math.numeric.Complex;
import jscl.math.numeric.Numeric;
import jscl.math.numeric.Real;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 12/5/11
 * Time: 8:58 PM
 */
public final class PlotUtils {

	// not intended for instantiation
	private PlotUtils() {
		throw new AssertionError();
	}

	@Nonnull
	public static Complex calculatorExpression(@Nonnull Generic expression) {
		try {
			return unwrap(expression.numeric());
		} catch (RuntimeException e) {
			return NaN;
		}
	}

	@Nonnull
	public static Complex calculatorExpression(@Nonnull Generic expression, @Nonnull Constant xVar, double x) {
		try {
			return unwrap(expression.substitute(xVar, Expression.valueOf(x)).numeric());
		} catch (RuntimeException e) {
			return NaN;
		}
	}

	@Nonnull
	public static Complex calculatorExpression(@Nonnull Generic expression, @Nonnull Constant xVar, double x, @Nonnull Constant yVar, double y) {
		try {
			Generic tmp = expression.substitute(xVar, Expression.valueOf(x));
			tmp = tmp.substitute(yVar, Expression.valueOf(y));
			return unwrap(tmp.numeric());
		} catch (RuntimeException e) {
			return NaN;
		}
	}

	private static final Complex NaN = Complex.valueOf(Double.NaN, 0d);

	@Nonnull
	public static Complex unwrap(@Nullable Generic numeric) {
		if (numeric instanceof JsclInteger) {
			return Complex.valueOf(((JsclInteger) numeric).intValue(), 0d);
		} else if (numeric instanceof NumericWrapper) {
			return unwrap(((NumericWrapper) numeric).content());
		} else {
			return NaN;
		}
	}

	@Nonnull
	public static Complex unwrap(@Nullable Numeric content) {
		if (content instanceof Real) {
			return Complex.valueOf(((Real) content).doubleValue(), 0d);
		} else if (content instanceof Complex) {
			return ((Complex) content);
		} else {
			throw new ArithmeticException();
		}
	}
}
