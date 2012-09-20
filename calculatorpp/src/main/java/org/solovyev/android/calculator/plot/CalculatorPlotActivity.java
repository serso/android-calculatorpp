/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.plot;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;
import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.function.Constant;
import jscl.text.ParseException;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.CubicLineChart;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.CalculatorParseException;
import org.solovyev.android.calculator.PreparedExpression;
import org.solovyev.android.calculator.ToJsclTextProcessor;
import org.solovyev.common.MutableObject;

import java.io.Serializable;

/**
 * User: serso
 * Date: 12/1/11
 * Time: 12:40 AM
 */
public class CalculatorPlotActivity extends Activity {

	private static final String TAG = CalculatorPlotActivity.class.getSimpleName();

	private static final int DEFAULT_NUMBER_OF_STEPS = 100;

	private static final int DEFAULT_MIN_NUMBER = -10;

	private static final int DEFAULT_MAX_NUMBER = 10;

	public static final String INPUT = "org.solovyev.android.calculator.CalculatorPlotActivity_input";

	public static final long EVAL_DELAY_MILLIS = 200;

	private XYChart chart;

	/**
	 * The encapsulated graphical view.
	 */
	private GraphicalView graphicalView;

	@NotNull
	private Generic expression;

	@NotNull
	private Constant variable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();

		final Input input = (Input) extras.getSerializable(INPUT);

		try {
            final PreparedExpression preparedExpression = ToJsclTextProcessor.getInstance().process(input.getExpression());
            this.expression = Expression.valueOf(preparedExpression.getExpression());
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
		} catch (CalculatorParseException e) {
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
			minX = Math.min(minX, series.getMinX());
			minY = Math.min(minY, series.getMinY());
			maxX = Math.max(maxX, series.getMaxX());
			maxY = Math.max(maxY, series.getMaxY());
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
				Log.d(TAG, "org.achartengine.tools.PanListener.panApplied");
				updateDataSets(chart);
			}

		});
		graphContainer.addView(graphicalView);

		updateDataSets(chart, 50);
	}


	private void updateDataSets(@NotNull final XYChart chart) {
		updateDataSets(chart, EVAL_DELAY_MILLIS);
	}

	private void updateDataSets(@NotNull final XYChart chart, long millisToWait) {
		pendingOperation.setObject(new Runnable() {
			@Override
			public void run() {
				// allow only one runner at one time
				synchronized (pendingOperation) {
					//lock all operations with history
					if (pendingOperation.getObject() == this) {

						Log.d(TAG, "org.solovyev.android.calculator.plot.CalculatorPlotActivity.updateDataSets");

						final XYMultipleSeriesRenderer dr = chart.getRenderer();

						//Log.d(CalculatorPlotActivity.class.getName(), "x = [" + dr.getXAxisMin() + ", " + dr.getXAxisMax() + "], y = [" + dr.getYAxisMin() + ", " + dr.getYAxisMax() + "]");

						final MyXYSeries realSeries = (MyXYSeries)chart.getDataset().getSeriesAt(0);

						final MyXYSeries imagSeries;
						if (chart.getDataset().getSeriesCount() > 1) {
							imagSeries = (MyXYSeries)chart.getDataset().getSeriesAt(1);
						} else {
							imagSeries = new MyXYSeries(getImagFunctionName(CalculatorPlotActivity.this.variable), DEFAULT_NUMBER_OF_STEPS * 2);
						}

						try {
							if (PlotUtils.addXY(dr.getXAxisMin(), dr.getXAxisMax(), expression, variable, realSeries, imagSeries, true, DEFAULT_NUMBER_OF_STEPS)) {
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


		new Handler().postDelayed(pendingOperation.getObject(), millisToWait);
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
		final MyXYSeries realSeries = new MyXYSeries(getRealFunctionName(expression, variable), DEFAULT_NUMBER_OF_STEPS * 2);
		final MyXYSeries imagSeries = new MyXYSeries(getImagFunctionName(variable), DEFAULT_NUMBER_OF_STEPS * 2);

		boolean imagExists = PlotUtils.addXY(minValue, maxValue, expression, variable, realSeries, imagSeries, false, DEFAULT_NUMBER_OF_STEPS);

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

		renderer.setZoomEnabled(true);
		renderer.setZoomButtonsVisible(true);

		renderer.addSeriesRenderer(createCommonRenderer());
		if (imagExists) {
			renderer.addSeriesRenderer(createImagRenderer());
		}

		return new CubicLineChart(data, renderer, 0.1f);
	}

	private static XYSeriesRenderer createImagRenderer() {
		final XYSeriesRenderer imagRenderer = createCommonRenderer();
		imagRenderer.setStroke(BasicStroke.DASHED);
		imagRenderer.setColor(Color.LTGRAY);
		return imagRenderer;
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

	public void zoomInClickHandler(@NotNull View v) {
		this.graphicalView.zoomIn();
	}

	public void zoomOutClickHandler(@NotNull View v) {
		this.graphicalView.zoomOut();
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
