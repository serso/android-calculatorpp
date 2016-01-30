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
 * Date: 1/5/13
 * Time: 7:41 PM
 */
public class PlotLineDef {

	/*
    **********************************************************************
	*
	*                           CONSTANTS
	*
	**********************************************************************
	*/

    @Nonnull
    private static final Float DEFAULT_LINE_WIDTH = 2f;

    private static final int WHITE = 0xFFFFFFFF;


	/*
	**********************************************************************
	*
	*                           FIELDS
	*
	**********************************************************************
	*/

    @Nonnull
    private PlotLineColorType lineColorType = PlotLineColorType.monochrome;

    private int lineColor = WHITE;

    @Nonnull
    private PlotLineStyle lineStyle = PlotLineStyle.solid;

    private float lineWidth = DEFAULT_LINE_WIDTH;

    private PlotLineDef() {
    }

    @Nonnull
    public static PlotLineDef newInstance(int lineColor, @Nonnull PlotLineStyle lineStyle) {
        final PlotLineDef result = new PlotLineDef();
        result.lineColor = lineColor;
        result.lineStyle = lineStyle;
        return result;
    }

    @Nonnull
    public static PlotLineDef newInstance(int lineColor, @Nonnull PlotLineStyle lineStyle, float lineWidth) {
        final PlotLineDef result = new PlotLineDef();
        result.lineColor = lineColor;
        result.lineStyle = lineStyle;
        result.lineWidth = lineWidth;
        return result;
    }

    @Nonnull
    public static PlotLineDef newInstance(int lineColor, @Nonnull PlotLineStyle lineStyle, float lineWidth, @Nonnull PlotLineColorType lineColorType) {
        final PlotLineDef result = new PlotLineDef();
        result.lineColor = lineColor;
        result.lineColorType = lineColorType;
        result.lineStyle = lineStyle;
        result.lineWidth = lineWidth;
        return result;
    }

    @Nonnull
    public static PlotLineDef changeLineColor(@Nonnull PlotLineDef plotLineDef, int newLineColor) {
        final PlotLineDef result = plotLineDef.copy();
        result.lineColor = newLineColor;
        return result;
    }

    @Nonnull
    public static PlotLineDef changeLineWidth(@Nonnull PlotLineDef plotLineDef, int newLineWidth) {
        final PlotLineDef result = plotLineDef.copy();
        result.lineWidth = newLineWidth;
        return result;
    }

    @Nonnull
    public static PlotLineDef changeLineColorType(@Nonnull PlotLineDef plotLineDef, @Nonnull PlotLineColorType newPlotLineColorType) {
        final PlotLineDef result = plotLineDef.copy();
        result.lineColorType = newPlotLineColorType;
        return result;
    }

    @Nonnull
    public static PlotLineDef changeLineStyle(@Nonnull PlotLineDef plotLineDef, @Nonnull PlotLineStyle newPlotLineStyle) {
        final PlotLineDef result = plotLineDef.copy();
        result.lineStyle = newPlotLineStyle;
        return result;
    }

    @Nonnull
    public static PlotLineDef changeColor(@Nonnull PlotLineDef plotLineDef, int newLineColor) {
        final PlotLineDef result = plotLineDef.copy();
        result.lineColor = newLineColor;
        return result;
    }

    @Nonnull
    public static PlotLineDef newDefaultInstance() {
        return new PlotLineDef();
    }

    @Nonnull
    private PlotLineDef copy() {
        final PlotLineDef copy = new PlotLineDef();
        copy.lineColor = lineColor;
        copy.lineColorType = lineColorType;
        copy.lineStyle = lineStyle;
        copy.lineWidth = lineWidth;
        return copy;
    }

    public int getLineColor() {
        return lineColor;
    }

    @Nonnull
    public PlotLineStyle getLineStyle() {
        return lineStyle;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    @Nonnull
    public PlotLineColorType getLineColorType() {
        return lineColorType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlotLineDef)) return false;

        PlotLineDef that = (PlotLineDef) o;

        if (lineColor != that.lineColor) return false;
        if (Float.compare(that.lineWidth, lineWidth) != 0) return false;
        if (lineColorType != that.lineColorType) return false;
        return lineStyle == that.lineStyle;

    }

    @Override
    public int hashCode() {
        int result = lineColorType.hashCode();
        result = 31 * result + lineColor;
        result = 31 * result + lineStyle.hashCode();
        result = 31 * result + (lineWidth != +0.0f ? Float.floatToIntBits(lineWidth) : 0);
        return result;
    }
}
