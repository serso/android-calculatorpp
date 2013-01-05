package org.solovyev.android.calculator.plot;

import android.graphics.Color;
import android.graphics.Paint;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 1/5/13
 * Time: 7:41 PM
 */
public class FunctionLineDef {

    /*
    **********************************************************************
    *
    *                           CONSTANTS
    *
    **********************************************************************
    */

    @NotNull
    private static final Float DEFAULT_LINE_WIDTH = -1f;

    /*
    **********************************************************************
    *
    *                           FIELDS
    *
    **********************************************************************
    */

    private int lineColor = Color.WHITE;

    @NotNull
    private FunctionLineStyle lineStyle = FunctionLineStyle.solid;

    private float lineWidth = -DEFAULT_LINE_WIDTH;

    private FunctionLineDef() {
    }

    @NotNull
    public static FunctionLineDef newInstance(int lineColor, @NotNull FunctionLineStyle lineStyle) {
        final FunctionLineDef result = new FunctionLineDef();
        result.lineColor = lineColor;
        result.lineStyle = lineStyle;
        return result;
    }

    @NotNull
    public static FunctionLineDef newInstance(int lineColor, @NotNull FunctionLineStyle lineStyle, float lineWidth) {
        final FunctionLineDef result = new FunctionLineDef();
        result.lineColor = lineColor;
        result.lineStyle = lineStyle;
        result.lineWidth = lineWidth;
        return result;
    }

    @NotNull
    public static FunctionLineDef newDefaultInstance() {
        return new FunctionLineDef();
    }


    public int getLineColor() {
        return lineColor;
    }

    @NotNull
    public FunctionLineStyle getLineStyle() {
        return lineStyle;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void applyToPaint(@NotNull Paint paint) {
        paint.setColor(lineColor);
        paint.setStyle(Paint.Style.STROKE);

        if ( lineWidth == DEFAULT_LINE_WIDTH ) {
            paint.setStrokeWidth(0);
        } else {
            paint.setStrokeWidth(lineWidth);
        }

        lineStyle.applyToPaint(paint);
    }
}
