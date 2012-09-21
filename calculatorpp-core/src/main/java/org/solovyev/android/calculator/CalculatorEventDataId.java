package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 18:18
 */
public interface CalculatorEventDataId {

    // the higher id => the later event
    long getEventId();

    // the higher id => the later event
    @NotNull
    Long getSequenceId();

    boolean isAfter(@NotNull CalculatorEventDataId that);

    boolean isSameSequence(@NotNull CalculatorEventDataId that);
}
