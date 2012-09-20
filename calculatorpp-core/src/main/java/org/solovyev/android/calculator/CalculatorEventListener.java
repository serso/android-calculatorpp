package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EventListener;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 16:39
 */
public interface CalculatorEventListener extends EventListener {

    void onCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable Object data);

}
