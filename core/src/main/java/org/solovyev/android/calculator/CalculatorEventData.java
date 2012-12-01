package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 18:18
 */
public interface CalculatorEventData {

    // the higher id => the later event
    long getEventId();

    // the higher id => the later event
    @NotNull
    Long getSequenceId();

    @Nullable
    Object getSource();

    boolean isAfter(@NotNull CalculatorEventData that);

    boolean isSameSequence(@NotNull CalculatorEventData that);

    boolean isAfterSequence(@NotNull CalculatorEventData that);
}
