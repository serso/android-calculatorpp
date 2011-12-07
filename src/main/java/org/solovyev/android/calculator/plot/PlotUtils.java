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
import org.achartengine.util.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 12/5/11
 * Time: 8:58 PM
 */
public final class PlotUtils {

	private static final double MAX_Y_DIFF = Math.pow(10, 6);

	// not intended for instantiation
	private PlotUtils() {
		throw new AssertionError();
	}

	public static boolean addXY(double minValue,
								double maxValue,
								@NotNull Generic expression,
								@NotNull Constant variable,
								@NotNull MyXYSeries realSeries,
								@NotNull MyXYSeries imagSeries,
								boolean addExtra,
								int numberOfSteps) throws ArithmeticException {

		boolean imagExists = false;

		double min = Math.min(minValue, maxValue);
		double max = Math.max(minValue, maxValue);
		double dist = max - min;
		if (addExtra) {
			min = min - dist;
			max = max + dist;
		}

		final double step = Math.max( dist / numberOfSteps, 0.000000001);

		Double prevRealY = null;
		Double prevX = null;
		Double prevImagY = null;

		double x = min;
		while (x <= max) {

			boolean needToCalculateRealY = realSeries.needToAdd(step, x);

			if (needToCalculateRealY) {
				final Complex c = calculatorExpression(expression, variable, x);
				Double y = prepareY(c.realPart());
				if (y != null) {
					addSingularityPoint(realSeries, prevX, x, prevRealY, y);
					realSeries.add(x, y);
					prevRealY = y;
					prevX = x;
				}

				boolean needToCalculateImagY = imagSeries.needToAdd(step, x);
				if (needToCalculateImagY) {
					y = prepareY(c.imaginaryPart());
					if (y != null) {
						addSingularityPoint(imagSeries, prevX, x, prevImagY, y);
						imagSeries.add(x, y);
						prevImagY = y;
						prevX = x;
					}
					if (c.imaginaryPart() != 0d) {
						imagExists = true;
					}
				}
			} else {
				boolean needToCalculateImagY = imagSeries.needToAdd(step, x);
				if (needToCalculateImagY) {
					final Complex c = calculatorExpression(expression, variable, x);
					Double y = prepareY(c.imaginaryPart());
					if (y != null) {
						addSingularityPoint(imagSeries, prevX, x, prevImagY, y);
						imagSeries.add(x, y);
						prevImagY = y;
						prevX = x;
					}
					if (c.imaginaryPart() != 0d) {
						imagExists = true;
					}
				}
			}

			x += step;
		}

		return imagExists;
	}

	@NotNull
	public static Complex calculatorExpression(@NotNull Generic expression, @NotNull Constant variable, double x) {
		return unwrap(expression.substitute(variable, Expression.valueOf(x)).numeric());
	}

	public static void addSingularityPoint(@NotNull MyXYSeries series, @Nullable Double prevX, @NotNull Double x, @Nullable Double prevY, @NotNull Double y) {
		if (prevX  != null && prevY != null) {
			// y or prevY should be more than 1d because if they are too small false singularity may occur (e.g., 1/0.000000000000000001)
			if ( (Math.abs(y) >= 1d && Math.abs(prevY / y) > MAX_Y_DIFF) || (Math.abs(prevY) >= 1d && Math.abs(y / prevY) > MAX_Y_DIFF)) {
				//Log.d(CalculatorPlotActivity.class.getName(), "Singularity! Prev point: (" + prevX + ", " + prevY + "), current point: (" +x+ ", " + y +")" );
				//Log.d(CalculatorPlotActivity.class.getName(), String.valueOf(prevX + Math.abs(x - prevX) / 2) +  ", null");
				series.add(prevX + Math.abs(x - prevX) / 2, MathHelper.NULL_VALUE);
			}
		}
	}

	@Nullable
	public static Double prepareY(double y) {
		if (Double.isNaN(y)) {
			return null;
		} else {
			return y;
		}
	}

	@NotNull
	public static Complex unwrap(@Nullable Generic numeric) {
		if (numeric instanceof JsclInteger) {
			return Complex.valueOf(((JsclInteger) numeric).intValue(), 0d);
		} else if (numeric instanceof NumericWrapper) {
			return unwrap(((NumericWrapper) numeric).content());
		} else {
			throw new ArithmeticException();
		}
	}

	@NotNull
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
