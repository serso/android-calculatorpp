package org.solovyev.android.calculator.plot;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * User: serso
 * Date: 1/5/13
 * Time: 8:06 PM
 */
public class GraphViewHelper {

	@NotNull
	private PlotViewDef plotViewDef = PlotViewDef.newDefaultInstance();

	@NotNull
	private List<PlotFunction> plotFunctions = Collections.emptyList();

	private GraphViewHelper() {
	}

	@NotNull
	public static GraphViewHelper newDefaultInstance() {
		return new GraphViewHelper();
	}

	@NotNull
	public static GraphViewHelper newInstance(@NotNull PlotViewDef plotViewDef,
											  @NotNull List<PlotFunction> plotFunctions) {
		final GraphViewHelper result = new GraphViewHelper();

		result.plotViewDef = plotViewDef;
		result.plotFunctions = Collections.unmodifiableList(plotFunctions);

		return result;
	}

	@NotNull
	public GraphViewHelper copy(@NotNull List<PlotFunction> plotFunctions) {
		final GraphViewHelper result = new GraphViewHelper();

		result.plotViewDef = plotViewDef;
		result.plotFunctions = Collections.unmodifiableList(plotFunctions);

		return result;
	}

	@NotNull
	public List<PlotFunction> getPlotFunctions() {
		return plotFunctions;
	}

	@NotNull
	public PlotViewDef getPlotViewDef() {
		return plotViewDef;
	}
}
