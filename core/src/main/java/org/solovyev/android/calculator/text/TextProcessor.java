package org.solovyev.android.calculator.text;

import javax.annotation.Nonnull;
import org.solovyev.android.calculator.CalculatorParseException;

/**
 * User: serso
 * Date: 9/26/11
 * Time: 12:12 PM
 */
public interface TextProcessor<TO extends CharSequence, FROM> {

	@Nonnull
	TO process(@Nonnull FROM from) throws CalculatorParseException;
}
