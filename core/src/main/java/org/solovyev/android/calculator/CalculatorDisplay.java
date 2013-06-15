/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: serso
 * Date: 12/17/11
 * Time: 9:45 PM
 */
public interface CalculatorDisplay extends CalculatorEventListener {

	void setView(@Nullable CalculatorDisplayView view);

	@Nullable
	CalculatorDisplayView getView();

	@Nonnull
	CalculatorDisplayViewState getViewState();

	void setViewState(@Nonnull CalculatorDisplayViewState viewState);

	@Nonnull
	CalculatorEventData getLastEventData();
}
