package org.solovyev.android.calculator;

import jscl.NumeralBase;
import jscl.math.Generic;
import org.jetbrains.annotations.NotNull;

/**
 * User: Solovyev_S
 * Date: 24.09.12
 * Time: 16:45
 */
public interface CalculatorConversionEventData extends CalculatorEventData {

    // display state on the moment of conversion
    @NotNull
    CalculatorDisplayViewState getDisplayState();

    @NotNull
    NumeralBase getFromNumeralBase();

    @NotNull
    NumeralBase getToNumeralBase();

    @NotNull
    Generic getValue();
}
