package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 18:18
 */
class CalculatorEventDataIdImpl implements CalculatorEventDataId {

    private final long eventId;

    @Nullable
    private final Long sequenceId;

    private CalculatorEventDataIdImpl(long id, @Nullable Long sequenceId) {
        this.eventId = id;
        this.sequenceId = sequenceId;
    }

    @NotNull
    static CalculatorEventDataId newInstance(long id, @Nullable Long sequenceId) {
        return new CalculatorEventDataIdImpl(id, sequenceId);
    }

    @Override
    public long getEventId() {
        return this.eventId;
    }

    @Nullable
    @Override
    public Long getSequenceId() {
        return this.sequenceId;
    }

    @Override
    public boolean isAfter(@NotNull CalculatorEventDataId calculatorEventDataId) {
        return this.eventId > calculatorEventDataId.getEventId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalculatorEventDataIdImpl)) return false;

        CalculatorEventDataIdImpl that = (CalculatorEventDataIdImpl) o;

        if (eventId != that.eventId) return false;
        if (sequenceId != null ? !sequenceId.equals(that.sequenceId) : that.sequenceId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (eventId ^ (eventId >>> 32));
        result = 31 * result + (sequenceId != null ? sequenceId.hashCode() : 0);
        return result;
    }
}
