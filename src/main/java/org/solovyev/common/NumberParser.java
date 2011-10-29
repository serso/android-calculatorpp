/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.utils.Parser;

/**
 * User: serso
 * Date: 9/26/11
 * Time: 11:07 PM
 */
public class NumberParser<T extends Number> implements Parser<T> {

	@NotNull
	private final Class<T> clazz;

	public NumberParser(@NotNull Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public T parseValue(@Nullable String value) throws IllegalArgumentException {
		T result;

		if (value != null) {
			if (this.clazz.equals(Integer.class)) {
				result = (T) Integer.valueOf(value);
			} else if (this.clazz.equals(Float.class)) {
				result = (T) Float.valueOf(value);
			} else if (this.clazz.equals(Long.class)) {
				result = (T) Long.valueOf(value);
			} else {
				throw new UnsupportedOperationException(this.clazz + " is not supported!");
			}
		} else {
			result = null;
		}

		return result;
	}
}
