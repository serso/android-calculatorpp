/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.utils.Finder;

/**
* User: serso
* Date: 10/3/11
* Time: 12:49 AM
*/
public class StartsWithFinder implements Finder<String> {

	private int i;

	@NotNull
	private final String targetString;

	public StartsWithFinder(@NotNull String targetString, int i) {
		this.targetString = targetString;
		this.i = i;
	}

	@Override
	public boolean isFound(@Nullable String s) {
		return targetString.startsWith(s, i);
	}

	public void setI(int i) {
		this.i = i;
	}
}
