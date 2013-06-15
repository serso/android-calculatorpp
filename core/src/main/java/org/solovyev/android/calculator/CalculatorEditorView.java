package org.solovyev.android.calculator;

import javax.annotation.Nonnull;

/**
 * User: Solovyev_S
 * Date: 21.09.12
 * Time: 11:48
 */
public interface CalculatorEditorView {

	void setState(@Nonnull CalculatorEditorViewState viewState);
}
