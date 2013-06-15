package org.solovyev.android.calculator.plot;

import javax.annotation.Nonnull;

import java.util.Collections;
import java.util.List;

/**
 * User: serso
 * Date: 1/5/13
 * Time: 8:06 PM
 */
public class GraphViewHelper {

	@Nonnull
	private PlotViewDef plotViewDef = PlotViewDef.newDefaultInstance();

	@Nonnull
	private List<PlotFunction> plotFunctions = Collections.emptyList();

	private GraphViewHelper() {
	}

	@Nonnull
	public static GraphViewHelper newDefaultInstance() {
		return new GraphViewHelper();
	}

	@Nonnull
	public static GraphViewHelper newInstance(@Nonnull PlotViewDef plotViewDef,
											  @Nonnull List<PlotFunction> plotFunctions) {
		final GraphViewHelper result = new GraphViewHelper();

		result.plotViewDef = plotViewDef;
		result.plotFunctions = Collections.unmodifiableList(plotFunctions);

		return result;
	}

	@Nonnull
	public GraphViewHelper copy(@Nonnull List<PlotFunction> plotFunctions) {
		final GraphViewHelper result = new GraphViewHelper();

		result.plotViewDef = plotViewDef;
		result.plotFunctions = Collections.unmodifiableList(plotFunctions);

		return result;
	}

	@Nonnull
	public List<PlotFunction> getPlotFunctions() {
		return plotFunctions;
	}

	@Nonnull
	public PlotViewDef getPlotViewDef() {
		return plotViewDef;
	}
}
