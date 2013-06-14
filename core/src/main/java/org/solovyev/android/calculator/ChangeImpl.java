package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 10/1/12
 * Time: 11:18 PM
 */
public class ChangeImpl<T> implements Change<T> {

	@NotNull
	private T oldValue;

	@NotNull
	private T newValue;

	private ChangeImpl() {
	}

	@NotNull
	public static <T> Change<T> newInstance(@NotNull T oldValue, @NotNull T newValue) {
		final ChangeImpl<T> result = new ChangeImpl<T>();

		result.oldValue = oldValue;
		result.newValue = newValue;

		return result;
	}

	@NotNull
	@Override
	public T getOldValue() {
		return this.oldValue;
	}

	@NotNull
	@Override
	public T getNewValue() {
		return this.newValue;
	}
}
