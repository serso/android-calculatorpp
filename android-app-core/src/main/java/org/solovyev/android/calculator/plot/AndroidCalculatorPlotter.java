package org.solovyev.android.calculator.plot;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import jscl.math.Generic;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.android.calculator.CalculatorPreferences;

import java.util.List;

/**
 * User: serso
 * Date: 1/12/13
 * Time: 11:03 PM
 */
public class AndroidCalculatorPlotter implements CalculatorPlotter, SharedPreferences.OnSharedPreferenceChangeListener {

	@Nonnull
	private final CalculatorPlotter plotter;

	public AndroidCalculatorPlotter(@Nonnull Context context,
									@Nonnull CalculatorPlotter plotter) {
		this.plotter = plotter;

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		preferences.registerOnSharedPreferenceChangeListener(this);

		onSharedPreferenceChanged(preferences, CalculatorPreferences.Graph.plotImag.getKey());
	}

	@Override
	@Nonnull
	public PlotData getPlotData() {
		return plotter.getPlotData();
	}

	@Override
	public boolean addFunction(@Nonnull Generic expression) {
		return plotter.addFunction(expression);
	}

	@Override
	public boolean addFunction(@Nonnull PlotFunction plotFunction) {
		return plotter.addFunction(plotFunction);
	}

	@Override
	public boolean addFunction(@Nonnull XyFunction xyFunction) {
		return plotter.addFunction(xyFunction);
	}

	@Override
	public boolean addFunction(@Nonnull XyFunction xyFunction, @Nonnull PlotLineDef functionLineDef) {
		return plotter.addFunction(xyFunction, functionLineDef);
	}

	@Override
	public boolean updateFunction(@Nonnull PlotFunction newFunction) {
		return plotter.updateFunction(newFunction);
	}

	@Override
	public boolean updateFunction(@Nonnull XyFunction xyFunction, @Nonnull PlotLineDef functionLineDef) {
		return plotter.updateFunction(xyFunction, functionLineDef);
	}

	@Override
	public boolean removeFunction(@Nonnull PlotFunction plotFunction) {
		return plotter.removeFunction(plotFunction);
	}

	@Override
	public boolean removeFunction(@Nonnull XyFunction xyFunction) {
		return plotter.removeFunction(xyFunction);
	}

	@Nonnull
	@Override
	public PlotFunction pin(@Nonnull PlotFunction plotFunction) {
		return plotter.pin(plotFunction);
	}

	@Nonnull
	@Override
	public PlotFunction unpin(@Nonnull PlotFunction plotFunction) {
		return plotter.unpin(plotFunction);
	}

	@Nonnull
	@Override
	public PlotFunction show(@Nonnull PlotFunction plotFunction) {
		return plotter.show(plotFunction);
	}

	@Nonnull
	@Override
	public PlotFunction hide(@Nonnull PlotFunction plotFunction) {
		return plotter.hide(plotFunction);
	}

	@Override
	public void clearAllFunctions() {
		plotter.clearAllFunctions();
	}

	@Nullable
	@Override
	public PlotFunction getFunctionById(@Nonnull String functionId) {
		return plotter.getFunctionById(functionId);
	}

	@Override
	@Nonnull
	public List<PlotFunction> getFunctions() {
		return plotter.getFunctions();
	}

	@Override
	@Nonnull
	public List<PlotFunction> getVisibleFunctions() {
		return plotter.getVisibleFunctions();
	}

	@Override
	public void plot() {
		plotter.plot();
	}

	@Override
	public void plot(@Nonnull Generic expression) {
		plotter.plot(expression);
	}

	@Override
	public boolean is2dPlotPossible() {
		return plotter.is2dPlotPossible();
	}

	@Override
	public boolean isPlotPossibleFor(@Nonnull Generic expression) {
		return plotter.isPlotPossibleFor(expression);
	}

	@Override
	public void setPlot3d(boolean plot3d) {
		plotter.setPlot3d(plot3d);
	}

	@Override
	public void removeAllUnpinned() {
		plotter.removeAllUnpinned();
	}

	@Override
	public void setPlotImag(boolean plotImag) {
		plotter.setPlotImag(plotImag);
	}

	@Override
	public void savePlotBoundaries(@Nonnull PlotBoundaries plotBoundaries) {
		plotter.savePlotBoundaries(plotBoundaries);
	}

	@Override
	public void setPlotBoundaries(@Nonnull PlotBoundaries plotBoundaries) {
		plotter.setPlotBoundaries(plotBoundaries);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
		if (CalculatorPreferences.Graph.plotImag.getKey().equals(key)) {
			setPlotImag(CalculatorPreferences.Graph.plotImag.getPreference(preferences));
		}
	}
}
