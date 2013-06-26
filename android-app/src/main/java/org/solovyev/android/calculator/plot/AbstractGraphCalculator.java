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

/**
 * User: serso
 * Date: 1/18/13
 * Time: 9:03 PM
 */
public abstract class AbstractGraphCalculator implements GraphCalculator {

	@Nonnull
	protected final GraphData next = GraphData.newEmptyInstance();

	@Nonnull
	private final GraphData endGraph = GraphData.newEmptyInstance();

	@Nonnull
	private final GraphData startGraph = GraphData.newEmptyInstance();

	@Override
	public final void computeGraph(@Nonnull XyFunction f,
								   float xMin,
								   float xMax,
								   @Nonnull GraphData graph,
								   @Nonnull GraphsData graphsData,
								   @Nonnull Graph2dDimensions dimensions) {
		if (f.getArity() == 0) {
			final float v = (float) f.eval();
			graph.clear();
			graph.push(xMin, v);
			graph.push(xMax, v);
			return;
		}

		float yMin = graphsData.getLastYMin();
		float yMax = graphsData.getLastYMax();

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
				if (xMax <= graphsData.getLastXMax()) {
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

				if (xMax <= graphsData.getLastXMax()) {
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

	protected abstract void compute(@Nonnull XyFunction f,
									float xMin,
									float xMax,
									float yMin,
									float yMax,
									@Nonnull GraphData graph,
									@Nonnull Graph2dDimensions dimensions);
}
