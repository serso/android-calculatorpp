package org.solovyev.android.calculator.plot;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * User: serso
 * Date: 1/12/13
 * Time: 10:01 PM
 */
public class PlotData {

    @NotNull
    private List<PlotFunction> functions;

    private boolean plot3d;

    @NotNull
    private PlotBoundaries boundaries;

    public PlotData(@NotNull List<PlotFunction> functions,
                    boolean plot3d,
                    @NotNull PlotBoundaries boundaries) {
        this.functions = functions;
        this.plot3d = plot3d;
        this.boundaries = boundaries;
    }

    @NotNull
    public List<PlotFunction> getFunctions() {
        return functions;
    }

    public boolean isPlot3d() {
        return plot3d;
    }

    @NotNull
    public PlotBoundaries getBoundaries() {
        return boundaries;
    }
}
