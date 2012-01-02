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
 * Time: 9:08 PM
 */
public interface ApplicationData {

	public static enum Type {
		free,
		pro
	}

	boolean isFree();

	boolean isShowAd();

	int getApplicationTitle();

	@NotNull
	Type getType();

}
