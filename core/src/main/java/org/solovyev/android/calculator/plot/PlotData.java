package org.solovyev.android.calculator.plot;

import javax.annotation.Nonnull;

import java.util.List;

/**
 * User: serso
 * Date: 1/12/13
 * Time: 10:01 PM
 */
public class PlotData {

	@Nonnull
	private List<PlotFunction> functions;

	private boolean plot3d;

	@Nonnull
	private PlotBoundaries boundaries;

	public PlotData(@Nonnull List<PlotFunction> functions,
					boolean plot3d,
					@Nonnull PlotBoundaries boundaries) {
		this.functions = functions;
		this.plot3d = plot3d;
		this.boundaries = boundaries;
	}

	@Nonnull
	public List<PlotFunction> getFunctions() {
		return functions;
	}

	public boolean isPlot3d() {
		return plot3d;
	}

	@Nonnull
	public PlotBoundaries getBoundaries() {
		return boundaries;
	}
}
