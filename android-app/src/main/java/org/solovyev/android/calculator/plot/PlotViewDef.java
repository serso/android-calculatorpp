package org.solovyev.android.calculator.plot;

import android.graphics.Color;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 1/5/13
 * Time: 9:11 PM
 */
public class PlotViewDef {

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

    private PlotViewDef() {
    }

    private PlotViewDef(int axisColor, int axisLabelColor, int gridColor, int backgroundColor) {
        this.axisColor = axisColor;
        this.axisLabelsColor = axisLabelColor;
        this.gridColor = gridColor;
        this.backgroundColor = backgroundColor;
    }

    @NotNull
    public static PlotViewDef newDefaultInstance() {
        return new PlotViewDef();
    }

    @NotNull
    public static PlotViewDef newInstance(int axisColor, int axisLabelColor, int gridColor, int backgroundColor) {
        return new PlotViewDef(axisColor, axisLabelColor, gridColor, backgroundColor);
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
