package org.solovyev.android.calculator;

import javax.annotation.Nonnull;

/**
 * User: Solovyev_S
 * Date: 24.09.12
 * Time: 16:12
 */
public class ConversionFailureImpl implements ConversionFailure {

	@Nonnull
	private Exception exception;

	public ConversionFailureImpl(@Nonnull Exception exception) {
		this.exception = exception;
	}

	@Nonnull
	@Override
	public Exception getException() {
		return this.exception;
	}
}
