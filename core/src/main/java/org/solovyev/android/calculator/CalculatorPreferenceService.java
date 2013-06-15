package org.solovyev.android.calculator;

import jscl.AngleUnit;
import jscl.NumeralBase;
import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 11/17/12
 * Time: 7:45 PM
 */
public interface CalculatorPreferenceService {

	void setPreferredAngleUnits();

	void setAngleUnits(@Nonnull AngleUnit angleUnit);

	void setPreferredNumeralBase();

	void setNumeralBase(@Nonnull NumeralBase numeralBase);

	void checkPreferredPreferences(boolean force);
}
