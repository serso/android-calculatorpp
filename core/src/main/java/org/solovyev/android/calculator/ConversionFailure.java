package org.solovyev.android.calculator;

import javax.annotation.Nonnull;

/**
 * User: Solovyev_S
 * Date: 24.09.12
 * Time: 16:12
 */
public interface ConversionFailure {

	@Nonnull
	Exception getException();
}
