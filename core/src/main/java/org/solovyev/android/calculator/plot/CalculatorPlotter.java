package org.solovyev.android.calculator.plot;

import jscl.math.Generic;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * User: serso
 * Date: 1/12/13
 * Time: 8:23 PM
 */
public interface CalculatorPlotter {

    @NotNull
    PlotData getPlotData();

    boolean addFunction(@NotNull Generic expression);
    boolean addFunction(@NotNull PlotFunction plotFunction);
    boolean addFunction(@NotNull XyFunction xyFunction);
    boolean addFunction(@NotNull XyFunction xyFunction, @NotNull PlotFunctionLineDef functionLineDef);

    boolean updateFunction(@NotNull PlotFunction newFunction);
    boolean updateFunction(@NotNull XyFunction xyFunction, @NotNull PlotFunctionLineDef functionLineDef);

    boolean removeFunction(@NotNull PlotFunction plotFunction);
    boolean removeFunction(@NotNull XyFunction xyFunction);

    void pin(@NotNull PlotFunction plotFunction);
    void unpin(@NotNull PlotFunction plotFunction);

    void show(@NotNull PlotFunction plotFunction);
    void hide(@NotNull PlotFunction plotFunction);

    void clearAllFunctions();

    @NotNull
    List<PlotFunction> getFunctions();

    @NotNull
    List<PlotFunction> getVisibleFunctions();

    void plot();

    boolean isPlotPossible(@NotNull Generic expression);

    void setPlot3d(boolean plot3d);

    void removeAllUnpinned();

    void setPlotImag(boolean plotImag);

    void setRealLineColor(@NotNull GraphLineColor realLineColor);

    void setImagLineColor(@NotNull GraphLineColor imagLineColor);
}
