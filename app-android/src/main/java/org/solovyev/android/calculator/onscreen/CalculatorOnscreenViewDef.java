package org.solovyev.android.calculator.onscreen;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 11/21/12
 * Time: 10:55 PM
 */
public class CalculatorOnscreenViewDef {

    private int width;
    private int height;
    private int x;
    private int y;

    private CalculatorOnscreenViewDef() {
    }

    @NotNull
    public static CalculatorOnscreenViewDef newInstance(int width, int height, int x, int y) {
        final CalculatorOnscreenViewDef result = new CalculatorOnscreenViewDef();
        result.width = width;
        result.height = height;
        result.x = x;
        result.y = y;
        return result;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
