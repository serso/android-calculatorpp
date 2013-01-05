package org.solovyev.android.calculator.plot;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import jscl.math.Generic;
import jscl.math.function.Constant;
import org.javia.arity.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorPreferences;
import org.solovyev.android.calculator.R;

import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 12/30/12
 * Time: 4:43 PM
 */
public class CalculatorArityPlotFragment extends AbstractCalculatorPlotFragment {

    @Nullable
    private GraphView graphView;

    @Nullable
    @Override
    protected PlotBoundaries getPlotBoundaries() {
        if ( graphView != null ) {
            // todo serso: return plot boundaries
            return null;
        } else {
            return null;
        }
    }

    @Override
    protected void createGraphicalView(@NotNull View root, @NotNull PreparedInput preparedInput) {

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        final GraphLineColor realLineColor = CalculatorPreferences.Graph.lineColorReal.getPreference(preferences);
        final GraphLineColor imagLineColor = CalculatorPreferences.Graph.lineColorImag.getPreference(preferences);

        // remove old
        final ViewGroup graphContainer = (ViewGroup) root.findViewById(R.id.main_fragment_layout);

        if (graphView instanceof View) {
            graphContainer.removeView((View) graphView);
        }

        if (!preparedInput.isError()) {
            final Generic expression = preparedInput.getExpression();
            final Constant xVariable = preparedInput.getXVariable();
            final Constant yVariable = preparedInput.getYVariable();

            final int arity = xVariable == null ? 0 : (yVariable == null ? 1 : 2);

            final List<FunctionPlotDef> functions = new ArrayList<FunctionPlotDef>();

            functions.add(FunctionPlotDef.newInstance(new RealArityFunction(arity, expression, xVariable, yVariable), FunctionLineDef.newInstance(realLineColor.getColor(), FunctionLineStyle.solid, 3f)));
            functions.add(FunctionPlotDef.newInstance(new ImaginaryArityFunction(arity, expression, xVariable, yVariable), FunctionLineDef.newInstance(imagLineColor.getColor(), FunctionLineStyle.solid, 3f)));

            switch (arity) {
                case 0:
                case 1:
                    if (preparedInput.isForce3d()) {
                        graphView = new Graph3dView(getActivity());
                    } else {
                        graphView = new Graph2dView(getActivity());
                    }
                    break;
                case 2:
                    graphView = new Graph3dView(getActivity());
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported arity: " + arity);
            }

            graphView.init(FunctionViewDef.newInstance(Color.WHITE, Color.WHITE, Color.DKGRAY, getBgColor()));
            graphView.setFunctionPlotDefs(functions);

            graphContainer.addView((View) graphView);
        } else {
            onError();
        }
    }

    @Override
    protected void createChart(@NotNull PreparedInput preparedInput) {
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
