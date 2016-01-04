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

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

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
