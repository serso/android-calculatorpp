package org.solovyev.android.calculator.plot;

import android.graphics.Color;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 1/5/13
 * Time: 9:11 PM
 */
public class FunctionViewDef {

    /*
    **********************************************************************
    *
    *                           CONSTANTS
    *
    **********************************************************************
    */

    private static final int DEFAULT_AXIS_COLOR = 0xff00a000;
    private static final int DEFAULT_GRID_COLOR = 0xff004000;
    private static final int DEFAULT_BACKGROUND_COLOR = Color.BLACK;

    /*
    **********************************************************************
    *
    *                           FIELDS
    *
    **********************************************************************
    */

    private int axisColor = DEFAULT_AXIS_COLOR;

    private int axisLabelsColor = DEFAULT_AXIS_COLOR;

    private int gridColor = DEFAULT_GRID_COLOR;

    private int backgroundColor = DEFAULT_BACKGROUND_COLOR;

    private FunctionViewDef() {
    }

    private FunctionViewDef(int axisColor, int axisLabelColor, int gridColor, int backgroundColor) {
        this.axisColor = axisColor;
        this.axisLabelsColor = axisLabelColor;
        this.gridColor = gridColor;
        this.backgroundColor = backgroundColor;
    }

    @NotNull
    public static FunctionViewDef newDefaultInstance() {
        return new FunctionViewDef();
    }

    @NotNull
    public static FunctionViewDef newInstance(int axisColor, int axisLabelColor, int gridColor, int backgroundColor) {
        return new FunctionViewDef(axisColor, axisLabelColor, gridColor, backgroundColor);
    }

    public int getAxisColor() {
        return axisColor;
    }

    public int getAxisLabelsColor() {
        return axisLabelsColor;
    }

    public int getGridColor() {
        return gridColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }
}
