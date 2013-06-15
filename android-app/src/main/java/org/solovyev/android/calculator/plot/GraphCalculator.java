package org.solovyev.android.calculator.plot;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 1/18/13
 * Time: 8:58 PM
 */
public interface GraphCalculator {

	void computeGraph(@Nonnull XyFunction f,
					  float xMin,
					  float xMax,
					  @Nonnull GraphData graph,
					  @Nonnull GraphsData graphsData,
					  @Nonnull Graph2dDimensions dimensions);
}
