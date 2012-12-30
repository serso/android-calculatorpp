package org.solovyev.android.calculator.plot;

import android.view.View;
import android.view.ViewGroup;
import arity.calculator.Graph2dView;
import arity.calculator.Graph3dView;
import arity.calculator.GraphView;
import jscl.math.Generic;
import jscl.math.function.Constant;
import org.javia.arity.Complex;
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
        // remove old
        final ViewGroup graphContainer = (ViewGroup) root.findViewById(R.id.main_fragment_layout);

        if (graphView instanceof View) {
            graphContainer.removeView((View) graphView);
        }

        if (!preparedInput.isError()) {
            final Generic expression = preparedInput.getExpression();
            final Constant xVariable = preparedInput.getXVariable();
            final Constant yVariable = preparedInput.getYVariable();

            final int arity = yVariable == null ? 1 : 2;

            final List<Function> functions = new ArrayList<Function>();
            functions.add(new Function() {
                @Override
                public int arity() {
                    return arity;
                }

                @Override
                public double eval(double x) {
                    return PlotUtils.calculatorExpression(expression, xVariable, x).realPart();
                }

                @Override
                public double eval(double x, double y) {
                    return PlotUtils.calculatorExpression(expression, xVariable, x, yVariable, y).realPart();
                }

                @Override
                public Complex eval(Complex x) {
                    jscl.math.numeric.Complex result = PlotUtils.calculatorExpression(expression, xVariable, x.re);
                    return new Complex(result.realPart(), result.imaginaryPart());
                }

                @Override
                public Complex eval(Complex x, Complex y) {
                    jscl.math.numeric.Complex result = PlotUtils.calculatorExpression(expression, xVariable, x.re, yVariable, y.re);
                    return new Complex(result.realPart(), result.imaginaryPart());
                }
            });

            if (functions.size() == 1) {
                final Function f = functions.get(0);
                graphView = f.arity() == 1 ? new Graph2dView(getActivity()) : new Graph3dView(getActivity());
                graphView.setFunction(f);
            } else {
                graphView = new Graph2dView(this.getActivity());
                ((Graph2dView) graphView).setFunctions(functions);
            }

            graphContainer.addView((View) graphView);
        } else {
            onError();
        }
    }

    @Override
    protected void createChart(@NotNull PreparedInput preparedInput) {
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
}
