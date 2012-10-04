/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.plot;

import android.util.Log;
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

	private static final double MAX_Y_DIFF = 1;
	private static final double MAX_X_DIFF = 1;

	// not intended for instantiation
	private PlotUtils() {
		throw new AssertionError();
	}

	public static boolean addXY(double minValue,
								double maxValue,
								@NotNull Generic expression,
								@NotNull Constant variable,
								@NotNull MyXYSeries realSeries,
								@Nullable MyXYSeries imagSeries,
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

        final double eps = 0.000000001;

        final double defaultStep = Math.max(dist / numberOfSteps, eps);
        double step = defaultStep;

		final Point real = new Point();
        final Point imag = new Point();

		double x = min;

        while (x <= max) {

            boolean needToCalculateRealY = realSeries.needToAdd(defaultStep, x);

			if (needToCalculateRealY) {
				final Complex c = calculatorExpression(expression, variable, x);
				Double y = prepareY(c.realPart());

                if (y != null) {
                    real.moveToNextPoint(x, y);
                    addSingularityPoint(realSeries, real);
					realSeries.add(x, y);
				}

				boolean needToCalculateImagY = imagSeries != null && imagSeries.needToAdd(defaultStep, x);
				if (needToCalculateImagY) {
					y = prepareY(c.imaginaryPart());
					if (y != null) {
                        imag.moveToNextPoint(x, y);
                        addSingularityPoint(imagSeries, imag);
						imagSeries.add(x, y);
                    }
					if (c.imaginaryPart() != 0d) {
						imagExists = true;
					}
				}
			} else {
				boolean needToCalculateImagY = imagSeries != null && imagSeries.needToAdd(defaultStep, x);
				if (needToCalculateImagY) {
					final Complex c = calculatorExpression(expression, variable, x);
					Double y = prepareY(c.imaginaryPart());
					if (y != null) {
                        imag.moveToNextPoint(x, y);
                        addSingularityPoint(imagSeries, imag);
						imagSeries.add(x, y);
                    }
					if (c.imaginaryPart() != 0d) {
						imagExists = true;
					}
				}
			}

            step = updateStep(real, step, defaultStep / 2);

            if (real.isX2Defined()) {
                x = real.getX2() + step;
            } else {
                x += step;
            }
        }

		return imagExists;
	}

    private static class Point {
        private static final double DEFAULT = Double.MIN_VALUE;

        private double x0 = DEFAULT;
        private double x1 = DEFAULT;
        private double x2 = DEFAULT;

        private double y0 = DEFAULT;
        private double y1 = DEFAULT;
        private double y2 = DEFAULT;

        private Point() {
        }

        public void moveToNextPoint(double x, double y) {
            if ( this.x2 == x ) {
                return;
            }

            this.x0 = this.x1;
            this.x1 = this.x2;
            this.x2 = x;

            this.y0 = this.y1;
            this.y1 = this.y2;
            this.y2 = y;
        }

        public boolean isFullyDefined() {
            return x0 != DEFAULT && x1 != DEFAULT && x2 != DEFAULT && y0 != DEFAULT && y1 != DEFAULT && y2 != DEFAULT;
        }

        public double getDx2() {
            return x2 - x1;
        }

        public double getAbsDx2() {
            if ( x2 > x1 ) {
                return Math.abs(x2 - x1);
            } else {
                return Math.abs(x1 - x2);
            }
        }

        public double getAbsDx1() {
            if ( x1 > x0 ) {
                return Math.abs(x1 - x0);
            } else {
                return Math.abs(x0 - x1);
            }
        }

        public double getAbsDy1() {
            if ( y1 > y0 ) {
                return Math.abs(y1 - y0);
            } else {
                return Math.abs(y0 - y1);
            }
        }

        public double getAbsDy2() {
            if ( y2 > y1 ) {
                return Math.abs(y2 - y1);
            } else {
                return Math.abs(y1 - y2);
            }
        }

        public double getX0() {
            return x0;
        }

        public double getX1() {
            return x1;
        }

        public double getX2() {
            return x2;
        }

        public boolean isX2Defined() {
            return x2 != DEFAULT;
        }

        public double getY0() {
            return y0;
        }

        public double getY1() {
            return y1;
        }

        public double getY2() {
            return y2;
        }

        public void clearHistory () {
            this.x0 = DEFAULT;
            this.x1 = DEFAULT;
            this.y0 = DEFAULT;
            this.y1 = DEFAULT;
        }

        public double getAbsDyDx2() {
            double dx2 = this.getAbsDx2();
            double dy2 = this.getAbsDy2();
            return dy2 / dx2;
        }

        public double getAbsDyDx1() {
            double dx1 = this.getAbsDx1();
            double dy1 = this.getAbsDy1();
            return dy1 / dx1;
        }

        public double getDyDx1() {
            double result = getAbsDyDx1();
            return y1 > y0 ? result : -result;
        }

        public double getDyDx2() {
            double result = getAbsDyDx2();
            return y2 > y1 ? result : -result;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x0=" + x0 +
                    ", x1=" + x1 +
                    ", x2=" + x2 +
                    ", y0=" + y0 +
                    ", y1=" + y1 +
                    ", y2=" + y2 +
                    '}';
        }
    }

    private static double updateStep(@NotNull Point real,
                                     double step,
                                     double eps) {
        if ( !real.isFullyDefined() ) {
            return step;
        } else {
            double dydx2 = real.getAbsDyDx2();
            double dydx1 = real.getAbsDyDx1();

            double k = dydx2 / dydx1;

            if ( k > 1 ) {
                step = step / k;
            } else if ( k > 0 ) {
                step = step * k;
            }

            return Math.max(step, eps);
        }
    }

    @NotNull
	public static Complex calculatorExpression(@NotNull Generic expression, @NotNull Constant variable, double x) {
		return unwrap(expression.substitute(variable, Expression.valueOf(x)).numeric());
	}

	public static void addSingularityPoint(@NotNull MyXYSeries series,
                                           @NotNull Point point) {
		if (point.isFullyDefined()) {
			// y or prevY should be more than 1d because if they are too small false singularity may occur (e.g., 1/0.000000000000000001)
           // double dy0 = y1 - y0;
           // double dx0 = x1 - x0;
           // double dydx0 = dy0 / dx0;

            double dy2 = point.getAbsDy2();
            double dx2 = point.getAbsDx2();
            //double dx1 = x2 - x1;
           // double dydx1 = dy2 / dx1;

            if ( dy2 > MAX_Y_DIFF && dx2 < MAX_X_DIFF && isDifferentSign(point.getY2(), point.getY1()) && isDifferentSign(point.getDyDx1(), point.getDyDx2())) {
				Log.d(CalculatorPlotActivity.class.getName(), "Singularity: " + point);
				//Log.d(CalculatorPlotActivity.class.getName(), String.valueOf(prevX + Math.abs(x - prevX) / 2) +  ", null");
				series.add(point.getX1() + point.getAbsDx2() / 2, MathHelper.NULL_VALUE);
                point.clearHistory();
			}
		}
	}

    private static boolean isDifferentSign(@NotNull Double y0, @NotNull Double y1) {
        return (y0 >= 0 && y1 < 0) || (y1 >= 0 && y0 < 0);
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
