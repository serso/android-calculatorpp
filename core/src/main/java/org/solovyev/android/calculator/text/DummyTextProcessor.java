/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.text;

import jscl.math.Generic;
import javax.annotation.Nonnull;
import org.solovyev.android.calculator.CalculatorParseException;

/**
 * User: serso
 * Date: 10/18/11
 * Time: 10:39 PM
 */
public enum DummyTextProcessor implements TextProcessor<String, Generic> {

	instance;

	@Nonnull
	@Override
	public String process(@Nonnull Generic s) throws CalculatorParseException {
		return s.toString();
	}
}
