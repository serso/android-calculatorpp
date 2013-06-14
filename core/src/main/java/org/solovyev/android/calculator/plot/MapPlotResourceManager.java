package org.solovyev.android.calculator.plot;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

/**
 * User: serso
 * Date: 1/19/13
 * Time: 12:48 AM
 */
public class MapPlotResourceManager implements PlotResourceManager {

	@NotNull
	private Map<PlotLineDef, List<PlotLineDef>> registeredLineDefsMap = new HashMap<PlotLineDef, List<PlotLineDef>>();

	@NotNull
	private final List<PlotLineDef> preparedLineDefs = new ArrayList<PlotLineDef>(PlotLineStyle.values().length * PlotLineColor.values().length);

	public MapPlotResourceManager() {
		for (PlotLineStyle plotLineStyle : PlotLineStyle.values()) {
			for (PlotLineColor plotLineColor : PlotLineColor.values()) {
				preparedLineDefs.add(PlotLineDef.newInstance(plotLineColor.getColor(), plotLineStyle));
			}
		}
	}

	@NotNull
	@Override
	public PlotLineDef generateAndRegister() {
		synchronized (this) {
			for (PlotLineDef lineDef : preparedLineDefs) {
				final List<PlotLineDef> registeredLineDefs = registeredLineDefsMap.get(lineDef);
				if (registeredLineDefs == null || registeredLineDefs.isEmpty()) {
					register(lineDef);
					return lineDef;
				}
			}

			return preparedLineDefs.get(0);
		}
	}

	private void addLineDef(@NotNull final PlotLineDef toBeAdded) {
		assert Thread.holdsLock(this);

		List<PlotLineDef> registeredLineDefs = registeredLineDefsMap.get(toBeAdded);
		if (registeredLineDefs == null) {
			registeredLineDefs = new ArrayList<PlotLineDef>();
			registeredLineDefsMap.put(toBeAdded, registeredLineDefs);
		}

		try {
			Iterables.find(registeredLineDefs, new Predicate<PlotLineDef>() {
				@Override
				public boolean apply(@Nullable PlotLineDef lineDef) {
					return lineDef == toBeAdded;
				}
			});

			// already added

		} catch (NoSuchElementException e) {
			registeredLineDefs.add(toBeAdded);
		}

	}

	private void removeLineDef(@NotNull final PlotLineDef toBeRemoved) {
		assert Thread.holdsLock(this);

		List<PlotLineDef> registeredLineDefs = registeredLineDefsMap.get(toBeRemoved);

		if (registeredLineDefs != null) {
			Iterables.removeIf(registeredLineDefs, new Predicate<PlotLineDef>() {
				@Override
				public boolean apply(@Nullable PlotLineDef lineDef) {
					return lineDef == toBeRemoved;
				}
			});

			if (registeredLineDefs.isEmpty()) {
				registeredLineDefsMap.remove(toBeRemoved);
			}

		} else {
			registeredLineDefsMap.remove(toBeRemoved);
		}
	}

	@Override
	public void register(@NotNull PlotLineDef lineDef) {
		synchronized (this) {
			addLineDef(lineDef);
		}
	}

	@Override
	public void unregister(@NotNull PlotLineDef lineDef) {
		synchronized (this) {
			removeLineDef(lineDef);
		}
	}

	@Override
	public void unregisterAll() {
		synchronized (this) {
			registeredLineDefsMap.clear();
		}
	}
}
