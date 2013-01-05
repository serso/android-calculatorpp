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
    private List<FunctionPlotDef> functionPlotDefs = Collections.emptyList();

    private GraphViewHelper() {
    }

    @NotNull
    public static GraphViewHelper newDefaultInstance() {
        return new GraphViewHelper();
    }

    @NotNull
    public static GraphViewHelper newInstance(@NotNull FunctionViewDef functionViewDef,
                                              @NotNull List<FunctionPlotDef> functionPlotDefs) {
        final GraphViewHelper result = new GraphViewHelper();

        result.functionViewDef = functionViewDef;
        result.functionPlotDefs = Collections.unmodifiableList(functionPlotDefs);

        return result;
    }

    @NotNull
    public GraphViewHelper copy(@NotNull List<FunctionPlotDef> newFunctionPlotDefs) {
        final GraphViewHelper result = new GraphViewHelper();

        result.functionViewDef = functionViewDef;
        result.functionPlotDefs = Collections.unmodifiableList(newFunctionPlotDefs);

        return result;
    }

    @NotNull
    public List<FunctionPlotDef> getFunctionPlotDefs() {
        return functionPlotDefs;
    }

    @NotNull
    public FunctionViewDef getFunctionViewDef() {
        return functionViewDef;
    }
}
