package org.solovyev.android.calculator.plot;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import jscl.math.Generic;
import jscl.math.function.Constant;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.Calculator;
import org.solovyev.android.calculator.CalculatorEventType;
import org.solovyev.android.calculator.CalculatorUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 1/12/13
 * Time: 8:42 PM
 */
public class CalculatorPlotterImpl implements CalculatorPlotter {

    @NotNull
    private final List<PlotFunction> functions = new ArrayList<PlotFunction>();

    @NotNull
    private final Calculator calculator;

    private final PlotResourceManager plotResourceManager = new PlotResourceManager();

    private boolean plot3d = false;

    private boolean plotImag = false;

    private int arity = 0;

    @NotNull
    private GraphLineColor realLineColor;

    @NotNull
    private GraphLineColor imagLineColor;

    public CalculatorPlotterImpl(@NotNull Calculator calculator) {
        this.calculator = calculator;
    }

    @NotNull
    @Override
    public PlotData getPlotData() {
        return new PlotData(getVisibleFunctions(), plot3d);
    }

    @Override
    public boolean addFunction(@NotNull Generic expression) {
        final List<Constant> variables = new ArrayList<Constant>(CalculatorUtils.getNotSystemConstants(expression));

        assert variables.size() <= 2;

        final Constant xVariable;
        if (variables.size() > 0) {
            xVariable = variables.get(0);
        } else {
            xVariable = null;
        }

        final Constant yVariable;
        if (variables.size() > 1) {
            yVariable = variables.get(1);
        } else {
            yVariable = null;
        }

        boolean realAdded = addFunction(new XyFunction(expression, xVariable, yVariable, false));

        final PlotFunction imagPlotFunction = newPlotFunction(new XyFunction(expression, xVariable, yVariable, true));
        final boolean imagAdded = addFunction(plotImag ? imagPlotFunction : PlotFunction.invisible(imagPlotFunction));

        return imagAdded || realAdded;
    }

    @NotNull
    private PlotFunction newPlotFunction(@NotNull XyFunction xyFunction) {
        return new PlotFunction(xyFunction);
    }

