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
public class ApplicationDataImpl implements ApplicationData {

	private final boolean free;

	private final int applicationTitle;

	@NotNull
	private final Type type;

	public ApplicationDataImpl(boolean free, int applicationTitle, @NotNull Type type) {
		this.free = free;
		this.applicationTitle = applicationTitle;
		this.type = type;
	}

	@Override
	public boolean isFree() {
		return this.free;
	}

	@Override
	public boolean isShowAd() {
		return this.free;
	}

	@Override
	public int getApplicationTitle() {
		return this.applicationTitle;
	}

	@Override
	@NotNull
	public Type getType() {
		return type;
	}
}
