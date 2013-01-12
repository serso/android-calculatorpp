package org.solovyev.android.calculator.plot;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 1/12/13
 * Time: 8:45 PM
 */
public class PlotFunction {

    @NotNull
    private XyFunction xyFunction;

    @NotNull
    private PlotFunctionLineDef plotFunctionLineDef;

    private boolean pinned = false;

    private boolean visible = true;

    public PlotFunction(@NotNull XyFunction xyFunction) {
        this.xyFunction = xyFunction;
        this.plotFunctionLineDef = PlotFunctionLineDef.newDefaultInstance();
    }

    public PlotFunction(@NotNull XyFunction xyFunction,
                        @NotNull PlotFunctionLineDef plotFunctionLineDef) {
        this.xyFunction = xyFunction;
        this.plotFunctionLineDef = plotFunctionLineDef;
    }

    @NotNull
    private PlotFunction copy() {
        final PlotFunction copy = new PlotFunction(this.xyFunction);

        copy.plotFunctionLineDef = this.plotFunctionLineDef;
        copy.pinned = this.pinned;
        copy.visible = this.visible;

        return copy;
    }

    public static PlotFunction pin(@NotNull PlotFunction that) {
        final PlotFunction copy = that.copy();
        copy.pinned = true;
        return copy;
    }

    public static PlotFunction unpin(@NotNull PlotFunction that) {
        final PlotFunction copy = that.copy();
        copy.pinned = false;
        return copy;
    }

    public static PlotFunction visible(@NotNull PlotFunction that) {
        final PlotFunction copy = that.copy();
        copy.visible = true;
        return copy;
    }

    public static PlotFunction invisible(@NotNull PlotFunction that) {
        final PlotFunction copy = that.copy();
        copy.visible = false;
        return copy;
    }

    @NotNull
    public XyFunction getXyFunction() {
        return xyFunction;
    }

    @NotNull
    public PlotFunctionLineDef getPlotFunctionLineDef() {
        return plotFunctionLineDef;
    }

    public boolean isPinned() {
        return pinned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlotFunction)) return false;

        PlotFunction that = (PlotFunction) o;

        if (!xyFunction.equals(that.xyFunction)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return xyFunction.hashCode();
    }

    public boolean isVisible() {
        return visible;
    }
}
