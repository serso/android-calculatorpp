package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 8:25 PM
 */
public interface CalculatorDisplayView {

	void setState(@NotNull CalculatorDisplayViewState state);

	@NotNull
	CalculatorDisplayViewState getState();
}
