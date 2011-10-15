/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * User: serso
 * Date: 10/15/11
 * Time: 1:45 PM
 */
public class AbstractHistoryState {

	@NotNull
	private final Date time = new Date();

	@NotNull
	public Date getTime() {
		return time;
	}
}
