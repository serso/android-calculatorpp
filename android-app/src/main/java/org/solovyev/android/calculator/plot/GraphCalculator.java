package org.solovyev.android.calculator.plot;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 1/18/13
 * Time: 8:58 PM
 */
public interface GraphCalculator {

    void computeGraph(@NotNull XyFunction f,
                      float xMin,
                      float xMax,
                      @NotNull GraphData graph,
                      @NotNull GraphsData graphsData,
                      @NotNull Graph2dDimensions dimensions);
}
