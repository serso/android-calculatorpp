package org.solovyev.android.calculator.plot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: serso
 * Date: 1/19/13
 * Time: 12:48 AM
 */
public class MapPlotResourceManager implements PlotResourceManager {

    @NotNull
    private Map<PlotLineDef, Integer> counters = new HashMap<PlotLineDef, Integer>();

    @NotNull
    private final List<PlotLineDef> preparedLineDefs = new ArrayList<PlotLineDef>(PlotLineStyle.values().length * PlotLineColor.values().length);

    public MapPlotResourceManager() {
        for (PlotLineStyle plotLineStyle : PlotLineStyle.values()) {
            for (PlotLineColor plotLineColor : PlotLineColor.values()) {
                 preparedLineDefs.add(PlotLineDef.newInstance(plotLineColor.getColor(), plotLineStyle));
            }
        }
    }

    @NotNull
    @Override
    public PlotLineDef generateAndRegister() {
        synchronized (this) {
            for (PlotLineDef lineDef : preparedLineDefs) {
                final Integer counter = counters.get(lineDef);
                if ( counter == null || counter.equals(0) ) {
                    register0(lineDef);
                    return lineDef;
                }
            }

            return preparedLineDefs.get(0);
        }
    }

    private void increaseCounter(@NotNull PlotLineDef lineDef) {
        assert Thread.holdsLock(this);

        Integer counter = counters.get(lineDef);
        if ( counter == null ) {
            counter = 0;
        }
        counter++;
        counters.put(lineDef, counter);
    }

    private void decreaseCounter(@NotNull PlotLineDef lineDef) {
        assert Thread.holdsLock(this);

        Integer counter = counters.get(lineDef);
        if (counter != null) {
            counter--;
            counters.put(lineDef, Math.max(counter, 0));
        }
    }

    @Override
    public void register(@NotNull PlotLineDef lineDef) {
        synchronized (this) {
            // we should check if specified line def is not ours, i.e. created by this class
            for (PlotLineDef preparedLineDef : preparedLineDefs) {
                if ( preparedLineDef == lineDef ) {
                    return;
                }
            }

            register0(lineDef);
        }
    }

    private void register0(@NotNull PlotLineDef lineDef) {
        increaseCounter(lineDef);
    }

    @Override
    public void unregister(@NotNull PlotLineDef lineDef) {
        synchronized (this) {
            decreaseCounter(lineDef);
        }
    }

    @Override
    public void unregisterAll() {
        synchronized (this) {
            counters.clear();
        }
    }
}
