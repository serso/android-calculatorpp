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

/**
 * User: serso
 * Date: 1/12/13
 * Time: 10:01 PM
 */
public class PlotData {

    @Nonnull
    private final List<PlotFunction> functions;

    private final boolean plot3d;

    private final boolean adjustYAxis;

    @Nonnull
    private final PlotBoundaries boundaries;

    public PlotData(@Nonnull List<PlotFunction> functions,
                    boolean plot3d,
                    boolean adjustYAxis,
                    @Nonnull PlotBoundaries boundaries) {
        this.functions = functions;
        this.plot3d = plot3d;
        this.adjustYAxis = adjustYAxis;
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

    public boolean isAdjustYAxis() {
        return adjustYAxis;
    }
}
