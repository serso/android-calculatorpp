package org.solovyev.android.calculator.plot;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 1/5/13
 * Time: 7:41 PM
 */
public class PlotFunctionLineDef {

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
    private PlotFunctionLineColorType lineColorType = PlotFunctionLineColorType.solid;

    private int lineColor = WHITE;

    @NotNull
    private PlotLineStyle lineStyle = PlotLineStyle.solid;

    private float lineWidth = -DEFAULT_LINE_WIDTH;

    private PlotFunctionLineDef() {
    }

    @NotNull
    public static PlotFunctionLineDef newInstance(int lineColor, @NotNull PlotLineStyle lineStyle) {
        final PlotFunctionLineDef result = new PlotFunctionLineDef();
        result.lineColor = lineColor;
        result.lineStyle = lineStyle;
        return result;
    }

    @NotNull
    public static PlotFunctionLineDef newInstance(int lineColor, @NotNull PlotLineStyle lineStyle, float lineWidth) {
        final PlotFunctionLineDef result = new PlotFunctionLineDef();
        result.lineColor = lineColor;
        result.lineStyle = lineStyle;
        result.lineWidth = lineWidth;
        return result;
    }

    @NotNull
    public static PlotFunctionLineDef newInstance(int lineColor, @NotNull PlotLineStyle lineStyle, float lineWidth, @NotNull PlotFunctionLineColorType lineColorType) {
        final PlotFunctionLineDef result = new PlotFunctionLineDef();
        result.lineColor = lineColor;
        result.lineColorType = lineColorType;
        result.lineStyle = lineStyle;
        result.lineWidth = lineWidth;
        return result;
    }

    @NotNull
    public static PlotFunctionLineDef newDefaultInstance() {
        return new PlotFunctionLineDef();
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
    public PlotFunctionLineColorType getLineColorType() {
        return lineColorType;
    }
}
