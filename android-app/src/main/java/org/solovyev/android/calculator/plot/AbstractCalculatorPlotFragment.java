package org.solovyev.android.calculator.plot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.*;
import org.solovyev.android.menu.AMenuItem;
import org.solovyev.android.menu.ActivityMenu;
import org.solovyev.android.menu.IdentifiableMenuItem;
import org.solovyev.android.menu.ListActivityMenu;
import org.solovyev.android.sherlock.menu.SherlockMenuHelper;
import org.solovyev.common.JPredicate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * User: serso
 * Date: 12/30/12
 * Time: 3:09 PM
 */
public abstract class AbstractCalculatorPlotFragment extends CalculatorFragment implements CalculatorEventListener {

    /*
    **********************************************************************
    *
    *                           CONSTANTS
    *
    **********************************************************************
    */

    protected static final String TAG = "CalculatorPlotFragment";

    protected static final String PLOT_BOUNDARIES = "plot_boundaries";

    private static final int DEFAULT_MIN_NUMBER = -10;

    private static final int DEFAULT_MAX_NUMBER = 10;

    /*
    **********************************************************************
    *
    *                           FIELDS
    *
    **********************************************************************
    */

    private int bgColor;

    // thread for applying UI changes
    @NotNull
    private final Handler uiHandler = new Handler();

    @NotNull
    private PlotData plotData = new PlotData(Collections.<PlotFunction>emptyList(), false);

    @NotNull
    private ActivityMenu<Menu, MenuItem> fragmentMenu;

    // thread which calculated data for graph view
    @NotNull
    private final Executor plotExecutor = Executors.newSingleThreadExecutor();

    @NotNull
    private final CalculatorEventHolder lastEventHolder = new CalculatorEventHolder(CalculatorUtils.createFirstEventDataId());


    public AbstractCalculatorPlotFragment() {
        super(CalculatorApplication.getInstance().createFragmentHelper(R.layout.plot_fragment, R.string.c_graph, false));
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // todo serso: init variable properly
        boolean paneFragment = true;
        if (paneFragment) {
            this.bgColor = getResources().getColor(R.color.cpp_pane_background);
        } else {
            this.bgColor = getResources().getColor(android.R.color.transparent);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);

        final PlotBoundaries plotBoundaries = getPlotBoundaries();
        if (plotBoundaries != null) {
            out.putSerializable(PLOT_BOUNDARIES, plotBoundaries);
        }
    }

    @Nullable
    protected abstract PlotBoundaries getPlotBoundaries();

    @Override
    public void onResume() {
        super.onResume();

        plotData = Locator.getInstance().getPlotter().getPlotData();
        createChart(plotData);
        createGraphicalView(getView(), plotData);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable final Object data) {
        if (calculatorEventType.isOfType(CalculatorEventType.plot_data_changed)) {
            final CalculatorEventHolder.Result result = this.lastEventHolder.apply(calculatorEventData);
            if (result.isNewAfter()) {
                onNewPlotData((PlotData) data);
            }
        }
    }

    private void onNewPlotData(@NotNull final PlotData plotData) {
        this.plotData = plotData;

        getUiHandler().post(new Runnable() {
            @Override
            public void run() {
                getActivity().invalidateOptionsMenu();

                createChart(plotData);

                final View view = getView();
                if (view != null) {
                    createGraphicalView(view, plotData);
                }
            }
        });
    }

    protected abstract void onError();

    protected abstract void createGraphicalView(@NotNull View view, @NotNull PlotData plotData);

    protected abstract void createChart(@NotNull PlotData plotData);


    protected double getMaxValue(@Nullable PlotBoundaries plotBoundaries) {
        return plotBoundaries == null ? DEFAULT_MAX_NUMBER : plotBoundaries.getXMax();
    }

    protected double getMinValue(@Nullable PlotBoundaries plotBoundaries) {
        return plotBoundaries == null ? DEFAULT_MIN_NUMBER : plotBoundaries.getXMin();
    }

    /*
    **********************************************************************
    *
    *                           GETTERS
    *
    **********************************************************************
    */

    @NotNull
    public Handler getUiHandler() {
        return uiHandler;
    }

    public int getBgColor() {
        return bgColor;
    }

    @NotNull
    public Executor getPlotExecutor() {
        return plotExecutor;
    }

    /*
    **********************************************************************
    *
    *                           MENU
    *
    **********************************************************************
    */

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        final List<IdentifiableMenuItem<MenuItem>> menuItems = new ArrayList<IdentifiableMenuItem<MenuItem>>();
        menuItems.add(PlotMenu.preferences);
        menuItems.add(PlotMenu.functions);

