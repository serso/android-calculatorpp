package org.solovyev.android.calculator.plot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.function.Constant;
import jscl.text.ParseException;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.*;
import org.solovyev.android.menu.ActivityMenu;
import org.solovyev.android.menu.IdentifiableMenuItem;
import org.solovyev.android.menu.ListActivityMenu;
import org.solovyev.android.sherlock.menu.SherlockMenuHelper;

import java.io.Serializable;
import java.util.ArrayList;
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

    public static final String INPUT = "plotter_input";

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

    @Nullable
    private Input input;

    private int bgColor;

    // thread for applying UI changes
    @NotNull
    private final Handler uiHandler = new Handler();

    @NotNull
    private PreparedInput preparedInput;

    @NotNull
    private ActivityMenu<Menu, MenuItem> fragmentMenu = ListActivityMenu.fromResource(R.menu.plot_menu, PlotMenu.class, SherlockMenuHelper.getInstance());

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

        final Bundle arguments = getArguments();

        if (arguments != null) {
            input = (CalculatorPlotFragment.Input) arguments.getSerializable(INPUT);
        }

        if (input == null) {
            this.bgColor = getResources().getColor(R.color.cpp_pane_background);
        } else {
            this.bgColor = getResources().getColor(android.R.color.transparent);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (input == null) {
            this.preparedInput = prepareInputFromDisplay(Locator.getInstance().getDisplay().getViewState(), savedInstanceState);
        } else {
            this.preparedInput = prepareInput(input, true, savedInstanceState);
        }
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

        createChart(preparedInput);
        createGraphicalView(getView(), preparedInput);
    }

    @Override
    public void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable final Object data) {
        if (calculatorEventType.isOfType(CalculatorEventType.display_state_changed)) {
            PreparedInput preparedInput = getPreparedInput();
            if (!preparedInput.isFromInputArgs()) {

                final CalculatorEventHolder.Result result = this.lastEventHolder.apply(calculatorEventData);
                if (result.isNewAfter()) {
                    preparedInput = prepareInputFromDisplay(((CalculatorDisplayChangeEventData) data).getNewValue(), null);
                    this.preparedInput = preparedInput;


                    final PreparedInput finalPreparedInput = preparedInput;
                    getUiHandler().post(new Runnable() {
                        @Override
                        public void run() {

                            if (!finalPreparedInput.isError()) {
                                createChart(finalPreparedInput);

                                final View view = getView();
                                if (view != null) {
                                    createGraphicalView(view, finalPreparedInput);
                                }
                            } else {
                                onError();
                            }
                        }
                    });
                }

            }
        }
    }

    protected abstract void onError();

    protected abstract void createGraphicalView(@NotNull View view, @NotNull PreparedInput preparedInput);

    protected abstract void createChart(@NotNull PreparedInput preparedInput);


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

    @NotNull
    public PreparedInput getPreparedInput() {
        return preparedInput;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        final FragmentActivity activity = this.getActivity();
        if (activity != null) {
            fragmentMenu.onCreateOptionsMenu(activity, menu);
        }
    }

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

    @NotNull
    protected static PreparedInput prepareInputFromDisplay(@NotNull CalculatorDisplayViewState displayState, @Nullable Bundle savedInstanceState) {
        try {
            if (displayState.isValid() && displayState.getResult() != null) {
                final Generic expression = displayState.getResult();
                if (CalculatorUtils.isPlotPossible(expression, displayState.getOperation())) {
                    final List<Constant> variables = new ArrayList<Constant>(CalculatorUtils.getNotSystemConstants(expression));
                    final Constant xVariable = variables.get(0);

                    final Constant yVariable;
                    if ( variables.size() > 1 ) {
                        yVariable = variables.get(1);
                    } else {
                        yVariable = null;
                    }

                    final Input input = new Input(expression.toString(), xVariable.getName(), yVariable == null ? null : yVariable.getName());
                    return prepareInput(input, false, savedInstanceState);
                }
            }
        } catch (RuntimeException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }

        return PreparedInput.newErrorInstance(false);
    }

    @NotNull
    private static PreparedInput prepareInput(@NotNull Input input, boolean fromInputArgs, @Nullable Bundle savedInstanceState) {
        PreparedInput result;

        try {
            final PreparedExpression preparedExpression = ToJsclTextProcessor.getInstance().process(input.getExpression());
            final Generic expression = Expression.valueOf(preparedExpression.getExpression());
            final Constant xVar = new Constant(input.getXVariableName());

            final Constant yVar;
            if (input.getYVariableName() != null) {
                yVar = new Constant(input.getYVariableName());
            } else {
                yVar = null;
            }

            PlotBoundaries plotBoundaries = null;
            if (savedInstanceState != null) {
                plotBoundaries = (PlotBoundaries) savedInstanceState.getSerializable(PLOT_BOUNDARIES);
            }

            if ( plotBoundaries == null ) {
                plotBoundaries = PlotBoundaries.newDefaultInstance();
            }

            result = PreparedInput.newInstance(input, expression, xVar, yVar, fromInputArgs, plotBoundaries);
        } catch (ParseException e) {
            result = PreparedInput.newErrorInstance(fromInputArgs);
            Locator.getInstance().getNotifier().showMessage(e);
        } catch (CalculatorParseException e) {
            result = PreparedInput.newErrorInstance(fromInputArgs);
            Locator.getInstance().getNotifier().showMessage(e);
        }

        return result;
    }

    private static enum PlotMenu implements IdentifiableMenuItem<MenuItem> {

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

    public static class PreparedInput {

        @Nullable
        private Input input;

        @Nullable
        private Generic expression;

        @Nullable
        private Constant xVariable;

        @Nullable
        private Constant yVariable;

        private boolean fromInputArgs;

        @NotNull
        private PlotBoundaries plotBoundaries = PlotBoundaries.newDefaultInstance();

        private PreparedInput() {
        }

        @NotNull
        public static PreparedInput newInstance(@NotNull Input input,
                                                @NotNull Generic expression,
                                                @NotNull Constant xVariable,
                                                @Nullable Constant yVariable,
                                                boolean fromInputArgs,
                                                @NotNull  PlotBoundaries plotBoundaries) {
            final PreparedInput result = new PreparedInput();

            result.input = input;
            result.expression = expression;
            result.xVariable = xVariable;
            result.yVariable = yVariable;
            result.fromInputArgs = fromInputArgs;
            result.plotBoundaries = plotBoundaries;

            return result;
        }

        @NotNull
        public static PreparedInput newErrorInstance(boolean fromInputArgs) {
            final PreparedInput result = new PreparedInput();

            result.input = null;
            result.expression = null;
            result.xVariable = null;
            result.yVariable = null;
            result.fromInputArgs = fromInputArgs;

            return result;
        }

        public boolean isFromInputArgs() {
            return fromInputArgs;
        }

        @Nullable
        public Input getInput() {
            return input;
        }

        @Nullable
        public Generic getExpression() {
            return expression;
        }

        @NotNull
        public PlotBoundaries getPlotBoundaries() {
            return plotBoundaries;
        }

        @Nullable
        public Constant getXVariable() {
            return xVariable;
        }

        @Nullable
        public Constant getYVariable() {
            return yVariable;
        }

        public boolean isError() {
            return input == null || expression == null || xVariable == null;
        }
    }

    public static class Input implements Serializable {

        @NotNull
        private String expression;

        @NotNull
        private String xVariableName;

        @Nullable
        private String yVariableName;

        public Input(@NotNull String expression,
                     @NotNull String xVariableName,
                     @Nullable String yVariableName) {
            this.expression = expression;
            this.xVariableName = xVariableName;
            this.yVariableName = yVariableName;
        }

        @NotNull
        public String getExpression() {
            return expression;
        }

        @NotNull
        public String getXVariableName() {
            return xVariableName;
        }

        @Nullable
        public String getYVariableName() {
            return yVariableName;
        }
    }
}
