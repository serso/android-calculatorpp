package org.solovyev.android.calculator.plot;

import org.jetbrains.annotations.NotNull;

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

    @NotNull
    public static final Float DEFAULT_LINE_WIDTH = -1f;

    private static final int WHITE = 0xFFFFFFFF;


    /*
    **********************************************************************
    *
    *                           FIELDS
    *
    **********************************************************************
    */

    @NotNull
    private PlotLineColorType lineColorType = PlotLineColorType.monochrome;

    private int lineColor = WHITE;

    @NotNull
    private PlotLineStyle lineStyle = PlotLineStyle.solid;

    private float lineWidth = -DEFAULT_LINE_WIDTH;

    private PlotLineDef() {
    }

    @NotNull
    public static PlotLineDef newInstance(int lineColor, @NotNull PlotLineStyle lineStyle) {
        final PlotLineDef result = new PlotLineDef();
        result.lineColor = lineColor;
        result.lineStyle = lineStyle;
        return result;
    }

    @NotNull
    public static PlotLineDef newInstance(int lineColor, @NotNull PlotLineStyle lineStyle, float lineWidth) {
        final PlotLineDef result = new PlotLineDef();
        result.lineColor = lineColor;
        result.lineStyle = lineStyle;
        result.lineWidth = lineWidth;
        return result;
    }

    @NotNull
    public static PlotLineDef newInstance(int lineColor, @NotNull PlotLineStyle lineStyle, float lineWidth, @NotNull PlotLineColorType lineColorType) {
        final PlotLineDef result = new PlotLineDef();
        result.lineColor = lineColor;
        result.lineColorType = lineColorType;
        result.lineStyle = lineStyle;
        result.lineWidth = lineWidth;
        return result;
    }

	@NotNull
	private PlotLineDef copy() {
		final PlotLineDef copy = new PlotLineDef();
		copy.lineColor = lineColor;
		copy.lineColorType = lineColorType;
		copy.lineStyle = lineStyle;
		copy.lineWidth = lineWidth;
		return copy;
	}

	@NotNull
	public static PlotLineDef changeLineColor(@NotNull PlotLineDef plotLineDef, int newLineColor) {
		final PlotLineDef result = plotLineDef.copy();
		result.lineColor = newLineColor;
		return result;
	}

	@NotNull
	public static PlotLineDef changeLineColorType(@NotNull PlotLineDef plotLineDef, @NotNull PlotLineColorType newPlotLineColorType) {
		final PlotLineDef result = plotLineDef.copy();
		result.lineColorType = newPlotLineColorType;
		return result;
	}

	@NotNull
	public static PlotLineDef changeLineStyle(@NotNull PlotLineDef plotLineDef, @NotNull PlotLineStyle newPlotLineStyle) {
		final PlotLineDef result = plotLineDef.copy();
		result.lineStyle = newPlotLineStyle;
		return result;
	}

	@NotNull
	public static PlotLineDef changeColor(@NotNull PlotLineDef plotLineDef, int newLineColor) {
		final PlotLineDef result = plotLineDef.copy();
		result.lineColor = newLineColor;
		return result;
	}




	@NotNull
    public static PlotLineDef newDefaultInstance() {
        return new PlotLineDef();
    }

    public int getLineColor() {
        return lineColor;
    }

    @NotNull
    public PlotLineStyle getLineStyle() {
        return lineStyle;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    @NotNull
    public PlotLineColorType getLineColorType() {
        return lineColorType;
    }
}
