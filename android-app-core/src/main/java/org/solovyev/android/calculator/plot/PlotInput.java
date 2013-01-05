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

    @Nullable
    private Constant xVariable;

    @Nullable
    private Constant yVariable;

    public PlotInput() {
    }

    @NotNull
    public static PlotInput newInstance(@NotNull Generic function,
                                        @Nullable  Constant xVariable,
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

    @Nullable
    public Constant getXVariable() {
        return xVariable;
    }

    @Nullable
    public Constant getYVariable() {
        return yVariable;
    }
}
