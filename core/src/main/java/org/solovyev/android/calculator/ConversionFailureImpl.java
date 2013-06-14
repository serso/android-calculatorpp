package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

/**
 * User: Solovyev_S
 * Date: 24.09.12
 * Time: 16:12
 */
public class ConversionFailureImpl implements ConversionFailure {

	@NotNull
	private Exception exception;

	public ConversionFailureImpl(@NotNull Exception exception) {
		this.exception = exception;
	}

	@NotNull
	@Override
	public Exception getException() {
		return this.exception;
	}
}
