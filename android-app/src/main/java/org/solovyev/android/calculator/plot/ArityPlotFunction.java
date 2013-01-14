package org.solovyev.android.calculator.plot;

import org.javia.arity.Function;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 1/5/13
 * Time: 7:35 PM
 */
public class ArityPlotFunction {

    @NotNull
    private Function function;

    @NotNull
    private PlotLineDef lineDef;

    private ArityPlotFunction() {
    }

    @NotNull
    public static ArityPlotFunction newInstance(@NotNull Function function) {
        return newInstance(function, PlotLineDef.newDefaultInstance());
    }

    @NotNull
    public static ArityPlotFunction newInstance(@NotNull Function function, @NotNull PlotLineDef lineDef) {
        final ArityPlotFunction result = new ArityPlotFunction();

        result.function = function;
        result.lineDef = lineDef;

        return result;
    }

    @NotNull
    public Function getFunction() {
        return function;
    }

    @NotNull
    public PlotLineDef getLineDef() {
        return lineDef;
    }
}
