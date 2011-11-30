package org.solovyev.android.calculator;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.NumericWrapper;
import jscl.math.function.Constant;
import jscl.math.numeric.Complex;
import jscl.math.numeric.Numeric;
import jscl.math.numeric.Real;
import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.help.HelpActivity;
import org.solovyev.common.utils.StringUtils;

/**
 * User: serso
 * Date: 11/2/11
 * Time: 2:18 PM
 */
public class CalculatorActivityLauncher {

	public static void showHistory(@NotNull final Context context) {
		context.startActivity(new Intent(context, CalculatorHistoryActivity.class));
	}

	public static void showHelp(@NotNull final Context context) {
		context.startActivity(new Intent(context, HelpActivity.class));
	}

	public static void showSettings(@NotNull final Context context) {
		context.startActivity(new Intent(context, CalculatorPreferencesActivity.class));
	}

	public static void showAbout(@NotNull final Context context) {
		context.startActivity(new Intent(context, AboutActivity.class));
	}

	public static void showFunctions(@NotNull final Context context) {
		context.startActivity(new Intent(context, CalculatorFunctionsActivity.class));
	}

	public static void showOperators(@NotNull final Context context) {
		context.startActivity(new Intent(context, CalculatorOperatorsActivity.class));
	}

	public static void showVars(@NotNull final Context context) {
		context.startActivity(new Intent(context, CalculatorVarsActivity.class));
	}

	public static void plotGraph(@NotNull final Context context, @NotNull Generic generic, @NotNull Constant constant) throws ArithmeticException {

		final XYSeries series = new XYSeries(generic.toString());

		final double min = -10;
		final double max = 10;
		final double step = 0.5;
		double x = min;
		while (x <= max) {
			Generic numeric = generic.substitute(constant, Expression.valueOf(x)).numeric();
			series.add(x, unwrap(numeric));
			x += step;
		}
		final XYMultipleSeriesDataset data = new XYMultipleSeriesDataset();
		data.addSeries(series);
		final XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.addSeriesRenderer(new XYSeriesRenderer());
		final Intent intent = ChartFactory.getLineChartIntent(context, data, renderer);
		intent.setClass(context, CalculatorPlotActivity.class);
		context.startActivity(intent);
	}

	private static double unwrap(Generic numeric) {
		if ( numeric instanceof JsclInteger) {
			return ((JsclInteger) numeric).intValue();
		} else if ( numeric instanceof NumericWrapper ) {
			return unwrap(((NumericWrapper) numeric).content());
		} else {
			throw  new ArithmeticException();
		}
	}

	private static double unwrap(Numeric content) {
		if (content instanceof Real) {
			return ((Real) content).doubleValue();
		} else if ( content instanceof Complex) {
			return ((Complex) content).realPart();
		} else {
			throw  new ArithmeticException();
		}
	}

	public static void createVar(@NotNull final Context context, @NotNull CalculatorModel calculatorModel) {
		if (calculatorModel.getDisplay().isValid() ) {
			final String varValue = calculatorModel.getDisplay().getText().toString();
			if (!StringUtils.isEmpty(varValue)) {
				if (CalculatorVarsActivity.isValidValue(varValue)) {
					final Intent intent = new Intent(context, CalculatorVarsActivity.class);
					intent.putExtra(CalculatorVarsActivity.CREATE_VAR_EXTRA_STRING, varValue);
					context.startActivity(intent);
				} else {
					Toast.makeText(context, R.string.c_not_valid_result, Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(context, R.string.c_empty_var_error, Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(context, R.string.c_not_valid_result, Toast.LENGTH_SHORT).show();
		}
	}
}
