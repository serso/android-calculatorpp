/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 10/18/11
 * Time: 10:39 PM
 */
public enum DummyTextProcessor implements TextProcessor<String> {

	instance;

	@NotNull
	@Override
	public String process(@NotNull String s) throws ParseException {
		return s;
	}
}
