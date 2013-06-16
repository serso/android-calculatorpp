package org.solovyev.android.calculator.plot;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import jscl.math.Generic;
import jscl.math.function.Constant;
import org.solovyev.android.calculator.Calculator;
import org.solovyev.android.calculator.CalculatorEventType;
import org.solovyev.android.calculator.CalculatorUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: serso
 * Date: 1/12/13
 * Time: 8:42 PM
 */
public class CalculatorPlotterImpl implements CalculatorPlotter {

	@Nonnull
	private final List<PlotFunction> functions = new ArrayList<PlotFunction>();

	@Nonnull
	private final Calculator calculator;

	private final PlotResourceManager resourceManager = new MapPlotResourceManager();

	private boolean plot3d = false;
	private boolean adjustYAxis = true;

	private boolean plotImag = false;

	private int arity = 0;

	@Nonnull
	private PlotBoundaries plotBoundaries = PlotBoundaries.newDefaultInstance();

	@Nonnull
	private PlotData plotData = new PlotData(Collections.<PlotFunction>emptyList(), plot3d, true, plotBoundaries);

	public CalculatorPlotterImpl(@Nonnull Calculator calculator) {
		this.calculator = calculator;
	}

	@Nonnull
	@Override
	public PlotData getPlotData() {
		return plotData;
	}

	@Override
	public boolean addFunction(@Nonnull Generic expression) {
		final List<Constant> variables = new ArrayList<Constant>(CalculatorUtils.getNotSystemConstants(expression));

		assert variables.size() <= 2;

		final Constant xVariable;
		if (variables.size() > 0) {
			xVariable = variables.get(0);
		} else {
			xVariable = null;
		}

		final Constant yVariable;
		if (variables.size() > 1) {
			yVariable = variables.get(1);
		} else {
			yVariable = null;
		}

		final XyFunction realXyFunction = new XyFunction(expression, xVariable, yVariable, false);
		final XyFunction imagXyFunction = new XyFunction(expression, xVariable, yVariable, true);

		// first create plot functions with default line definitions
		PlotFunction realPlotFunction = new PlotFunction(realXyFunction);
		PlotFunction imagPlotFunction = new PlotFunction(imagXyFunction);

		// then remove all unpinned graphs and free their line definitions
		removeAllUnpinnedExcept(realPlotFunction, imagPlotFunction);

		// create plot functions with freed line definitions
		realPlotFunction = newPlotFunction(realXyFunction);
		imagPlotFunction = newPlotFunction(imagXyFunction);

		final boolean realAdded = addFunction(realPlotFunction);
		final boolean imagAdded = addFunction(plotImag ? imagPlotFunction : PlotFunction.invisible(imagPlotFunction));

		return imagAdded || realAdded;
	}

	@Nonnull
	private PlotFunction newPlotFunction(@Nonnull XyFunction xyFunction) {
		return new PlotFunction(xyFunction, resourceManager.generateAndRegister());
	}

	@Override
	public boolean addFunction(@Nonnull PlotFunction plotFunction) {
		synchronized (functions) {
			if (!functions.contains(plotFunction)) {
				resourceManager.register(plotFunction.getPlotLineDef());
				functions.add(plotFunction);
				onFunctionsChanged();
				return true;
			} else {
				return false;
			}
		}
	}

	private boolean removeAllUnpinnedExcept(@Nonnull final PlotFunction... exceptFunctions) {
		synchronized (functions) {

			boolean changed = Iterables.removeIf(functions, new Predicate<PlotFunction>() {
				@Override
				public boolean apply(@Nullable PlotFunction function) {
					if (function != null && !function.isPinned()) {

						for (PlotFunction exceptFunction : exceptFunctions) {
							if (exceptFunction.equals(function)) {
								return false;
							}
						}

						resourceManager.unregister(function.getPlotLineDef());

						return true;
					} else {
						return false;
					}
				}
			});

			if (changed) {
				onFunctionsChanged();
			}

			return changed;
		}
	}


	@Override
	public void removeAllUnpinned() {
		synchronized (functions) {
			boolean changed = Iterables.removeIf(functions, new Predicate<PlotFunction>() {
				@Override
				public boolean apply(@Nullable PlotFunction function) {
					boolean removed = function != null && !function.isPinned();

					if (removed) {
						resourceManager.unregister(function.getPlotLineDef());
					}

					return removed;
				}
			});

			if (changed) {
				onFunctionsChanged();
			}
		}
	}

	@Override
	public boolean removeFunction(@Nonnull PlotFunction plotFunction) {
		synchronized (functions) {
			boolean changed = functions.remove(plotFunction);
			if (changed) {
				resourceManager.unregister(plotFunction.getPlotLineDef());
				onFunctionsChanged();
			}
			return changed;
		}
	}

	@Override
	public boolean addFunction(@Nonnull XyFunction xyFunction) {
		return addFunction(newPlotFunction(xyFunction));
	}

	@Override
	public boolean addFunction(@Nonnull XyFunction xyFunction, @Nonnull PlotLineDef functionLineDef) {
		return addFunction(new PlotFunction(xyFunction, functionLineDef));
	}

	@Override
	public boolean updateFunction(@Nonnull XyFunction xyFunction, @Nonnull PlotLineDef functionLineDef) {
		final PlotFunction newFunction = new PlotFunction(xyFunction, functionLineDef);

		return updateFunction(newFunction);
	}

	@Override
	public boolean updateFunction(@Nonnull PlotFunction newFunction) {
		boolean changed = updateFunction0(newFunction);
		if (changed) {
			firePlotDataChangedEvent();
		}
		return changed;
	}

