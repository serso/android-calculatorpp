package org.solovyev.android.calculator;

import jscl.math.Generic;
import jscl.math.function.Constant;
import jscl.math.function.IConstant;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.jscl.JsclOperation;

import java.util.HashSet;
import java.util.Set;

/**
 * User: serso
 * Date: 9/22/12
 * Time: 7:13 PM
 */
public final class CalculatorUtils {

    static final long FIRST_ID = 0;

    private CalculatorUtils() {
        throw new AssertionError();
    }

    @NotNull
    public static CalculatorEventData createFirstEventDataId() {
        return CalculatorEventDataImpl.newInstance(FIRST_ID, FIRST_ID);
    }

    @NotNull
    public static Set<Constant> getNotSystemConstants(@NotNull Generic expression) {
        final Set<Constant> notSystemConstants = new HashSet<Constant>();

        for (Constant constant : expression.getConstants()) {
            IConstant var = Locator.getInstance().getEngine().getVarsRegistry().get(constant.getName());
            if (var != null && !var.isSystem() && !var.isDefined()) {
                notSystemConstants.add(constant);
            }
        }

        return notSystemConstants;
    }

    public static boolean isPlotPossible(@NotNull Generic expression, @NotNull JsclOperation operation) {
        boolean result = false;

        if (operation == JsclOperation.simplify) {
            if (getNotSystemConstants(expression).size() == 1) {
                result = true;
            }
        }

        return result;
    }
}
