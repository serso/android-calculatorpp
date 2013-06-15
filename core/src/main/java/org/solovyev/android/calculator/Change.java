package org.solovyev.android.calculator;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 10/1/12
 * Time: 11:16 PM
 */
public interface Change<T> {

	@Nonnull
	T getOldValue();

	@Nonnull
	T getNewValue();

}
