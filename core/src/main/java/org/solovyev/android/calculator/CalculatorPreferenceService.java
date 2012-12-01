package org.solovyev.android.calculator;

import jscl.AngleUnit;
import jscl.NumeralBase;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 11/17/12
 * Time: 7:45 PM
 */
public interface CalculatorPreferenceService {

    void setPreferredAngleUnits();
    void setAngleUnits(@NotNull AngleUnit angleUnit);

    void setPreferredNumeralBase();
    void setNumeralBase(@NotNull NumeralBase numeralBase);

    void checkPreferredPreferences(boolean force);
}
