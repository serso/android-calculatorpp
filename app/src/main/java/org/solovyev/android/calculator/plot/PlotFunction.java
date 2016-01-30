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

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 1/12/13
 * Time: 8:45 PM
 */
public class PlotFunction {

    @Nonnull
    private XyFunction xyFunction;

    @Nonnull
    private PlotLineDef plotLineDef;

    private boolean pinned = false;

    private boolean visible = true;

    public PlotFunction(@Nonnull XyFunction xyFunction) {
        this.xyFunction = xyFunction;
        this.plotLineDef = PlotLineDef.newDefaultInstance();
    }

    public PlotFunction(@Nonnull XyFunction xyFunction,
                        @Nonnull PlotLineDef plotLineDef) {
        this.xyFunction = xyFunction;
        this.plotLineDef = plotLineDef;
    }

    @Nonnull
    public static PlotFunction changePlotLineDef(@Nonnull PlotFunction that, @Nonnull PlotLineDef newPlotLineDef) {
        final PlotFunction copy = that.copy();
        copy.plotLineDef = newPlotLineDef;
        return copy;
    }

    @Nonnull
    public static PlotFunction pin(@Nonnull PlotFunction that) {
        return togglePinned(that, true);
    }

    @Nonnull
    public static PlotFunction togglePinned(@Nonnull PlotFunction that, boolean pinned) {
        final PlotFunction copy = that.copy();
        copy.pinned = pinned;
        return copy;
    }

    @Nonnull
    public static PlotFunction unpin(@Nonnull PlotFunction that) {
        return togglePinned(that, false);
    }

    @Nonnull
    public static PlotFunction visible(@Nonnull PlotFunction that) {
        return toggleVisible(that, true);
    }

    @Nonnull
    public static PlotFunction toggleVisible(@Nonnull PlotFunction that, boolean visible) {
        final PlotFunction copy = that.copy();
        copy.visible = visible;
        return copy;
    }

    @Nonnull
    public static PlotFunction invisible(@Nonnull PlotFunction that) {
        return toggleVisible(that, false);
    }

    @Nonnull
    private PlotFunction copy() {
        final PlotFunction copy = new PlotFunction(this.xyFunction, this.plotLineDef);

        copy.pinned = this.pinned;
        copy.visible = this.visible;

        return copy;
    }

    @Nonnull
    public XyFunction getXyFunction() {
        return xyFunction;
    }

    @Nonnull
    public PlotLineDef getPlotLineDef() {
        return plotLineDef;
    }

    public boolean isPinned() {
        return pinned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlotFunction)) return false;

        PlotFunction that = (PlotFunction) o;

        return xyFunction.equals(that.xyFunction);

    }

    @Override
    public int hashCode() {
        return xyFunction.hashCode();
    }

    public boolean isVisible() {
        return visible;
    }
}
