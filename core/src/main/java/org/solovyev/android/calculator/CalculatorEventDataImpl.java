package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 18:18
 */
class CalculatorEventDataImpl implements CalculatorEventData {

	private static final long NO_SEQUENCE = -1L;

	private final long eventId;

	@Nonnull
	private Long sequenceId = NO_SEQUENCE;

	private final Object source;

	private CalculatorEventDataImpl(long id, @Nonnull Long sequenceId, @Nullable Object source) {
		this.eventId = id;
		this.sequenceId = sequenceId;
		this.source = source;
	}

	@Nonnull
	static CalculatorEventData newInstance(long id, @Nonnull Long sequenceId) {
		return new CalculatorEventDataImpl(id, sequenceId, null);
	}

	@Nonnull
	static CalculatorEventData newInstance(long id, @Nonnull Long sequenceId, @Nonnull Object source) {
		return new CalculatorEventDataImpl(id, sequenceId, source);
	}

	@Override
	public long getEventId() {
		return this.eventId;
	}

	@Nonnull
	@Override
	public Long getSequenceId() {
		return this.sequenceId;
	}

	@Override
	public Object getSource() {
		return this.source;
	}

	@Override
	public boolean isAfter(@Nonnull CalculatorEventData that) {
		return this.eventId > that.getEventId();
	}

	@Override
	public boolean isSameSequence(@Nonnull CalculatorEventData that) {
		return !this.sequenceId.equals(NO_SEQUENCE) && this.sequenceId.equals(that.getSequenceId());
	}

	@Override
	public boolean isAfterSequence(@Nonnull CalculatorEventData that) {
		return !this.sequenceId.equals(NO_SEQUENCE) && this.sequenceId > that.getSequenceId();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CalculatorEventDataImpl)) return false;

		CalculatorEventDataImpl that = (CalculatorEventDataImpl) o;

		if (eventId != that.eventId) return false;
		if (!sequenceId.equals(that.sequenceId))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (int) (eventId ^ (eventId >>> 32));
		result = 31 * result + (sequenceId.hashCode());
		return result;
	}
}
