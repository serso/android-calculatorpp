package org.solovyev.android.calculator.model;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 9/26/11
 * Time: 12:12 PM
 */
public interface TextProcessor<T extends CharSequence> {

	@NotNull
	T process(@NotNull String s) throws CalculatorParseException;
}
