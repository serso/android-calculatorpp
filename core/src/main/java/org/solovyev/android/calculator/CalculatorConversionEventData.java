package org.solovyev.android.calculator;

import jscl.NumeralBase;
import jscl.math.Generic;
import javax.annotation.Nonnull;

/**
 * User: Solovyev_S
 * Date: 24.09.12
 * Time: 16:45
 */
public interface CalculatorConversionEventData extends CalculatorEventData {

	// display state on the moment of conversion
	@Nonnull
	CalculatorDisplayViewState getDisplayState();

	@Nonnull
	NumeralBase getFromNumeralBase();

	@Nonnull
	NumeralBase getToNumeralBase();

	@Nonnull
	Generic getValue();
}
