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
