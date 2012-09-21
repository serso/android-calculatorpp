package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 18:18
 */
public interface CalculatorEventDataId {

    // the higher id => the later event
    long getEventId();

    // the higher id => the later event
    @Nullable
    Long getSequenceId();

    boolean isAfter(@NotNull CalculatorEventDataId calculatorEventDataId);
}
