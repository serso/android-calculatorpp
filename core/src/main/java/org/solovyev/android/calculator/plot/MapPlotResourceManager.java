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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import javax.annotation.Nonnull;

import javax.annotation.Nullable;

import java.util.*;

/**
 * User: serso
 * Date: 1/19/13
 * Time: 12:48 AM
 */
public class MapPlotResourceManager implements PlotResourceManager {

	@Nonnull
	private Map<PlotLineDef, List<PlotLineDef>> registeredLineDefsMap = new HashMap<PlotLineDef, List<PlotLineDef>>();

	@Nonnull
	private final List<PlotLineDef> preparedLineDefs = new ArrayList<PlotLineDef>(PlotLineStyle.values().length * PlotLineColor.values().length);

	public MapPlotResourceManager() {
		for (PlotLineStyle plotLineStyle : PlotLineStyle.values()) {
			for (PlotLineColor plotLineColor : PlotLineColor.values()) {
				preparedLineDefs.add(PlotLineDef.newInstance(plotLineColor.getColor(), plotLineStyle));
			}
		}
	}

	@Nonnull
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

	private void addLineDef(@Nonnull final PlotLineDef toBeAdded) {
		if (!Thread.holdsLock(this)) throw new AssertionError();

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

	private void removeLineDef(@Nonnull final PlotLineDef toBeRemoved) {
		if (!Thread.holdsLock(this)) throw new AssertionError();

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
	public void register(@Nonnull PlotLineDef lineDef) {
		synchronized (this) {
			addLineDef(lineDef);
		}
	}

	@Override
	public void unregister(@Nonnull PlotLineDef lineDef) {
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
