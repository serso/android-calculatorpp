package org.solovyev.android.calculator;

import javax.annotation.Nonnull;

import java.io.Serializable;

/**
 * User: Solovyev_S
 * Date: 21.09.12
 * Time: 11:48
 */
public interface CalculatorEditorViewState extends Serializable {

	@Nonnull
	String getText();

	int getSelection();
}
