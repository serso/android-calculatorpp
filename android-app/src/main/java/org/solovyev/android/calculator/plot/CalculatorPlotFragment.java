package org.solovyev.android.calculator.plot;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import jscl.math.Generic;
import jscl.math.function.Constant;
import org.javia.arity.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.R;

import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 12/30/12
 * Time: 4:43 PM
 */
public class CalculatorPlotFragment extends AbstractCalculatorPlotFragment {

    @Nullable
    private GraphView graphView;

    @Nullable
    @Override
    protected PlotBoundaries getPlotBoundaries() {
        if ( graphView != null ) {
            return PlotBoundaries.newInstance(graphView.getXMin(), graphView.getXMax(), graphView.getYMin(), graphView.getYMax());
        } else {
            return null;
        }
    }

    @Override
    protected void createGraphicalView(@NotNull View root, @NotNull PlotData plotData, @NotNull PlotBoundaries plotBoundaries) {

        // remove old
        final ViewGroup graphContainer = (ViewGroup) root.findViewById(R.id.main_fragment_layout);

        if (graphView instanceof View) {
            graphContainer.removeView((View) graphView);
        }

        final List<ArityPlotFunction> arityFunctions = new ArrayList<ArityPlotFunction>();

        for (PlotFunction plotFunction : plotData.getFunctions()) {

            final XyFunction xyFunction = plotFunction.getXyFunction();

            final Generic expression = xyFunction.getExpression();
            final Constant xVariable = xyFunction.getXVariable();
            final Constant yVariable = xyFunction.getYVariable();

            final int arity = xyFunction.getArity();

            final Function arityFunction;
            if (xyFunction.isImag()) {
                arityFunction = new ImaginaryArityFunction(arity, expression, xVariable, yVariable);
            } else {
                arityFunction = new RealArityFunction(arity, expression, xVariable, yVariable);
            }

            arityFunctions.add(ArityPlotFunction.newInstance(arityFunction, plotFunction.getPlotLineDef()));
        }

        if ( plotData.isPlot3d() ) {
            graphView = new Graph3dView(getActivity());
        } else {
            graphView = new CalculatorGraph2dView(getActivity());
        }

        graphView.init(FunctionViewDef.newInstance(Color.WHITE, Color.WHITE, Color.DKGRAY, getBgColor()));
        graphView.setFunctionPlotDefs(arityFunctions);
		graphView.setXRange((float)plotBoundaries.getXMin(), (float)plotBoundaries.getXMax());

        graphContainer.addView((View) graphView);
    }

    @Override
    protected void createChart(@NotNull PlotData plotData, @NotNull PlotBoundaries plotBoundaries) {
    }

	@Override
	protected boolean isScreenshotSupported() {
		return true;
	}

	@NotNull
	@Override
	protected Bitmap getScreehshot() {
		assert this.graphView != null;
		return this.graphView.captureScreenshot();
	}

	@Override
    protected boolean is3dPlotSupported() {
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (this.graphView != null) {
            this.graphView.onResume();
        }
    }

    @Override
    protected void onError() {
        final View root = getView();
        if (root != null && graphView instanceof View) {
            final ViewGroup graphContainer = (ViewGroup) root.findViewById(R.id.main_fragment_layout);
            graphContainer.removeView((View) graphView);
        }
        this.graphView = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.graphView != null) {
            this.graphView.onPause();
        }
    }

    /*
    **********************************************************************
    *
    *                           STATIC
    *
    **********************************************************************
    */

    private static abstract class AbstractArityFunction extends Function {

        protected final int arity;

        @NotNull
        protected final Generic expression;

        @Nullable
        protected final Constant xVariable;

        @Nullable
        protected final Constant yVariable;

        @Nullable
        private Double constant = null;

        public AbstractArityFunction(int arity,
                                     @NotNull Generic expression,
                                     @Nullable  Constant xVariable,
                                     @Nullable Constant yVariable) {
            this.arity = arity;
            this.expression = expression;
            this.xVariable = xVariable;
            this.yVariable = yVariable;
        }

        @Override
        public final double eval() {
            if (constant == null) {
                constant = eval0();
            }
            return constant;
        }

        protected abstract double eval0();

        @Override
        public final int arity() {
            return arity;
        }

    }

    private static class RealArityFunction extends AbstractArityFunction {

        private RealArityFunction(int arity,
                                  @NotNull Generic expression,
                                  @Nullable  Constant xVariable,
                                  @Nullable Constant yVariable) {
            super(arity, expression, xVariable, yVariable);
        }

        @Override
        public double eval0() {
            return PlotUtils.calculatorExpression(expression).realPart();
        }

        @Override
        public double eval(double x) {
            return PlotUtils.calculatorExpression(expression, xVariable, x).realPart();
        }

        @Override
        public double eval(double x, double y) {
            return PlotUtils.calculatorExpression(expression, xVariable, x, yVariable, y).realPart();
        }
    }

    private static class ImaginaryArityFunction extends AbstractArityFunction {

        private ImaginaryArityFunction(int arity,
                             @NotNull Generic expression,
                             @Nullable  Constant xVariable,
                             @Nullable Constant yVariable) {
            super(arity, expression, xVariable, yVariable);
        }

        @Override
        public double eval0() {
            return PlotUtils.calculatorExpression(expression).imaginaryPart();
        }

        @Override
        public double eval(double x) {
            return PlotUtils.calculatorExpression(expression, xVariable, x).imaginaryPart();
        }

        @Override
        public double eval(double x, double y) {
            return PlotUtils.calculatorExpression(expression, xVariable, x, yVariable, y).imaginaryPart();
        }
    }
}
