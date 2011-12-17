/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.common;


import org.jetbrains.annotations.Nullable;
import org.solovyev.common.utils.Equalizer;

/**
 * User: serso
 * Date: 12/17/11
 * Time: 11:11 PM
 */
public enum SameEqualizer implements Equalizer {

	instance;

	private SameEqualizer() {
	}

	@Override
	public boolean equals(@Nullable Object first, @Nullable Object second) {
		return first == second;
	}
}
