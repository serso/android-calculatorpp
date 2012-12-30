package org.solovyev.android.calculator.plot;

import jscl.math.Generic;
import jscl.math.function.Constant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 12/1/12
 * Time: 5:09 PM
 */
public class PlotInput {

    @NotNull
    private Generic function;

    @NotNull
    private Constant xVariable;

    @Nullable
    private Constant yVariable;

    public PlotInput() {
    }

    @NotNull
    public static PlotInput newInstance(@NotNull Generic function,
                                        @NotNull Constant xVariable,
                                        @Nullable Constant yVariable) {
        PlotInput result = new PlotInput();

        result.function = function;
        result.xVariable = xVariable;
        result.yVariable = yVariable;

        return result;
    }

    @NotNull
    public Generic getFunction() {
        return function;
    }

    @NotNull
    public Constant getXVariable() {
        return xVariable;
    }

    @Nullable
    public Constant getYVariable() {
        return yVariable;
    }
}
