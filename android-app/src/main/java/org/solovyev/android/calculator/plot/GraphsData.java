package org.solovyev.android.calculator.plot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 1/18/13
 * Time: 8:32 PM
 */
public class GraphsData {

    @NotNull
    private final GraphView graphView;

    @NotNull
    private List<GraphData> graphs;

    private float lastXMin;
    private float lastXMax;

    private float lastYMin;

    private float lastYMax;

    public GraphsData(@NotNull GraphView graphView) {
        this.graphView = graphView;
        graphs = new ArrayList<GraphData>(graphView.getPlotFunctions().size());
    }

    public void clear() {
        for (GraphData graph : graphs) {
            graph.clear();
        }

        while (graphView.getPlotFunctions().size() > graphs.size()) {
            graphs.add(GraphData.newEmptyInstance());
        }

        lastYMin = 0;
        lastYMax = 0;
    }

    @NotNull
    public List<GraphData> getGraphs() {
        return graphs;
    }

    public float getLastXMin() {
        return lastXMin;
    }

    public float getLastXMax() {
        return lastXMax;
    }

    public float getLastYMin() {
        return lastYMin;
    }

    public float getLastYMax() {
        return lastYMax;
    }

    void checkBoundaries(float graphHeight, float yMin, float yMax) {
        if (yMin < lastYMin || yMax > lastYMax) {
            float halfGraphHeight = graphHeight / 2;
            clear();
            lastYMin = yMin - halfGraphHeight;
            lastYMax = yMax + halfGraphHeight;
        }
    }

    public void setLastXMin(float lastXMin) {
        this.lastXMin = lastXMin;
    }

    public void setLastXMax(float lastXMax) {
        this.lastXMax = lastXMax;
    }

    @NotNull
    public GraphData get(int i) {
        return this.graphs.get(i);
    }
}
