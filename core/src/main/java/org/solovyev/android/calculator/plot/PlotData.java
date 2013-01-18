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

    public PlotData(@NotNull List<PlotFunction> functions, boolean plot3d) {
        this.functions = functions;
        this.plot3d = plot3d;
    }

    @NotNull
    public List<PlotFunction> getFunctions() {
        return functions;
    }

    public boolean isPlot3d() {
        return plot3d;
    }
}
