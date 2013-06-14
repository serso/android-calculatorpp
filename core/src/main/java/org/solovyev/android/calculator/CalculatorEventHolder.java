package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 10/9/12
 * Time: 9:59 PM
 */
public class CalculatorEventHolder {

	@NotNull
	private volatile CalculatorEventData lastEventData;

	public CalculatorEventHolder(@NotNull CalculatorEventData lastEventData) {
		this.lastEventData = lastEventData;
	}

	@NotNull
	public synchronized CalculatorEventData getLastEventData() {
		return lastEventData;
	}

	@NotNull
	public synchronized Result apply(@NotNull CalculatorEventData newEventData) {
		final Result result = new Result(lastEventData, newEventData);

		if (result.isNewAfter()) {
			this.lastEventData = newEventData;
		}

		return result;
	}

	public static class Result {

		@NotNull
		private final CalculatorEventData lastEventData;

		@NotNull
		private final CalculatorEventData newEventData;

		@Nullable
		private Boolean after = null;

		@Nullable
		private Boolean sameSequence = null;

		public Result(@NotNull CalculatorEventData lastEventData,
					  @NotNull CalculatorEventData newEventData) {
			this.lastEventData = lastEventData;
			this.newEventData = newEventData;
		}

		public boolean isNewAfter() {
			if (after == null) {
				after = newEventData.isAfter(lastEventData);
			}
			return after;
		}

		public boolean isSameSequence() {
			if (sameSequence == null) {
				sameSequence = newEventData.isSameSequence(lastEventData);
			}
			return sameSequence;
		}

		public boolean isNewAfterSequence() {
			return newEventData.isAfterSequence(lastEventData);
		}

		public boolean isNewSameOrAfterSequence() {
			return isSameSequence() || isNewAfterSequence();
		}
	}
}
