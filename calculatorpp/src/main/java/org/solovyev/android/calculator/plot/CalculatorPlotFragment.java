/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.plot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.function.Constant;
import jscl.text.ParseException;
import org.achartengine.GraphicalView;
import org.achartengine.chart.XYChart;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.tools.PanListener;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.*;
import org.solovyev.android.menu.ActivityMenu;
import org.solovyev.android.menu.LabeledMenuItem;
import org.solovyev.android.menu.ListActivityMenu;
import org.solovyev.android.sherlock.menu.SherlockMenuHelper;
import org.solovyev.common.MutableObject;
import org.solovyev.common.collections.CollectionsUtils;

import java.io.Serializable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * User: serso
 * Date: 12/1/11
 * Time: 12:40 AM
 */
public class CalculatorPlotFragment extends SherlockFragment implements CalculatorEventListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = CalculatorPlotFragment.class.getSimpleName();

    private static final int DEFAULT_MIN_NUMBER = -10;

    private static final int DEFAULT_MAX_NUMBER = 10;

    public static final String INPUT = "plotter_input";
    private static final String PLOT_BOUNDARIES = "plot_boundaries";

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

    @NotNull
    private final CalculatorFragmentHelper fragmentHelper = CalculatorApplication.getInstance().createFragmentHelper(R.layout.plot_fragment, R.string.c_graph, false);

    @NotNull
    private final Executor plotExecutor = Executors.newSingleThreadExecutor();

    @NotNull
    private final Handler uiHandler = new Handler();

    @Nullable
    private Input input = null;

    private boolean inputFromArgs = true;

    @NotNull
    private CalculatorEventData lastCalculatorEventData = CalculatorUtils.createFirstEventDataId();

    private int bgColor;

    @Nullable
    private PlotBoundaries plotBoundaries = null;

    @NotNull
    private ActivityMenu<Menu, MenuItem> fragmentMenu = ListActivityMenu.fromList(PlotMenu.class, SherlockMenuHelper.getInstance());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.fragmentHelper.onCreate(this);


        final Bundle arguments = getArguments();

        if (arguments != null) {
            input = (Input) arguments.getSerializable(INPUT);
        }

        if (input == null) {
            inputFromArgs = false;
            createInputFromDisplayState(CalculatorLocatorImpl.getInstance().getDisplay().getViewState());
            this.bgColor = getResources().getColor(R.color.pane_background);
        } else {
            this.bgColor = getResources().getColor(android.R.color.transparent);
            prepareData();
        }

        PreferenceManager.getDefaultSharedPreferences(this.getActivity()).registerOnSharedPreferenceChangeListener(this);

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    private void createInputFromDisplayState(@NotNull CalculatorDisplayViewState displayState) {
        try {
            if (displayState.isValid() && displayState.getResult() != null) {
                final Generic expression = displayState.getResult();
                if (CalculatorUtils.isPlotPossible(expression, displayState.getOperation())) {
                    final Constant constant = CollectionsUtils.getFirstCollectionElement(CalculatorUtils.getNotSystemConstants(expression));
                    input = new Input(expression.toString(), constant.getName());

                    prepareData();
                }
            }
        } catch (RuntimeException e) {
            this.input = null;
            Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    private void prepareData(){
        try {
            if (input != null) {
                final PreparedExpression preparedExpression = ToJsclTextProcessor.getInstance().process(input.getExpression());
                this.expression = Expression.valueOf(preparedExpression.getExpression());
                this.variable = new Constant(input.getVariableName());

                initChart();
            }
        } catch (ParseException e) {
            this.input = null;
            Toast.makeText(this.getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        } catch (CalculatorParseException e) {
            this.input = null;
            Toast.makeText(this.getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initChart() {
        if (input != null) {
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            final Boolean interpolate = CalculatorPreferences.Graph.interpolate.getPreference(preferences);
            final GraphLineColor realLineColor = CalculatorPreferences.Graph.lineColorReal.getPreference(preferences);
            final GraphLineColor imagLineColor = CalculatorPreferences.Graph.lineColorImag.getPreference(preferences);

            this.chart = PlotUtils.prepareChart(getMinValue(null), getMaxValue(null), this.expression, variable, bgColor, interpolate, realLineColor.getColor(), imagLineColor.getColor());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return this.fragmentHelper.onCreateView(this, inflater, container);
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        this.fragmentHelper.onViewCreated(this, root);


        /*if ( savedInstanceState != null ) {
            final Object object = savedInstanceState.getSerializable(PLOT_BOUNDARIES);
            if ( object instanceof PlotBoundaries) {
                plotBoundaries = ((PlotBoundaries) object);
            }
        }*/

        updateGraphicalView(root, plotBoundaries);
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);

        /*if (chart != null) {
            out.putSerializable(PLOT_BOUNDARIES, new PlotBoundaries(chart.getRenderer()));
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();

        this.fragmentHelper.onResume(this);

        if ( !inputFromArgs ) {
            createInputFromDisplayState(CalculatorLocatorImpl.getInstance().getDisplay().getViewState());
            updateGraphicalView(getView(), null);
        }
    }

    @Override
    public void onPause() {
        this.fragmentHelper.onPause(this);

        super.onPause();
    }

    private void updateGraphicalView(@NotNull View root, @Nullable PlotBoundaries plotBoundaries) {
        if (input != null) {
            setGraphicalView(root, plotBoundaries);
        } else {
            Toast.makeText(this.getActivity(), "Plot is not possible!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        this.fragmentHelper.onDestroy(this);

        PreferenceManager.getDefaultSharedPreferences(this.getActivity()).unregisterOnSharedPreferenceChangeListener(this);

        super.onDestroy();
    }

    private void setGraphicalView(@NotNull View root, @Nullable PlotBoundaries plotBoundaries) {
        double minValue = getMinValue(plotBoundaries);
        double maxValue = getMaxValue(plotBoundaries);

        final ViewGroup graphContainer = (ViewGroup) root.findViewById(R.id.main_fragment_layout);

        if (graphicalView != null) {
            graphContainer.removeView(graphicalView);
        }

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

        Log.d(CalculatorPlotFragment.class.getName(), "min x: " + minX + ", min y: " + minY + ", max x: " + maxX + ", max y: " + maxY);
        Log.d(CalculatorPlotFragment.class.getName(), "Plot boundaries are " + plotBoundaries);


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

        graphicalView = new GraphicalView(this.getActivity(), chart);
        graphicalView.setBackgroundColor(this.bgColor);

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

    private double getMaxValue(@Nullable PlotBoundaries plotBoundaries) {
        return plotBoundaries == null ? DEFAULT_MAX_NUMBER : plotBoundaries.xMax;
    }

    private double getMinValue(@Nullable PlotBoundaries plotBoundaries) {
        return plotBoundaries == null ? DEFAULT_MIN_NUMBER : plotBoundaries.xMin;
    }


    private void updateDataSets(@NotNull final XYChart chart) {
        updateDataSets(chart, EVAL_DELAY_MILLIS);
    }

    private void updateDataSets(@NotNull final XYChart chart, long millisToWait) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        final GraphLineColor imagLineColor = CalculatorPreferences.Graph.lineColorImag.getPreference(preferences);

        pendingOperation.setObject(new Runnable() {
            @Override
            public void run() {
                // allow only one runner at one time
                synchronized (pendingOperation) {
                    //lock all operations with history
                    if (pendingOperation.getObject() == this) {

                        plotExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "org.solovyev.android.calculator.plot.CalculatorPlotActivity.updateDataSets");

                                final XYMultipleSeriesRenderer dr = chart.getRenderer();

                                //Log.d(CalculatorPlotActivity.class.getName(), "x = [" + dr.getXAxisMin() + ", " + dr.getXAxisMax() + "], y = [" + dr.getYAxisMin() + ", " + dr.getYAxisMax() + "]");

                                final MyXYSeries realSeries = (MyXYSeries) chart.getDataset().getSeriesAt(0);

                                final MyXYSeries imagSeries;
                                if (chart.getDataset().getSeriesCount() > 1) {
                                    imagSeries = (MyXYSeries) chart.getDataset().getSeriesAt(1);
                                } else {
                                    imagSeries = new MyXYSeries(PlotUtils.getImagFunctionName(CalculatorPlotFragment.this.variable), PlotUtils.DEFAULT_NUMBER_OF_STEPS * 2);
                                }

                                try {
                                    if (PlotUtils.addXY(dr.getXAxisMin(), dr.getXAxisMax(), expression, variable, realSeries, imagSeries, true, PlotUtils.DEFAULT_NUMBER_OF_STEPS)) {
                                        if (chart.getDataset().getSeriesCount() <= 1) {
                                            chart.getDataset().addSeries(imagSeries);
                                            chart.getRenderer().addSeriesRenderer(PlotUtils.createImagRenderer(imagLineColor.getColor()));
                                        }
                                    }
                                } catch (ArithmeticException e) {
                                    // todo serso: translate
                                    Toast.makeText(CalculatorPlotFragment.this.getActivity(), "Arithmetic error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }

                                if (pendingOperation.getObject() == this) {
                                    uiHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            graphicalView.repaint();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });


        uiHandler.postDelayed(pendingOperation.getObject(), millisToWait);
    }

    @NotNull
    private final MutableObject<Runnable> pendingOperation = new MutableObject<Runnable>();

    @Override
    public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable final Object data) {
        if ( calculatorEventType.isOfType(CalculatorEventType.display_state_changed) ) {
            if ( !inputFromArgs ) {
                if ( calculatorEventData.isAfter(this.lastCalculatorEventData) ) {
                    this.lastCalculatorEventData = calculatorEventData;

                    createInputFromDisplayState(((CalculatorDisplayChangeEventData) data).getNewValue());

                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            final View view = getView();
                            if (view != null) {
                                updateGraphicalView(view, null);
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(@NotNull SharedPreferences preferences, @NotNull String key) {
        if ( CalculatorPreferences.Graph.interpolate.getKey().equals(key) ||
                CalculatorPreferences.Graph.lineColorReal.getKey().equals(key) ||
                    CalculatorPreferences.Graph.lineColorImag.getKey().equals(key)) {
            initChart();
            updateGraphicalView(getView(), plotBoundaries);
        }
    }

    /*@Override
    public Object onRetainNonConfigurationInstance() {
        return new PlotBoundaries(chart.getRenderer());
    }*/


    public void zoomInClickHandler(@NotNull View v) {
        this.graphicalView.zoomIn();
    }

    public void zoomOutClickHandler(@NotNull View v) {
        this.graphicalView.zoomOut();
    }

    /*
    **********************************************************************
    *
    *                           MENU
    *
    **********************************************************************
    */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        fragmentMenu.onCreateOptionsMenu(this.getActivity(), menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        fragmentMenu.onPrepareOptionsMenu(this.getActivity(), menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item) || fragmentMenu.onOptionsItemSelected(this.getActivity(), item);
    }

    /*
    **********************************************************************
    *
    *                           STATIC
    *
    **********************************************************************
    */

    private static enum PlotMenu implements LabeledMenuItem<MenuItem> {

        preferences(R.string.c_settings) {
            @Override
            public void onClick(@NotNull MenuItem data, @NotNull Context context) {
                context.startActivity(new Intent(context, CalculatorPlotPreferenceActivity.class));
            }
        };

        private final int captionResId;

        private PlotMenu(int captionResId) {
            this.captionResId = captionResId;
        }

        @NotNull
        @Override
        public String getCaption(@NotNull Context context) {
            return context.getString(captionResId);
        }
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
