package org.solovyev.android.calculator;

import jscl.NumeralBase;
import jscl.math.Generic;
import javax.annotation.Nonnull;

/**
 * User: Solovyev_S
 * Date: 24.09.12
 * Time: 16:48
 */
public class CalculatorConversionEventDataImpl implements CalculatorConversionEventData {

	@Nonnull
	private CalculatorEventData calculatorEventData;

	@Nonnull
	private NumeralBase fromNumeralBase;

	@Nonnull
	private NumeralBase toNumeralBase;

	@Nonnull
	private Generic value;

	@Nonnull
	private CalculatorDisplayViewState displayState;

	private CalculatorConversionEventDataImpl() {
	}

	@Nonnull
	public static CalculatorConversionEventData newInstance(@Nonnull CalculatorEventData calculatorEventData,
															@Nonnull Generic value,
															@Nonnull NumeralBase from,
															@Nonnull NumeralBase to,
															@Nonnull CalculatorDisplayViewState displayViewState) {
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
	@Nonnull
	public Long getSequenceId() {
		return calculatorEventData.getSequenceId();
	}

	@Override
	public Object getSource() {
		return calculatorEventData.getSource();
	}

	@Override
	public boolean isAfter(@Nonnull CalculatorEventData that) {
		return calculatorEventData.isAfter(that);
	}

	@Override
	public boolean isSameSequence(@Nonnull CalculatorEventData that) {
		return calculatorEventData.isSameSequence(that);
	}

	@Override
	public boolean isAfterSequence(@Nonnull CalculatorEventData that) {
		return calculatorEventData.isAfterSequence(that);
	}

	@Nonnull
	@Override
	public CalculatorDisplayViewState getDisplayState() {
		return this.displayState;
	}

	@Override
	@Nonnull
	public NumeralBase getFromNumeralBase() {
		return fromNumeralBase;
	}

	@Override
	@Nonnull
	public NumeralBase getToNumeralBase() {
		return toNumeralBase;
	}

	@Override
	@Nonnull
	public Generic getValue() {
		return value;
	}
}
