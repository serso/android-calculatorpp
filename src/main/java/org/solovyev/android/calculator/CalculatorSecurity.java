/*
 * Copyright (c) 2009-2012. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 1/4/12
 * Time: 1:23 AM
 */
public final class CalculatorSecurity {

	private CalculatorSecurity() {
	}

	@NotNull
	public static String getPK() {
		return "org.solovyev.android.calculator";
	}
}
