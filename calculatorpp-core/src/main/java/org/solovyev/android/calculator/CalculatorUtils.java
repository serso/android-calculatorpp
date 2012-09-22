package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

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
    public static CalculatorEventDataId createFirstEventDataId() {
        return CalculatorEventDataIdImpl.newInstance(FIRST_ID, FIRST_ID);
    }
}
