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
import android.util.Log;
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
import jscl.text.MutableInt;
import jscl.text.ParseException;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.XYChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.PanListener;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;
import org.achartengine.util.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.utils.MutableObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: serso
 * Date: 12/1/11
 * Time: 12:40 AM
 */
public class CalculatorPlotActivity extends Activity {

	private static final int DEFAULT_NUMBER_OF_STEPS = 200;

	private static final int DEFAULT_MIN_NUMBER = -10;

	private static final int DEFAULT_MAX_NUMBER = 10;

	public static final String INPUT = "org.solovyev.android.calculator.CalculatorPlotActivity_input";

	public static final long EVAL_DELAY_MILLIS = 400;

	private XYChart chart;

	/**
	 * The encapsulated graphical view.
	 */
	private GraphicalView graphicalView;

	@NotNull
	private Generic expression;

	@NotNull
	private Constant variable;

	private static final double MAX_Y_DIFF = Math.pow(10, 6);

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

			final Object lastNonConfigurationInstance = getLastNonConfigurationInstance();
			setGraphicalView(lastNonConfigurationInstance instanceof PlotBoundaries ? (PlotBoundaries)lastNonConfigurationInstance : null);

		} catch (ParseException e) {
			Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			finish();
		} catch (ArithmeticException e) {
			Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			finish();
		}
	}

	private void setGraphicalView(@Nullable PlotBoundaries plotBoundaries) {
		double minValue = plotBoundaries == null ? DEFAULT_MIN_NUMBER : plotBoundaries.xMin;
		double maxValue = plotBoundaries == null ? DEFAULT_MAX_NUMBER : plotBoundaries.xMax;

		final ViewGroup graphContainer = (ViewGroup) findViewById(R.id.plot_view_container);

		if (graphicalView != null) {
			graphContainer.removeView(graphicalView);
		}

		chart = prepareChart(minValue, maxValue, expression, variable);

		// reverting boundaries (as in prepareChart() we add some cached values )
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;

		double maxX = Double.MIN_VALUE;
		double maxY = Double.MIN_VALUE;

		for (XYSeries series : chart.getDataset().getSeries()) {
			if (checkMinMaxValue(series.getMinX())) {
				minX = Math.min(minX, series.getMinX());
			}

			if (checkMinMaxValue(series.getMinY())) {
				minY = Math.min(minY, series.getMinY());
			}

			if (checkMinMaxValue(series.getMaxX())) {
				maxX = Math.max(maxX, series.getMaxX());
			}

			if (checkMinMaxValue(series.getMaxY())) {
				maxY = Math.max(maxY, series.getMaxY());
			}
		}

		Log.d(CalculatorPlotActivity.class.getName(), "min x: " + minX + ", min y: " + minY + ", max x: " + maxX + ", max y: " + maxY);
		Log.d(CalculatorPlotActivity.class.getName(), "Plot boundaries are " + plotBoundaries);


		if (plotBoundaries == null) {
			chart.getRenderer().setXAxisMin(Math.max(minX, minValue));
			chart.getRenderer().setYAxisMin(Math.max(minY, minValue));
			chart.getRenderer().setXAxisMax(Math.min(maxX, maxValue));
			chart.getRenderer().setYAxisMax(Math.min(maxY, maxValue));
		} else {
			chart.getRenderer().setXAxisMin(plotBoundaries.xMin);
			chart.getRenderer().setYAxisMin(plotBoundaries.yMin);
			chart.getRenderer().setXAxisMax(plotBoundaries.xMax);
			chart.getRenderer().setYAxisMax(plotBoundaries.yMax);
		}

		graphicalView = new GraphicalView(this, chart);
		graphicalView.addZoomListener(new ZoomListener() {
			@Override
			public void zoomApplied(ZoomEvent e) {
				updateDataSets(chart);
			}

			@Override
			public void zoomReset() {
				updateDataSets(chart);
			}
		}, true, true);

		graphicalView.addPanListener(new PanListener() {
			@Override
			public void panApplied() {
				updateDataSets(chart);
			}

		});
		graphContainer.addView(graphicalView);

		updateDataSets(chart);
	}

	private boolean checkMinMaxValue(@NotNull Double value) {
		return !value.equals(MathHelper.NULL_VALUE);
	}

	private void updateDataSets(@NotNull final XYChart chart) {
		pendingOperation.setObject(new Runnable() {
			@Override
			public void run() {
				// allow only one runner at one time
				synchronized (pendingOperation) {
					//lock all operations with history
					if (pendingOperation.getObject() == this) {
						final XYMultipleSeriesRenderer dr = chart.getRenderer();

						//Log.d(CalculatorPlotActivity.class.getName(), "x = [" + dr.getXAxisMin() + ", " + dr.getXAxisMax() + "], y = [" + dr.getYAxisMin() + ", " + dr.getYAxisMax() + "]");

						final XYSeries realSeries = chart.getDataset().getSeriesAt(0);

						final XYSeries imagSeries;
						if (chart.getDataset().getSeriesCount() > 1) {
							imagSeries = chart.getDataset().getSeriesAt(1);
						} else {
							imagSeries = new XYSeries(getImagFunctionName(CalculatorPlotActivity.this.variable));
						}

						try {
							if (addXY(dr.getXAxisMin(), dr.getXAxisMax(), expression, variable, realSeries, imagSeries, true)) {
								if (chart.getDataset().getSeriesCount() <= 1) {
									chart.getDataset().addSeries(imagSeries);
									chart.getRenderer().addSeriesRenderer(createImagRenderer());
								}
							}
						} catch (ArithmeticException e) {
							// todo serso: translate
							Toast.makeText(CalculatorPlotActivity.this, "Arithmetic error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
							CalculatorPlotActivity.this.finish();
						}

						if (pendingOperation.getObject() == this) {
							graphicalView.repaint();
						}
					}
				}
			}
		});


		new Handler().postDelayed(pendingOperation.getObject(), EVAL_DELAY_MILLIS);
	}

	@NotNull
	private static String getImagFunctionName(@NotNull Constant variable) {
		return "g(" + variable.getName() +")" + " = " + "Im(ƒ(" + variable.getName() +"))";
	}

	@NotNull
	private static String getRealFunctionName(@NotNull Generic expression, @NotNull Constant variable) {
		return "ƒ(" + variable.getName() +")" + " = " + expression.toString();
	}

	@NotNull
	private final static MutableObject<Runnable> pendingOperation = new MutableObject<Runnable>();

	private static XYChart prepareChart(final double minValue, final double maxValue, @NotNull final Generic expression, @NotNull final Constant variable) {
		final XYSeries realSeries = new XYSeries(getRealFunctionName(expression, variable));
		final XYSeries imagSeries = new XYSeries(getImagFunctionName(variable));

		boolean imagExists = addXY(minValue, maxValue, expression, variable, realSeries, imagSeries, false);

		final XYMultipleSeriesDataset data = new XYMultipleSeriesDataset();
		data.addSeries(realSeries);
		if (imagExists) {
			data.addSeries(imagSeries);
		}

		final XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setShowGrid(true);
		renderer.setXTitle(variable.getName());
		renderer.setYTitle("f(" + variable.getName() + ")");
		renderer.setChartTitleTextSize(20);

		renderer.addSeriesRenderer(createCommonRenderer());
		if (imagExists) {
			renderer.addSeriesRenderer(createImagRenderer());
		}

		return new LineChart(data, renderer);
	}

	private static XYSeriesRenderer createImagRenderer() {
		final XYSeriesRenderer imagRenderer = createCommonRenderer();
		imagRenderer.setStroke(BasicStroke.DASHED);
		imagRenderer.setColor(Color.LTGRAY);
		return imagRenderer;
	}

	private static boolean addXY(double minValue, double maxValue, Generic expression, Constant variable, @NotNull XYSeries realSeries, @NotNull XYSeries imagSeries, boolean addExtra) {
		boolean imagExists = false;

		double min = Math.min(minValue, maxValue);
		double max = Math.max(minValue, maxValue);
		double dist = max - min;
		if (addExtra) {
			min = min - dist;
			max = max + dist;
		}

		final int numberOfSteps = DEFAULT_NUMBER_OF_STEPS;
		final double step = Math.max( dist / numberOfSteps, 0.000000001);

		Double prevRealY = null;
		Double prevX = null;
		Double prevImagY = null;

		final MutableInt realSeriesI = new MutableInt(0);
		final MutableInt imagSeriesI = new MutableInt(0);

		double x = min;
		while (x <= max) {

			boolean needToCalculateRealY = needToCalculate(realSeries, step, x, realSeriesI);

			if (needToCalculateRealY) {
				final Complex c = calculatorExpression(expression, variable, x);
				Double y = prepareY(c.realPart());
				if (y != null) {
					addSingularityPoint(realSeries, prevX, x, prevRealY, y);
					realSeries.add(x, y);
					prevRealY = y;
					prevX = x;
				}

				boolean needToCalculateImagY = needToCalculate(imagSeries, step, x, imagSeriesI);
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
				boolean needToCalculateImagY = needToCalculate(imagSeries, step, x, imagSeriesI);
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

		sortSeries(realSeries);
		if (imagExists) {
			sortSeries(imagSeries);
		}

		return imagExists;
	}

	@NotNull
	private static Complex calculatorExpression(@NotNull Generic expression, @NotNull Constant variable, double x) {
		return unwrap(expression.substitute(variable, Expression.valueOf(x)).numeric());
	}

	// todo serso: UNABLE TO PLOT i/ln(t)!!!

	private static void addSingularityPoint(@NotNull XYSeries series, @Nullable Double prevX, @NotNull Double x, @Nullable Double prevY, @NotNull Double y) {
		if (prevX  != null && prevY != null) {
			if ( (Math.abs(y) > 0d && Math.abs(prevY / y) > MAX_Y_DIFF) || (Math.abs(prevY) > 0d && Math.abs(y / prevY) > MAX_Y_DIFF)) {
				//Log.d(CalculatorPlotActivity.class.getName(), "Singularity! Prev point: (" + prevX + ", " + prevY + "), current point: (" +x+ ", " + y +")" );
				//Log.d(CalculatorPlotActivity.class.getName(), String.valueOf(prevX + Math.abs(x - prevX) / 2) +  ", null");
				series.add( prevX + Math.abs(x - prevX) / 2, MathHelper.NULL_VALUE);
			}
		}
	}

	private static boolean needToCalculate(@NotNull XYSeries series, double step, double x, @NotNull MutableInt i) {
		boolean needToCalculateY = true;
		for ( ; i.intValue() < series.getItemCount(); i.increment() ){
			if ( Math.abs(x - series.getX(i.intValue())) < step ) {
				needToCalculateY = false;
				break;
			}
		}
		return needToCalculateY;
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return new PlotBoundaries(chart.getRenderer());
	}

	private static final class PlotBoundaries implements Serializable {

		private final double xMin;
		private final double xMax;
		private final double yMin;
		private final double yMax;


		public PlotBoundaries(double xMin, double xMax, double yMin, double yMax) {
			this.xMin = xMin;
			this.xMax = xMax;
			this.yMin = yMin;
			this.yMax = yMax;
		}

		public PlotBoundaries(@NotNull XYMultipleSeriesRenderer renderer) {
			this.xMin = renderer.getXAxisMin();
			this.yMin = renderer.getYAxisMin();
			this.xMax = renderer.getXAxisMax();
			this.yMax = renderer.getYAxisMax();
		}

		@Override
		public String toString() {
			return "PlotBoundaries{" +
					"yMax=" + yMax +
					", yMin=" + yMin +
					", xMax=" + xMax +
					", xMin=" + xMin +
					'}';
		}
	}

	private static final class Point implements Serializable {
		/**
		 * The X axis coordinate value.
		 */
		private double x;
		/**
		 * The Y axis coordinate value.
		 */
		private double  y;

		public Point() {
		}

		public Point(double  x, double  y) {
			this.x = x;
			this.y = y;
		}

		public double  getX() {
			return x;
		}

		public double  getY() {
			return y;
		}

		public void setX(double  x) {
			this.x = x;
		}

		public void setY(double  y) {
			this.y = y;
		}
	}

	private static void sortSeries(@NotNull XYSeries series) {
		final List<Point> values = new ArrayList<Point>(series.getItemCount());

		for (int i = 0; i < series.getItemCount(); i++) {
			values.add(new Point(series.getX(i), series.getY(i)));
		}

		Collections.sort(values, new Comparator<Point>() {
			@Override
			public int compare(Point point, Point point1) {
				return Double.compare(point.getX(), point1.getX());
			}
		});

		Log.d(CalculatorPlotActivity.class.getName(), "Points for " + series.getTitle());
		series.clear();
		for (Point value : values) {
			series.add(value.getX(), value.getY());
			Log.d(CalculatorPlotActivity.class.getName(), "x = " + value.getX() + ", y = " + value.getY());
		}
	}

	@NotNull
	private static XYSeriesRenderer createCommonRenderer() {
		final XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setFillPoints(true);
		renderer.setPointStyle(PointStyle.POINT);
		renderer.setLineWidth(3);
		renderer.setColor(Color.WHITE);
		renderer.setStroke(BasicStroke.SOLID);
		return renderer;
	}

	@Nullable
	private static Double prepareY(double y) {
		if (Double.isNaN(y)) {
			return null;
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
