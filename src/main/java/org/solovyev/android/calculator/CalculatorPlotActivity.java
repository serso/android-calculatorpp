/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;
import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.math.NumericWrapper;
import jscl.math.function.Constant;
import jscl.math.numeric.Complex;
import jscl.math.numeric.Numeric;
import jscl.math.numeric.Real;
import jscl.text.ParseException;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.AbstractChart;
import org.achartengine.chart.LineChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.view.widgets.NumberPicker;
import org.solovyev.common.utils.MutableObject;

import java.io.Serializable;

/**
 * User: serso
 * Date: 12/1/11
 * Time: 12:40 AM
 */
public class CalculatorPlotActivity extends Activity {

	private static final int DEFAULT_NUMBER_OF_STEPS = 100;

	private static final int DEFAULT_MIN_NUMBER = -10;

	private static final int DEFAULT_MAX_NUMBER = 10;

	public static final String INPUT = "org.solovyev.android.calculator.CalculatorPlotActivity_input";

	public static final long EVAL_DELAY_MILLIS = 1000;

	/**
	 * The encapsulated graphical view.
	 */
	private GraphicalView graphicalView;

	@NotNull
	private Generic expression;

	@NotNull
	private Constant variable;

	private double minValue = DEFAULT_MIN_NUMBER;

	private double maxValue = DEFAULT_MAX_NUMBER;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();

		final Input input = (Input) extras.getSerializable(INPUT);

		try {
			this.expression = Expression.valueOf(input.getExpression());
			this.variable = new Constant(input.getVariableName());

			String title = extras.getString(ChartFactory.TITLE);
			if (title == null) {
				requestWindowFeature(Window.FEATURE_NO_TITLE);
			} else if (title.length() > 0) {
				setTitle(title);
			}

			setContentView(R.layout.calc_plot_view);

			setGraphicalView(minValue, maxValue);

			final NumberPicker minXNumberPicker = (NumberPicker)findViewById(R.id.plot_x_min_value);
			final NumberPicker maxXNumberPicker = (NumberPicker)findViewById(R.id.plot_x_max_value);

			minXNumberPicker.setRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
			minXNumberPicker.setCurrent(DEFAULT_MIN_NUMBER);
			maxXNumberPicker.setRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
			maxXNumberPicker.setCurrent(DEFAULT_MAX_NUMBER);


			minXNumberPicker.setOnChangeListener(new BoundariesChangeListener(true));
			maxXNumberPicker.setOnChangeListener(new BoundariesChangeListener(false));

		} catch (ParseException e) {
			Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			finish();
		}
	}

	private void setGraphicalView(final double minValue, final double maxValue) {
		final ViewGroup graphContainer = (ViewGroup) findViewById(R.id.plot_graph_container);

		if (graphicalView != null) {
			graphContainer.removeView(graphicalView);
		}

		graphicalView = new GraphicalView(this, prepareChart(minValue, maxValue, expression, variable));
		graphContainer.addView(graphicalView);
	}

	@NotNull
	private final static MutableObject<Runnable> pendingOperation = new MutableObject<Runnable>();

	private class BoundariesChangeListener implements NumberPicker.OnChangedListener {

		private boolean min;

		private BoundariesChangeListener(boolean min) {
			this.min = min;
		}


		@Override
		public void onChanged(NumberPicker picker, int oldVal, final int newVal) {
			if (min) {
				minValue = newVal;
			} else {
				maxValue = newVal;
			}

			pendingOperation.setObject(new Runnable() {
				@Override
				public void run() {
					// allow only one runner at one time
					synchronized (pendingOperation) {
						//lock all operations with history
						if (pendingOperation.getObject() == this) {
							// actually nothing shall be logged while text operations are done
							setGraphicalView(CalculatorPlotActivity.this.minValue, CalculatorPlotActivity.this.maxValue);
						}
					}
				}
			});

			new Handler().postDelayed(pendingOperation.getObject(), EVAL_DELAY_MILLIS);
		}
	}

	private static AbstractChart prepareChart(final double minValue, final double maxValue, @NotNull final Generic expression, @NotNull final Constant variable) {
		final XYSeries realSeries = new XYSeries(expression.toString());
		final XYSeries imagSeries = new XYSeries("Im(" + expression.toString() + ")");

		boolean imagExists = false;

		final double min = Math.min(minValue, maxValue);
		final double max = Math.max(minValue, maxValue);
		final int numberOfSteps = DEFAULT_NUMBER_OF_STEPS;
		final double step = Math.max((max - min) / numberOfSteps, 0.001);
		double x = min;
		while (x <= max) {
			Generic numeric = expression.substitute(variable, Expression.valueOf(x)).numeric();
			final Complex c = unwrap(numeric);
			realSeries.add(x, prepareY(c.realPart()));
			imagSeries.add(x, prepareY(c.imaginaryPart()));
			if (c.imaginaryPart() != 0d) {
				imagExists = true;
			}
			x += step;
		}

		final XYMultipleSeriesDataset data = new XYMultipleSeriesDataset();
		data.addSeries(realSeries);
		if (imagExists) {
			data.addSeries(imagSeries);
		}

		final XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setZoomEnabled(false);
		renderer.setZoomEnabled(false, false);
		renderer.addSeriesRenderer(createCommonRenderer());
		renderer.setPanEnabled(false);
		renderer.setPanEnabled(false, false);
		if (imagExists) {
			final XYSeriesRenderer imagRenderer = createCommonRenderer();
			imagRenderer.setStroke(BasicStroke.DOTTED);
			renderer.addSeriesRenderer(imagRenderer);
		}

		return new LineChart(data, renderer);
	}

	@NotNull
	private static XYSeriesRenderer createCommonRenderer() {
		final XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setColor(Color.WHITE);
		renderer.setStroke(BasicStroke.SOLID);
		return renderer;
	}

	private static double prepareY(double y) {
		if (Double.isNaN(y) || Double.isInfinite(y)) {
			return 0d;
		} else {
			return y;
		}
	}

	@NotNull
	private static Complex unwrap(@Nullable Generic numeric) {
		if (numeric instanceof JsclInteger) {
			return Complex.valueOf(((JsclInteger) numeric).intValue(), 0d);
		} else if (numeric instanceof NumericWrapper) {
			return unwrap(((NumericWrapper) numeric).content());
		} else {
			throw new ArithmeticException();
		}
	}

	@NotNull
	private static Complex unwrap(@Nullable Numeric content) {
		if (content instanceof Real) {
			return Complex.valueOf(((Real) content).doubleValue(), 0d);
		} else if (content instanceof Complex) {
			return ((Complex) content);
		} else {
			throw new ArithmeticException();
		}
	}


	public static class Input implements Serializable {

		@NotNull
		private String expression;

		@NotNull
		private String variableName;

		public Input(@NotNull String expression, @NotNull String variableName) {
			this.expression = expression;
			this.variableName = variableName;
		}

		@NotNull
		public String getExpression() {
			return expression;
		}

		@NotNull
		public String getVariableName() {
			return variableName;
		}
	}
}
