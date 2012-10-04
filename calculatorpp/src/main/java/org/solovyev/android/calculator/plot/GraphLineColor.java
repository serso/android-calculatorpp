package org.solovyev.android.calculator.plot;

import android.graphics.Color;

/**
 * User: serso
 * Date: 10/4/12
 * Time: 10:08 PM
 */
public enum GraphLineColor {

    white(Color.WHITE),
    grey(Color.GRAY),
    red(Color.RED),
    blue(Color.rgb(16, 100, 140)),
    green(Color.GREEN);

    private final int color;

    private GraphLineColor(int color) {
        this.color = color;
    }


    public int getColor() {
        return this.color;
    }
}
