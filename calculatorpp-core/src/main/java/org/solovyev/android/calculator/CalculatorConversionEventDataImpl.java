package org.solovyev.android.calculator;

import jscl.NumeralBase;
import jscl.math.Generic;
import org.jetbrains.annotations.NotNull;

/**
 * User: Solovyev_S
 * Date: 24.09.12
 * Time: 16:48
 */
public class CalculatorConversionEventDataImpl implements CalculatorConversionEventData {

    @NotNull
    private CalculatorEventData calculatorEventData;

    @NotNull
    private NumeralBase fromNumeralBase;

    @NotNull
    private NumeralBase toNumeralBase;

    @NotNull
    private Generic value;

    @NotNull
    private CalculatorDisplayViewState displayState;

    private CalculatorConversionEventDataImpl() {
    }

    @NotNull
    public static CalculatorConversionEventData newInstance(@NotNull CalculatorEventData calculatorEventData,
                                                            @NotNull Generic value,
                                                            @NotNull NumeralBase from,
                                                            @NotNull NumeralBase to,
                                                            @NotNull CalculatorDisplayViewState displayViewState) {
        final CalculatorConversionEventDataImpl result = new CalculatorConversionEventDataImpl();

        result.calculatorEventData = calculatorEventData;
        result.value = value;
        result.displayState = displayViewState;
        result.fromNumeralBase = from;
        result.toNumeralBase = to;

        return result;
    }

    @Override
    public long getEventId() {
        return calculatorEventData.getEventId();
    }

    @Override
    @NotNull
    public Long getSequenceId() {
        return calculatorEventData.getSequenceId();
    }

    @Override
    public boolean isAfter(@NotNull CalculatorEventData that) {
        return calculatorEventData.isAfter(that);
    }

    @Override
    public boolean isSameSequence(@NotNull CalculatorEventData that) {
        return calculatorEventData.isSameSequence(that);
    }

    @Override
    public boolean isAfterSequence(@NotNull CalculatorEventData that) {
        return calculatorEventData.isAfterSequence(that);
    }

    @NotNull
    @Override
    public CalculatorDisplayViewState getDisplayState() {
        return this.displayState;
    }

    @Override
    @NotNull
    public NumeralBase getFromNumeralBase() {
        return fromNumeralBase;
    }

    @Override
    @NotNull
    public NumeralBase getToNumeralBase() {
        return toNumeralBase;
    }

    @Override
    @NotNull
    public Generic getValue() {
        return value;
    }
}
