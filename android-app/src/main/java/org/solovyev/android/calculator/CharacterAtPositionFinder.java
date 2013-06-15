/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.common.JPredicate;

/**
 * User: serso
 * Date: 10/3/11
 * Time: 12:54 AM
 */
public class CharacterAtPositionFinder implements JPredicate<Character> {

	private int i;

	@Nonnull
	private final String targetString;

	public CharacterAtPositionFinder(@Nonnull String targetString, int i) {
		this.targetString = targetString;
		this.i = i;
	}

	@Override
	public boolean apply(@Nullable Character s) {
		return s != null && s.equals(targetString.charAt(i));
	}

	public void setI(int i) {
		this.i = i;
	}
}
