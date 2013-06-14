package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * User: Solovyev_S
 * Date: 21.09.12
 * Time: 11:48
 */
public interface CalculatorEditorViewState extends Serializable {

	@NotNull
	String getText();

	int getSelection();
}
