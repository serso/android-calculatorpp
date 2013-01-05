/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.plot;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import jscl.math.Generic;
import jscl.math.function.Constant;
import org.achartengine.chart.XYChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.tools.PanListener;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorPreferences;
import org.solovyev.android.calculator.R;
import org.solovyev.common.MutableObject;

/**
 * User: serso
 * Date: 12/1/11
 * Time: 12:40 AM
 */
public class CalculatorPlotFragment extends AbstractCalculatorPlotFragment {

    public static final long EVAL_DELAY_MILLIS = 200;

    @Nullable
    private XYChart chart;

    /**
     * The encapsulated graphical view.
     */
    @Nullable
    private MyGraphicalView graphicalView;

    protected void createChart(@NotNull PreparedInput preparedInput) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        final Boolean interpolate = CalculatorPreferences.Graph.interpolate.getPreference(preferences);
        final GraphLineColor realLineColor = CalculatorPreferences.Graph.lineColorReal.getPreference(preferences);
        final GraphLineColor imagLineColor = CalculatorPreferences.Graph.lineColorImag.getPreference(preferences);

        //noinspection ConstantConditions
        try {
            this.chart = PlotUtils.prepareChart(getMinValue(null), getMaxValue(null), preparedInput.getExpression(), preparedInput.getXVariable(), getBgColor(), interpolate, realLineColor.getColor(), imagLineColor.getColor());
        } catch (ArithmeticException e) {
            PlotUtils.handleArithmeticException(e, CalculatorPlotFragment.this);
        }
    }

    @Override
    protected boolean is3dPlotSupported() {
        return false;
    }

    @Nullable
    @Override
    protected PlotBoundaries getPlotBoundaries() {
        if (chart != null) {
            return new PlotBoundaries(chart.getRenderer());
        } else {
            return null;
        }
    }

    protected void createGraphicalView(@NotNull View root, @Nullable PreparedInput preparedInput) {
        final ViewGroup graphContainer = (ViewGroup) root.findViewById(R.id.main_fragment_layout);

        if (graphicalView != null) {
            graphContainer.removeView(graphicalView);
        }

        if (!getPreparedInput().isError()) {
            final XYChart chart = this.chart;
            assert chart != null;

            final PlotBoundaries plotBoundaries = preparedInput.getPlotBoundaries();
            double minValue = getMinValue(plotBoundaries);
            double maxValue = getMaxValue(plotBoundaries);

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

            if (preparedInput == null) {
                chart.getRenderer().setXAxisMin(Math.max(minX, minValue));
                chart.getRenderer().setYAxisMin(Math.max(minY, minValue));
                chart.getRenderer().setXAxisMax(Math.min(maxX, maxValue));
                chart.getRenderer().setYAxisMax(Math.min(maxY, maxValue));
            } else {
                chart.getRenderer().setXAxisMin(plotBoundaries.getXMin());
                chart.getRenderer().setYAxisMin(plotBoundaries.getYMin());
                chart.getRenderer().setXAxisMax(plotBoundaries.getXMax());
                chart.getRenderer().setYAxisMax(plotBoundaries.getYMax());
            }

            graphicalView = new MyGraphicalView(this.getActivity(), chart);
            graphicalView.setBackgroundColor(this.getBgColor());

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

            updateDataSets(chart, 50);
        } else {
            graphicalView = null;
        }

    }


    private void updateDataSets(@NotNull final XYChart chart) {
        updateDataSets(chart, EVAL_DELAY_MILLIS);
    }

    private void updateDataSets(@NotNull final XYChart chart, long millisToWait) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        final GraphLineColor imagLineColor = CalculatorPreferences.Graph.lineColorImag.getPreference(preferences);

        final PreparedInput preparedInput = getPreparedInput();

        final Generic expression = preparedInput.getExpression();
        final Constant variable = preparedInput.getXVariable();
        final MyGraphicalView graphicalView = this.graphicalView;

        if (expression != null && variable != null && graphicalView != null) {
            pendingOperation.setObject(new Runnable() {
                @Override
                public void run() {
                    // allow only one runner at one time
                    synchronized (pendingOperation) {
                        //lock all operations with history
                        if (pendingOperation.getObject() == this) {

                            getPlotExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    final XYMultipleSeriesRenderer dr = chart.getRenderer();

                                    final XYMultipleSeriesDataset dataset = chart.getDataset();
                                    if (dataset != null && dr != null) {
                                        final MyXYSeries realSeries = (MyXYSeries) dataset.getSeriesAt(0);

                                        if (realSeries != null) {
                                            final MyXYSeries imagSeries;
                                            if (dataset.getSeriesCount() > 1) {
                                                imagSeries = (MyXYSeries) dataset.getSeriesAt(1);
                                            } else {
                                                imagSeries = new MyXYSeries(PlotUtils.getImagFunctionName(variable), PlotUtils.DEFAULT_NUMBER_OF_STEPS * 2);
                                            }

                                            try {
                                                if (PlotUtils.addXY(dr.getXAxisMin(), dr.getXAxisMax(), expression, variable, realSeries, imagSeries, true, PlotUtils.DEFAULT_NUMBER_OF_STEPS)) {
                                                    if (dataset.getSeriesCount() <= 1) {
                                                        dataset.addSeries(imagSeries);
                                                        dr.addSeriesRenderer(PlotUtils.createImagRenderer(imagLineColor.getColor()));
                                                    }
                                                }
                                            } catch (ArithmeticException e) {
                                                PlotUtils.handleArithmeticException(e, CalculatorPlotFragment.this);
                                            }

                                            getUiHandler().post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    graphicalView.repaint();
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }


        getUiHandler().postDelayed(pendingOperation.getObject(), millisToWait);
    }

    @NotNull
    private final MutableObject<Runnable> pendingOperation = new MutableObject<Runnable>();


/*    public void zoomInClickHandler(@NotNull View v) {
        this.graphicalView.zoomIn();
    }

    public void zoomOutClickHandler(@NotNull View v) {
        this.graphicalView.zoomOut();
    }*/


    public void onError() {
        this.chart = null;
    }
}
