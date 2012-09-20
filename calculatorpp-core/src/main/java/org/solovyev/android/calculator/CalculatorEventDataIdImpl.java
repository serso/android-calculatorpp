package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 18:18
 */
public class CalculatorEventDataIdImpl implements CalculatorEventDataId {

    private final long eventId;

    @Nullable
    private final Long calculationId;

    private CalculatorEventDataIdImpl(long id,
                                      @Nullable Long calculationId) {
        this.eventId = id;
        this.calculationId = calculationId;
    }

    @NotNull
    public static CalculatorEventDataId newInstance(long id,
                                                    @Nullable Long calculationId) {
        return new CalculatorEventDataIdImpl(id, calculationId);
    }

    @Override
    public long getEventId() {
        return this.eventId;
    }

    @Nullable
    @Override
    public Long getCalculationId() {
        return this.calculationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalculatorEventDataIdImpl)) return false;

        CalculatorEventDataIdImpl that = (CalculatorEventDataIdImpl) o;

        if (eventId != that.eventId) return false;
        if (calculationId != null ? !calculationId.equals(that.calculationId) : that.calculationId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (eventId ^ (eventId >>> 32));
        result = 31 * result + (calculationId != null ? calculationId.hashCode() : 0);
        return result;
    }
}
