package org.solovyev.android.calculator.plot;

import jscl.math.Generic;
import jscl.math.function.Constant;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 12/1/12
 * Time: 5:09 PM
 */
public class PlotInput {

    @NotNull
    private Generic function;

    @NotNull
    private Constant constant;

    public PlotInput() {
    }

    private PlotInput(@NotNull Generic function, @NotNull Constant constant) {
        this.function = function;
        this.constant = constant;
    }

    @NotNull
    public static PlotInput newInstance(@NotNull Generic function, @NotNull Constant constant) {
        return new PlotInput(function, constant);
    }

    @NotNull
    public Generic getFunction() {
        return function;
    }

    @NotNull
    public Constant getConstant() {
        return constant;
    }
}
