/*
 * Copyright (c) 2009-2012. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 1/2/12
 * Time: 9:32 PM
 */
public class CalculatorActivityFree extends AbstractCalculatorActivity {

	public CalculatorActivityFree() {
		super(createApplicationData());
	}

	@NotNull
	private static ApplicationData createApplicationData() {
		return new ApplicationDataImpl(true, R.string.c_app_name_free, ApplicationData.Type.free);
	}
}
