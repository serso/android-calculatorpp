package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 18:18
 */
class CalculatorEventDataIdImpl implements CalculatorEventDataId {

    private static final long NO_SEQUENCE = -1L;

    private final long eventId;

    @NotNull
    private Long sequenceId = NO_SEQUENCE;

    private CalculatorEventDataIdImpl(long id, @NotNull Long sequenceId) {
        this.eventId = id;
        this.sequenceId = sequenceId;
    }

    @NotNull
    static CalculatorEventDataId newInstance(long id, @NotNull Long sequenceId) {
        return new CalculatorEventDataIdImpl(id, sequenceId);
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
    public boolean isAfter(@NotNull CalculatorEventDataId that) {
        return this.eventId > that.getEventId();
    }

    @Override
    public boolean isSameSequence(@NotNull CalculatorEventDataId that) {
        return !this.sequenceId.equals(NO_SEQUENCE) && this.sequenceId.equals(that.getSequenceId());
    }

    @Override
    public boolean isAfterSequence(@NotNull CalculatorEventDataId that) {
        return !this.sequenceId.equals(NO_SEQUENCE) && this.sequenceId > that.getSequenceId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalculatorEventDataIdImpl)) return false;

        CalculatorEventDataIdImpl that = (CalculatorEventDataIdImpl) o;

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
