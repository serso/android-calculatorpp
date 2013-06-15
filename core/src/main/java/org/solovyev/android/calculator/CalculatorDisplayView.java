package org.solovyev.android.calculator;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 8:25 PM
 */
public interface CalculatorDisplayView {

	void setState(@Nonnull CalculatorDisplayViewState state);

	@Nonnull
	CalculatorDisplayViewState getState();
}
