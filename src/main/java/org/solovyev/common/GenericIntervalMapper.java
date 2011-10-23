/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.common;

import org.jetbrains.annotations.NotNull;
import org.solovyev.common.utils.Formatter;
import org.solovyev.common.utils.Mapper;
import org.solovyev.common.utils.Parser;

/**
 * User: serso
 * Date: 9/26/11
 * Time: 10:45 PM
 */
public abstract class GenericIntervalMapper<T> extends AbstractIntervalMapper<T> {

	@NotNull
	private final Mapper<T> mapper;

	public GenericIntervalMapper(@NotNull Mapper<T> mapper) {
		this.mapper = mapper;
	}

	@NotNull
	@Override
	protected Formatter<T> getFormatter() {
		return mapper;
	}

	@NotNull
	@Override
	protected Parser<T> getParser() {
		return mapper;
	}

	@NotNull
	public Mapper<T> getMapper() {
		return mapper;
	}
}
