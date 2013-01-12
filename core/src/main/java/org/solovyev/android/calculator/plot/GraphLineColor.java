package org.solovyev.android.calculator.plot;

/**
 * User: serso
 * Date: 10/4/12
 * Time: 10:08 PM
 */
public enum GraphLineColor {

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

    private GraphLineColor(int color) {
        this.color = color;
    }


    public int getColor() {
        return this.color;
    }
}
