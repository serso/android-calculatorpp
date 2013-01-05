package org.solovyev.android.calculator.plot;

import org.javia.arity.Function;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 1/5/13
 * Time: 7:35 PM
 */
public class FunctionPlotDef {

    @NotNull
    private Function function;

    @NotNull
    private FunctionLineDef lineDef;

    private FunctionPlotDef() {
    }

    @NotNull
    public static FunctionPlotDef newInstance(@NotNull Function function) {
        return newInstance(function, FunctionLineDef.newDefaultInstance());
    }

    @NotNull
    public static FunctionPlotDef newInstance(@NotNull Function function, @NotNull FunctionLineDef lineDef) {
        final FunctionPlotDef result = new FunctionPlotDef();

        result.function = function;
        result.lineDef = lineDef;

        return result;
    }

    @NotNull
    public Function getFunction() {
        return function;
    }

    @NotNull
    public FunctionLineDef getLineDef() {
        return lineDef;
    }
}
