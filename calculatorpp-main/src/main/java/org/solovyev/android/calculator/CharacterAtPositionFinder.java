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
 * Time: 12:54 AM
 */
public class CharacterAtPositionFinder implements Finder<Character> {

	private int i;

	@NotNull
	private final String targetString;

	public CharacterAtPositionFinder(@NotNull String targetString, int i) {
		this.targetString = targetString;
		this.i = i;
	}

	@Override
	public boolean isFound(@Nullable Character s) {
		return s != null && s.equals(targetString.charAt(i));
	}

	public void setI(int i) {
		this.i = i;
	}
}
