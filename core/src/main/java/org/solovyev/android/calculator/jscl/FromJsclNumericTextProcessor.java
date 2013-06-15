/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.jscl;

import jscl.math.Generic;
import javax.annotation.Nonnull;
import org.solovyev.android.calculator.CalculatorParseException;
import org.solovyev.android.calculator.text.TextProcessor;

/**
 * User: serso
 * Date: 10/6/11
 * Time: 9:48 PM
 */
class FromJsclNumericTextProcessor implements TextProcessor<String, Generic> {

	public static final FromJsclNumericTextProcessor instance = new FromJsclNumericTextProcessor();

	@Nonnull
	@Override
	public String process(@Nonnull Generic numeric) throws CalculatorParseException {
		return numeric.toString().replace("*", "");
	}
}
