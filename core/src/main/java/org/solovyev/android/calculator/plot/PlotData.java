package org.solovyev.android.calculator.plot;

import java.util.List;

/**
 * User: serso
 * Date: 1/12/13
 * Time: 10:01 PM
 */
public class PlotData {

    private List<PlotFunction> functions;

    private boolean plot3d;

    public PlotData(List<PlotFunction> functions, boolean plot3d) {
        this.functions = functions;
        this.plot3d = plot3d;
    }

    public List<PlotFunction> getFunctions() {
        return functions;
    }

    public boolean isPlot3d() {
        return plot3d;
    }
}
