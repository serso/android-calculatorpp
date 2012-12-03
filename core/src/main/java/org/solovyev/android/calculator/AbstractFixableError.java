package org.solovyev.android.calculator;

import org.jetbrains.annotations.Nullable;

public abstract class AbstractFixableError implements FixableError {

	@Nullable
	private String fixCaption;

	protected AbstractFixableError() {
	}

	protected AbstractFixableError(@Nullable String fixCaption) {
		this.fixCaption = fixCaption;
	}

	@Nullable
	@Override
	public CharSequence getFixCaption() {
		return fixCaption;
	}
}
