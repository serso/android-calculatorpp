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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import jscl.math.Generic;

/**
 * User: serso
 * Date: 1/12/13
 * Time: 8:23 PM
 */
public interface CalculatorPlotter {

    @Nonnull
    PlotData getPlotData();

    boolean addFunction(@Nonnull Generic expression);

    boolean addFunction(@Nonnull PlotFunction plotFunction);

    boolean addFunction(@Nonnull XyFunction xyFunction);

    boolean addFunction(@Nonnull XyFunction xyFunction, @Nonnull PlotLineDef functionLineDef);

    boolean updateFunction(@Nonnull PlotFunction newFunction);

    boolean updateFunction(@Nonnull XyFunction xyFunction, @Nonnull PlotLineDef functionLineDef);

    boolean removeFunction(@Nonnull PlotFunction plotFunction);

    boolean removeFunction(@Nonnull XyFunction xyFunction);

    @Nonnull
    PlotFunction pin(@Nonnull PlotFunction plotFunction);

    @Nonnull
    PlotFunction unpin(@Nonnull PlotFunction plotFunction);

    @Nonnull
    PlotFunction show(@Nonnull PlotFunction plotFunction);

    @Nonnull
    PlotFunction hide(@Nonnull PlotFunction plotFunction);

    void clearAllFunctions();

    @Nullable
    PlotFunction getFunctionById(@Nonnull String functionId);

    @Nonnull
    List<PlotFunction> getFunctions();

    @Nonnull
    List<PlotFunction> getVisibleFunctions();

    void plot();

    void plot(@Nonnull Generic expression);

    boolean is2dPlotPossible();

    boolean isPlotPossibleFor(@Nonnull Generic expression);

    void setPlot3d(boolean plot3d);

    void removeAllUnpinned();

    void setPlotImag(boolean plotImag);

    void savePlotBoundaries(@Nonnull PlotBoundaries plotBoundaries);

    void setPlotBoundaries(@Nonnull PlotBoundaries plotBoundaries);
}