    @Override
    public boolean addFunction(@NotNull PlotFunction plotFunction) {
        synchronized (functions) {
            if (!functions.contains(plotFunction)) {
                functions.add(plotFunction);
                onFunctionsChanged();
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void removeAllUnpinned() {
        synchronized (functions) {
            boolean changed = Iterables.removeIf(functions, new Predicate<PlotFunction>() {
                @Override
                public boolean apply(@Nullable PlotFunction function) {
                    return function != null && !function.isPinned();
                }
            });

            if (changed) {
                onFunctionsChanged();
            }
        }
    }

    @Override
    public boolean removeFunction(@NotNull PlotFunction plotFunction) {
        synchronized (functions) {
            boolean changed = functions.remove(plotFunction);
            if (changed) {
                onFunctionsChanged();
            }
            return changed;
        }
    }

    @Override
    public boolean addFunction(@NotNull XyFunction xyFunction) {
        return addFunction(newPlotFunction(xyFunction));
    }

    @Override
    public boolean addFunction(@NotNull XyFunction xyFunction, @NotNull PlotFunctionLineDef functionLineDef) {
        return addFunction(new PlotFunction(xyFunction, functionLineDef));
    }

    @Override
    public boolean updateFunction(@NotNull XyFunction xyFunction, @NotNull PlotFunctionLineDef functionLineDef) {
        final PlotFunction newFunction = new PlotFunction(xyFunction, functionLineDef);

        return updateFunction(newFunction);
    }

    @Override
    public boolean updateFunction(@NotNull PlotFunction newFunction) {
        boolean changed = updateFunction0(newFunction);
        if (changed) {
            firePlotDataChangedEvent();
        }
        return changed;
    }

    public boolean updateFunction0(@NotNull PlotFunction newFunction) {
        boolean changed = false;

        synchronized (functions) {
            for (int i = 0; i < functions.size(); i++) {
                final PlotFunction plotFunction = functions.get(i);
                if (plotFunction.equals(newFunction)) {
                    // update old function
                    functions.set(i, newFunction);
                    changed = true;
                    break;
                }
            }
        }

        return changed;
    }

    @Override
    public boolean removeFunction(@NotNull XyFunction xyFunction) {
        return removeFunction(new PlotFunction(xyFunction));
    }

    @NotNull
    @Override
    public PlotFunction pin(@NotNull PlotFunction plotFunction) {
        final PlotFunction newFunction = PlotFunction.pin(plotFunction);
        updateFunction0(newFunction);
        return newFunction;
    }

    @NotNull
    @Override
    public PlotFunction unpin(@NotNull PlotFunction plotFunction) {
        final PlotFunction newFunction = PlotFunction.unpin(plotFunction);
        updateFunction0(newFunction);
        return newFunction;
    }

    @NotNull
    @Override
    public PlotFunction show(@NotNull PlotFunction plotFunction) {
        final PlotFunction newFunction = PlotFunction.visible(plotFunction);

        updateFunction(newFunction);

        return newFunction;
    }

    @NotNull
    @Override
    public PlotFunction hide(@NotNull PlotFunction plotFunction) {
        final PlotFunction newFunction = PlotFunction.invisible(plotFunction);

        updateFunction(newFunction);

        return newFunction;
    }

    @Override
    public void clearAllFunctions() {
        synchronized (functions) {
            functions.clear();
            onFunctionsChanged();
        }
    }

    // NOTE: this method must be called from synchronized block
    private void onFunctionsChanged() {
        assert Thread.holdsLock(functions);

        int maxArity = 0;
        for (PlotFunction function : functions) {
            final XyFunction xyFunction = function.getXyFunction();

            maxArity = Math.max(maxArity, xyFunction.getArity());
        }

        if (maxArity > 1) {
            plot3d = true;
        } else {
            plot3d = false;
        }

        arity = maxArity;

        firePlotDataChangedEvent();
    }

    @NotNull
    @Override
    public List<PlotFunction> getFunctions() {
        synchronized (functions) {
            return new ArrayList<PlotFunction>(functions);
        }
    }

    @NotNull
    @Override
    public List<PlotFunction> getVisibleFunctions() {
        synchronized (functions) {
            return Lists.newArrayList(Iterables.filter(functions, new Predicate<PlotFunction>() {
                @Override
                public boolean apply(@Nullable PlotFunction function) {
                    return function != null && function.isVisible();
                }
            }));
        }
    }

    @Override
    public void plot() {
        calculator.fireCalculatorEvent(CalculatorEventType.plot_graph, null);
    }

    @Override
    public void plot(@NotNull Generic expression) {
        removeAllUnpinned();
        addFunction(expression);
        plot();
    }

    @Override
    public boolean is2dPlotPossible() {
        return arity < 2;
    }

    @Override
    public boolean isPlotPossibleFor(@NotNull Generic expression) {
        boolean result = false;

        int size = CalculatorUtils.getNotSystemConstants(expression).size();
        if (size == 0 || size == 1 || size == 2) {
            result = true;
        }

        return result;
    }

    @Override
    public void setPlot3d(boolean plot3d) {
        if (this.plot3d != plot3d) {
            this.plot3d = plot3d;
            firePlotDataChangedEvent();
        }
    }

    private void firePlotDataChangedEvent() {
        calculator.fireCalculatorEvent(CalculatorEventType.plot_data_changed, getPlotData());
    }

    @Override
    public void setPlotImag(boolean plotImag) {
        if (this.plotImag != plotImag) {
            this.plotImag = plotImag;
            if (toggleImagFunctions(this.plotImag)) {
                firePlotDataChangedEvent();
            }
        }
    }

    @Override
    public void setRealLineColor(@NotNull GraphLineColor realLineColor) {
        this.realLineColor = realLineColor;
    }

    @Override
    public void setImagLineColor(@NotNull GraphLineColor imagLineColor) {
        this.imagLineColor = imagLineColor;
    }

    private boolean toggleImagFunctions(boolean show) {
        boolean changed = false;

        synchronized (functions) {
            for (int i = 0; i < functions.size(); i++) {
                final PlotFunction plotFunction = functions.get(i);
                if (plotFunction.getXyFunction().isImag()) {
                    functions.set(i, show ? PlotFunction.visible(plotFunction) : PlotFunction.invisible(plotFunction));
                    changed = true;
                }
            }
        }

        return changed;
    }
}
