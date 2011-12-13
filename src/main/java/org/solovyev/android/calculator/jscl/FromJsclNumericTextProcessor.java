/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.jscl;

import jscl.math.Generic;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.model.CalculatorParseException;
import org.solovyev.android.calculator.model.TextProcessor;

/**
 * User: serso
 * Date: 10/6/11
 * Time: 9:48 PM
 */
class FromJsclNumericTextProcessor implements TextProcessor<String, Generic> {

	@NotNull
	@Override
	public String process(@NotNull Generic numeric) throws CalculatorParseException {
		return numeric.toString().replace("*", "");
	}
}
