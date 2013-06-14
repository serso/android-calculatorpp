package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 16:39
 */
public interface CalculatorEventContainer {

	void addCalculatorEventListener(@NotNull CalculatorEventListener calculatorEventListener);

	void removeCalculatorEventListener(@NotNull CalculatorEventListener calculatorEventListener);

	void fireCalculatorEvent(@NotNull CalculatorEventData calculatorEventData, @NotNull CalculatorEventType calculatorEventType, @Nullable Object data);

	void fireCalculatorEvents(@NotNull List<CalculatorEvent> calculatorEvents);

	public static class CalculatorEvent {

		@NotNull
		private CalculatorEventData calculatorEventData;

		@NotNull
		private CalculatorEventType calculatorEventType;

		@Nullable
		private Object data;

		public CalculatorEvent(@NotNull CalculatorEventData calculatorEventData,
							   @NotNull CalculatorEventType calculatorEventType,
							   @Nullable Object data) {
			this.calculatorEventData = calculatorEventData;
			this.calculatorEventType = calculatorEventType;
			this.data = data;
		}

		@NotNull
		public CalculatorEventData getCalculatorEventData() {
			return calculatorEventData;
		}

		@NotNull
		public CalculatorEventType getCalculatorEventType() {
			return calculatorEventType;
		}

		@Nullable
		public Object getData() {
			return data;
		}
	}
}
