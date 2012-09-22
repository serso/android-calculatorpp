package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 16:54
 */
class CalculatorEventDataImpl implements CalculatorEventData {

    @NotNull
    private CalculatorEventDataId calculatorEventDataId;

    private CalculatorEventDataImpl(@NotNull CalculatorEventDataId calculatorEventDataId) {
        this.calculatorEventDataId = calculatorEventDataId;
    }

    @NotNull
    public static CalculatorEventData newInstance(@NotNull CalculatorEventDataId calculatorEventDataId) {
        return new CalculatorEventDataImpl(calculatorEventDataId);
    }

    @Override
    public long getEventId() {
        return calculatorEventDataId.getEventId();
    }

    @NotNull
    @Override
    public Long getSequenceId() {
        return calculatorEventDataId.getSequenceId();
    }

    @Override
    public boolean isAfter(@NotNull CalculatorEventDataId that) {
        return this.calculatorEventDataId.isAfter(that);
    }

    @Override
    public boolean isSameSequence(@NotNull CalculatorEventDataId that) {
        return this.calculatorEventDataId.isSameSequence(that);
    }

    @Override
    public boolean isAfterSequence(@NotNull CalculatorEventDataId that) {
        return this.calculatorEventDataId.isAfterSequence(that);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalculatorEventDataImpl)) return false;

        CalculatorEventDataImpl that = (CalculatorEventDataImpl) o;

        if (!calculatorEventDataId.equals(that.calculatorEventDataId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return calculatorEventDataId.hashCode();
    }
}
