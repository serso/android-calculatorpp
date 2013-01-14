package org.solovyev.android.calculator.plot;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 10/4/12
 * Time: 10:08 PM
 */
public enum PlotLineColor {

    // Color.WHITE
    white(0xFFFFFFFF),

    // Color.GRAY
    grey(0xFF888888),

    // Color.RED
    red(0xFFFF0000),

    blue(0xFF10648C),

    // Color.GREEN
    green(0xFF00FF00);

    private final int color;

    private PlotLineColor(int color) {
        this.color = color;
    }


    public int getColor() {
        return this.color;
    }

	@NotNull
	public static PlotLineColor valueOf(int color) {
		for (PlotLineColor plotLineColor : PlotLineColor.values()) {
			if ( plotLineColor.color == color ) {
				return plotLineColor;
			}
		}

		return PlotLineColor.white;
	}
}
