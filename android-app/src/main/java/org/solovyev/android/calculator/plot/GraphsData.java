/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator.plot;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 1/18/13
 * Time: 8:32 PM
 */
public class GraphsData {

	@Nonnull
	private final GraphView graphView;

	@Nonnull
	private List<GraphData> graphs;

	private float lastXMin;
	private float lastXMax;

	private float lastYMin;

	private float lastYMax;

	public GraphsData(@Nonnull GraphView graphView) {
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

	@Nonnull
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

	@Nonnull
	public GraphData get(int i) {
		return this.graphs.get(i);
	}
}