	public boolean updateFunction0(@Nonnull PlotFunction newFunction) {
		boolean changed = false;

		synchronized (functions) {
			for (int i = 0; i < functions.size(); i++) {
				final PlotFunction oldFunction = functions.get(i);
				if (oldFunction.equals(newFunction)) {

					resourceManager.unregister(oldFunction.getPlotLineDef());
					resourceManager.register(newFunction.getPlotLineDef());

					// update old function
					functions.set(i, newFunction);
					changed = true;
					break;
				}
			}
		}

		return changed;
	}

	@Override
	public boolean removeFunction(@Nonnull XyFunction xyFunction) {
		return removeFunction(new PlotFunction(xyFunction));
	}

	@Nonnull
	@Override
	public PlotFunction pin(@Nonnull PlotFunction plotFunction) {
		final PlotFunction newFunction = PlotFunction.pin(plotFunction);
		updateFunction0(newFunction);
		return newFunction;
	}

	@Nonnull
	@Override
	public PlotFunction unpin(@Nonnull PlotFunction plotFunction) {
		final PlotFunction newFunction = PlotFunction.unpin(plotFunction);
		updateFunction0(newFunction);
		return newFunction;
	}

	@Nonnull
	@Override
	public PlotFunction show(@Nonnull PlotFunction plotFunction) {
		final PlotFunction newFunction = PlotFunction.visible(plotFunction);

		updateFunction(newFunction);

		return newFunction;
	}

	@Nonnull
	@Override
	public PlotFunction hide(@Nonnull PlotFunction plotFunction) {
		final PlotFunction newFunction = PlotFunction.invisible(plotFunction);

		updateFunction(newFunction);

		return newFunction;
	}

	@Override
	public void clearAllFunctions() {
		synchronized (functions) {
			resourceManager.unregisterAll();
			functions.clear();
			onFunctionsChanged();
		}
	}

	@Nullable
	@Override
	public PlotFunction getFunctionById(@Nonnull final String functionId) {
		synchronized (functions) {
			return Iterables.find(functions, new Predicate<PlotFunction>() {
				@Override
				public boolean apply(@Nullable PlotFunction function) {
					return function != null && function.getXyFunction().getId().equals(functionId);
				}
			}, null);
		}
	}

	// NOTE: this method must be called from synchronized block
	private void onFunctionsChanged() {
		assert Thread.holdsLock(functions);

		int maxArity = 0;
		for (PlotFunction function : functions) {
			final XyFunction xyFunction = function.getXyFunction();

			maxArity = Math.max(maxArity, xyFunction.getArity());
		}

		if (maxArity > 1) {
			plot3d = true;
		} else {
			plot3d = false;
		}

		if (functions.isEmpty()) {
			// no functions => new plot => default boundaries
			this.plotBoundaries = PlotBoundaries.newDefaultInstance();
			this.adjustYAxis = true;
		}

		arity = maxArity;

		firePlotDataChangedEvent();
	}

	@Nonnull
	@Override
	public List<PlotFunction> getFunctions() {
		synchronized (functions) {
			return new ArrayList<PlotFunction>(functions);
		}
	}

	@Nonnull
	@Override
	public List<PlotFunction> getVisibleFunctions() {
		synchronized (functions) {
			return Lists.newArrayList(Iterables.filter(functions, new Predicate<PlotFunction>() {
				@Override
				public boolean apply(@Nullable PlotFunction function) {
					return function != null && function.isVisible();
				}
			}));
		}
	}

	@Override
	public void plot() {
		calculator.fireCalculatorEvent(CalculatorEventType.plot_graph, null);
	}

	@Override
	public void plot(@Nonnull Generic expression) {
		addFunction(expression);
		plot();
	}

	@Override
	public boolean is2dPlotPossible() {
		return arity < 2;
	}

	@Override
	public boolean isPlotPossibleFor(@Nonnull Generic expression) {
		boolean result = false;

		int size = CalculatorUtils.getNotSystemConstants(expression).size();
		if (size == 0 || size == 1 || size == 2) {
			result = true;
		}

		return result;
	}

	@Override
	public void setPlot3d(boolean plot3d) {
		if (this.plot3d != plot3d) {
			this.plot3d = plot3d;
			firePlotDataChangedEvent();
		}
	}

	private void firePlotDataChangedEvent() {
		updatePlotData();
		calculator.fireCalculatorEvent(CalculatorEventType.plot_data_changed, plotData);
	}

	private void updatePlotData() {
		plotData = new PlotData(getVisibleFunctions(), plot3d, adjustYAxis, plotBoundaries);
	}

	@Override
	public void setPlotImag(boolean plotImag) {
		if (this.plotImag != plotImag) {
			this.plotImag = plotImag;
			if (toggleImagFunctions(this.plotImag)) {
				firePlotDataChangedEvent();
			}
		}
	}

	@Override
	public void savePlotBoundaries(@Nonnull PlotBoundaries plotBoundaries) {
		if (!this.plotBoundaries.equals(plotBoundaries)) {
			this.plotBoundaries = plotBoundaries;
			this.adjustYAxis = false;
			updatePlotData();
		}
	}

	@Override
	public void setPlotBoundaries(@Nonnull PlotBoundaries plotBoundaries) {
		if (!this.plotBoundaries.equals(plotBoundaries)) {
			this.plotBoundaries = plotBoundaries;
			this.adjustYAxis = false;
			firePlotDataChangedEvent();
		}
	}

	private boolean toggleImagFunctions(boolean show) {
		boolean changed = false;

		synchronized (functions) {
			for (int i = 0; i < functions.size(); i++) {
				final PlotFunction plotFunction = functions.get(i);
				if (plotFunction.getXyFunction().isImag()) {
					functions.set(i, show ? PlotFunction.visible(plotFunction) : PlotFunction.invisible(plotFunction));
					changed = true;
				}
			}
		}

		return changed;
	}
}