        final IdentifiableMenuItem<MenuItem> plot3dMenuItem = new IdentifiableMenuItem<MenuItem>() {
            @NotNull
            @Override
            public Integer getItemId() {
                return R.id.menu_plot_3d;
            }

            @Override
            public void onClick(@NotNull MenuItem data, @NotNull Context context) {
                Locator.getInstance().getPlotter().setPlot3d(true);
            }
        };
        menuItems.add(plot3dMenuItem);


        final IdentifiableMenuItem<MenuItem> plot2dMenuItem = new IdentifiableMenuItem<MenuItem>() {
            @NotNull
            @Override
            public Integer getItemId() {
                return R.id.menu_plot_2d;
            }

            @Override
            public void onClick(@NotNull MenuItem data, @NotNull Context context) {
                Locator.getInstance().getPlotter().setPlot3d(false);
            }
        };
        menuItems.add(plot2dMenuItem);

        final boolean plot3dVisible = !plotData.isPlot3d() && is3dPlotSupported();
        final boolean plot2dVisible = plotData.isPlot3d() && Locator.getInstance().getPlotter().is2dPlotPossible();
        fragmentMenu = ListActivityMenu.fromResource(R.menu.plot_menu, menuItems, SherlockMenuHelper.getInstance(), new JPredicate<AMenuItem<MenuItem>>() {
            @Override
            public boolean apply(@Nullable AMenuItem<MenuItem> menuItem) {
                if ( menuItem == plot3dMenuItem ) {
                    return !plot3dVisible;
                } else if ( menuItem == plot2dMenuItem ) {
                    return !plot2dVisible;
                }
                return false;
            }
        });

        final FragmentActivity activity = this.getActivity();
        if (activity != null) {
            fragmentMenu.onCreateOptionsMenu(activity, menu);
        }
    }

    protected abstract boolean is3dPlotSupported();

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        final FragmentActivity activity = this.getActivity();
        if (activity != null) {
            fragmentMenu.onPrepareOptionsMenu(activity, menu);
        }
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

    private static enum PlotMenu implements IdentifiableMenuItem<MenuItem> {

        functions(R.id.menu_plot_functions) {
            @Override
            public void onClick(@NotNull MenuItem data, @NotNull Context context) {
                context.startActivity(new Intent(context, CalculatorPlotFunctionsActivity.class));
            }
        },

        preferences(R.id.menu_plot_settings) {
            @Override
            public void onClick(@NotNull MenuItem data, @NotNull Context context) {
                context.startActivity(new Intent(context, CalculatorPlotPreferenceActivity.class));
            }
        };

        private final int itemId;

        private PlotMenu(int itemId) {
            this.itemId = itemId;
        }


        @NotNull
        @Override
        public Integer getItemId() {
            return itemId;
        }
    }

    public static final class PlotBoundaries implements Serializable {

        private double xMin;
        private double xMax;
        private double yMin;
        private double yMax;

        public PlotBoundaries() {
        }

        public PlotBoundaries(@NotNull XYMultipleSeriesRenderer renderer) {
            this.xMin = renderer.getXAxisMin();
            this.yMin = renderer.getYAxisMin();
            this.xMax = renderer.getXAxisMax();
            this.yMax = renderer.getYAxisMax();
        }

        public double getXMin() {
            return xMin;
        }

        public double getXMax() {
            return xMax;
        }

        public double getYMin() {
            return yMin;
        }

        public double getYMax() {
            return yMax;
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

        @NotNull
        public static PlotBoundaries newDefaultInstance() {
            PlotBoundaries plotBoundaries = new PlotBoundaries();
            plotBoundaries.xMin = DEFAULT_MIN_NUMBER;
            plotBoundaries.yMin = DEFAULT_MIN_NUMBER;
            plotBoundaries.xMax = DEFAULT_MAX_NUMBER;
            plotBoundaries.yMax = DEFAULT_MAX_NUMBER;
            return plotBoundaries;
        }
    }

    public static void applyToPaint(@NotNull PlotFunctionLineDef plotFunctionLineDef, @NotNull Paint paint) {
        paint.setColor(plotFunctionLineDef.getLineColor());
        paint.setStyle(Paint.Style.STROKE);

        if ( plotFunctionLineDef.getLineWidth() == PlotFunctionLineDef.DEFAULT_LINE_WIDTH ) {
            paint.setStrokeWidth(0);
        } else {
            paint.setStrokeWidth(plotFunctionLineDef.getLineWidth());
        }

        final AndroidPlotLineStyle androidPlotLineStyle = AndroidPlotLineStyle.valueOf(plotFunctionLineDef.getLineStyle());
        if (androidPlotLineStyle != null) {
            androidPlotLineStyle.applyToPaint(paint);
        }
    }

}
