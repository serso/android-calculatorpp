package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

/**
 * User: Solovyev_S
 * Date: 24.09.12
 * Time: 16:12
 */
public interface ConversionFailure {

	@NotNull
	Exception getException();
}
