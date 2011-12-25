/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.view.prefs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.utils.Mapper;

/**
 * User: serso
 * Date: 12/25/11
 * Time: 1:17 PM
 */
public class EnumMapper<T extends Enum> implements Mapper<T>{

	@NotNull
	private final Class<T> enumClass;

	public EnumMapper(@NotNull Class<T> enumClass) {
		this.enumClass = enumClass;
	}

	public static <T extends Enum> Mapper<T> newInstance(@NotNull Class<T> enumClass) {
		return new EnumMapper<T>(enumClass);
	}

	@Override
	public String formatValue(@Nullable T value) throws IllegalArgumentException {
		return value == null ? null : value.name();
	}

	@Override
	public T parseValue(@Nullable String value) throws IllegalArgumentException {
		return value == null ? null : (T)Enum.valueOf(enumClass, value);
	}
}
