package org.solovyev.android.calculator.plot;

/**
 * User: serso
 * Date: 1/18/13
 * Time: 7:44 PM
 */
interface FunctionEvaluator {
	int getArity();

	double eval();

	double eval(double x);

	double eval(double x, double y);
}
