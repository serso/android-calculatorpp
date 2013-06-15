package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.EventListener;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 16:39
 */
public interface CalculatorEventListener extends EventListener {

	void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data);

}
