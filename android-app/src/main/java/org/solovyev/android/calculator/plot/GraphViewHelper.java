package org.solovyev.android.calculator.plot;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * User: serso
 * Date: 1/5/13
 * Time: 8:06 PM
 */
public class GraphViewHelper {

    @NotNull
    private FunctionViewDef functionViewDef = FunctionViewDef.newDefaultInstance();

    @NotNull
    private List<PlotFunction> functionPlotDefs = Collections.emptyList();

    private GraphViewHelper() {
    }

    @NotNull
    public static GraphViewHelper newDefaultInstance() {
        return new GraphViewHelper();
    }

    @NotNull
    public static GraphViewHelper newInstance(@NotNull FunctionViewDef functionViewDef,
                                              @NotNull List<PlotFunction> plotFunctions) {
        final GraphViewHelper result = new GraphViewHelper();

        result.functionViewDef = functionViewDef;
        result.functionPlotDefs = Collections.unmodifiableList(plotFunctions);

        return result;
    }

    @NotNull
    public GraphViewHelper copy(@NotNull List<PlotFunction> plotFunctions) {
        final GraphViewHelper result = new GraphViewHelper();

        result.functionViewDef = functionViewDef;
        result.functionPlotDefs = Collections.unmodifiableList(plotFunctions);

        return result;
    }

    @NotNull
    public List<PlotFunction> getFunctionPlotDefs() {
        return functionPlotDefs;
    }

    @NotNull
    public FunctionViewDef getFunctionViewDef() {
        return functionViewDef;
    }
}
