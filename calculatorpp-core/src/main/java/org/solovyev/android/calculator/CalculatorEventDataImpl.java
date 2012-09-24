package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 18:18
 */
class CalculatorEventDataImpl implements CalculatorEventData {

    private static final long NO_SEQUENCE = -1L;

    private final long eventId;

    @NotNull
    private Long sequenceId = NO_SEQUENCE;

    private CalculatorEventDataImpl(long id, @NotNull Long sequenceId) {
        this.eventId = id;
        this.sequenceId = sequenceId;
    }

    @NotNull
    static CalculatorEventData newInstance(long id, @NotNull Long sequenceId) {
        return new CalculatorEventDataImpl(id, sequenceId);
    }

    @Override
    public long getEventId() {
        return this.eventId;
    }

    @NotNull
    @Override
    public Long getSequenceId() {
        return this.sequenceId;
    }

    @Override
    public boolean isAfter(@NotNull CalculatorEventData that) {
        return this.eventId > that.getEventId();
    }

    @Override
    public boolean isSameSequence(@NotNull CalculatorEventData that) {
        return !this.sequenceId.equals(NO_SEQUENCE) && this.sequenceId.equals(that.getSequenceId());
    }

    @Override
    public boolean isAfterSequence(@NotNull CalculatorEventData that) {
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
