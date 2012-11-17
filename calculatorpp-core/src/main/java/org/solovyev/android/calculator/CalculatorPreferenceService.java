package org.solovyev.android.calculator;

import jscl.AngleUnit;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 11/17/12
 * Time: 7:45 PM
 */
public interface CalculatorPreferenceService {

    void setAngleUnits(@NotNull AngleUnit angleUnit);
}
