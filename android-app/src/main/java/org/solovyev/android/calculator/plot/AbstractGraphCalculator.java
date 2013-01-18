package org.solovyev.android.calculator.plot;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 1/18/13
 * Time: 9:03 PM
 */
public abstract class AbstractGraphCalculator implements GraphCalculator {

    @NotNull
    protected final GraphData next = GraphData.newEmptyInstance();

    @NotNull
    private final GraphData endGraph = GraphData.newEmptyInstance();

    @NotNull
    private final GraphData startGraph = GraphData.newEmptyInstance();

    @Override
    public final void computeGraph(@NotNull XyFunction f,
                                   float xMin,
                                   float xMax,
                                   @NotNull GraphData graph,
                                   @NotNull GraphsData graphsData,
                                   @NotNull Graph2dDimensions dimensions) {
        if (f.getArity() == 0) {
            final float v = (float) f.eval();
            graph.clear();
            graph.push(xMin, v);
            graph.push(xMax, v);
            return;
        }

        float yMin = graphsData.getLastYMin();
        float yMax = graphsData.getLastYMin();

        // prepare graph
        if (!graph.empty()) {
            if (xMin >= graphsData.getLastXMin()) {
                // |------[---erased---|------data----|---erased--]------ old data
                // |-------------------[------data----]------------------ new data
                //                    xMin           xMax
                //
                // OR
                //
                // |------[---erased---|------data----]----------- old data
                // |-------------------[------data----<---->]----- new data
                //                    xMin                 xMax
                graph.eraseBefore(xMin);
                if ( xMax <= graphsData.getLastXMax() ) {
                    graph.eraseAfter(xMax);
                    // nothing to compute
                } else {
                    xMin = graph.getLastX();
                    compute(f, xMin, xMax, yMin, yMax, endGraph, dimensions);
                }
            } else {
                // |--------------------[-----data----|---erased----]-- old data
                // |------[<------------>-----data----]---------------- new data
                //       xMin                        xMax
                //
                // OR
                //
                // |--------------------[------data--]----|----------- old data
                // |-------[<----------->------data--<--->]-----------new data
                //        xMin                           xMax

                if ( xMax <= graphsData.getLastXMax() ) {
                    graph.eraseAfter(xMax);
                    xMax = graph.getFirstX();
                    compute(f, xMin, xMax, yMin, yMax, startGraph, dimensions);
                } else {
                    compute(f, xMin, graph.getFirstX(), yMin, yMax, startGraph, dimensions);
                    compute(f, graph.getLastX(), xMax, yMin, yMax, endGraph, dimensions);
                }
            }
        } else {
            compute(f, xMin, xMax, yMin, yMax, graph, dimensions);
        }

        if (!endGraph.empty()) {
            // first add ending because it's fast
            graph.append(endGraph);
        }

        if (!startGraph.empty()) {
            startGraph.append(graph);
            graph.swap(startGraph);
        }


        next.clear();
        endGraph.clear();
        startGraph.clear();
    }

    protected abstract void compute(@NotNull XyFunction f,
                                    float xMin,
                                    float xMax,
                                    float yMin,
                                    float yMax,
                                    @NotNull GraphData graph,
                                    @NotNull Graph2dDimensions dimensions);
}
