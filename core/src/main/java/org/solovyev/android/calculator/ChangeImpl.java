package org.solovyev.android.calculator;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 10/1/12
 * Time: 11:18 PM
 */
public class ChangeImpl<T> implements Change<T> {

	@Nonnull
	private T oldValue;

	@Nonnull
	private T newValue;

	private ChangeImpl() {
	}

	@Nonnull
	public static <T> Change<T> newInstance(@Nonnull T oldValue, @Nonnull T newValue) {
		final ChangeImpl<T> result = new ChangeImpl<T>();

		result.oldValue = oldValue;
		result.newValue = newValue;

		return result;
	}

	@Nonnull
	@Override
	public T getOldValue() {
		return this.oldValue;
	}

	@Nonnull
	@Override
	public T getNewValue() {
		return this.newValue;
	}
}
