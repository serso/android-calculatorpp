/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.utils.Interval;
import org.solovyev.common.utils.NumberInterval;

/**
* User: serso
* Date: 9/20/11
* Time: 11:56 PM
*/
public class NumberIntervalMapper<T extends Number> extends GenericIntervalMapper<T> {

	public NumberIntervalMapper(@NotNull Class<T> clazz) {
		super(new NumberMapper<T>(clazz));
	}

	@NotNull
	@Override
	protected Interval<T> newInstance(@Nullable T left, @Nullable T right) {
		return new NumberInterval<T>(left, right);
	}
}
