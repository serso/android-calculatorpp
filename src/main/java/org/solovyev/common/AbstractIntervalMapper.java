/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.utils.*;

import java.util.Arrays;
import java.util.List;

/**
 * User: serso
 * Date: 9/21/11
 * Time: 12:02 AM
 */
public abstract class AbstractIntervalMapper<T> implements Mapper<Interval<T>> {

	@Override
	public String formatValue(@Nullable Interval<T> interval) throws IllegalArgumentException {
		if (interval != null) {
			return CollectionsUtils.formatValue(Arrays.asList(interval.getLeftBorder(), interval.getRightBorder()), ";", getFormatter());
		} else {
			return null;
		}
	}

	@NotNull
	protected abstract Formatter<T> getFormatter();

	@Override
	public Interval<T> parseValue(@Nullable String s) throws IllegalArgumentException {
		final List<T> list = CollectionsUtils.split(s, ";", getParser());

		assert list.size() == 2;
		return new IntervalImpl<T>(list.get(0), list.get(1));
	}

	@NotNull
	protected abstract Parser<T> getParser();
}
