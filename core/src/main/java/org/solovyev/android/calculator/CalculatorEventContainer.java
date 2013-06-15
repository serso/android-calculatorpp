package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 16:39
 */
public interface CalculatorEventContainer {

	void addCalculatorEventListener(@Nonnull CalculatorEventListener calculatorEventListener);

	void removeCalculatorEventListener(@Nonnull CalculatorEventListener calculatorEventListener);

	void fireCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data);

	void fireCalculatorEvents(@Nonnull List<CalculatorEvent> calculatorEvents);

	public static class CalculatorEvent {

		@Nonnull
		private CalculatorEventData calculatorEventData;

		@Nonnull
		private CalculatorEventType calculatorEventType;

		@Nullable
		private Object data;

		public CalculatorEvent(@Nonnull CalculatorEventData calculatorEventData,
							   @Nonnull CalculatorEventType calculatorEventType,
							   @Nullable Object data) {
			this.calculatorEventData = calculatorEventData;
			this.calculatorEventType = calculatorEventType;
			this.data = data;
		}

		@Nonnull
		public CalculatorEventData getCalculatorEventData() {
			return calculatorEventData;
		}

		@Nonnull
		public CalculatorEventType getCalculatorEventType() {
			return calculatorEventType;
		}

		@Nullable
		public Object getData() {
			return data;
		}
	}
}
