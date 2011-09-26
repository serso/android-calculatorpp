/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.math.ValueOfFormatter;
import org.solovyev.common.utils.Formatter;
import org.solovyev.common.utils.Mapper;
import org.solovyev.common.utils.Parser;

/**
 * User: serso
 * Date: 9/26/11
 * Time: 11:10 PM
 */
public class NumberMapper<T extends Number> implements Mapper<T>{

	@NotNull
	private final Formatter<T> formatter = new ValueOfFormatter<T>();

	@NotNull
	private final Parser<T> parser;

	public NumberMapper(@NotNull Class<T> clazz) {
		this.parser = new NumberParser<T>(clazz);
	}

	@Override
	public String formatValue(@Nullable T value) throws IllegalArgumentException {
		return formatter.formatValue(value);
	}

	@Override
	public T parseValue(@Nullable String value) throws IllegalArgumentException {
		return this.parser.parseValue(value);
	}
}
