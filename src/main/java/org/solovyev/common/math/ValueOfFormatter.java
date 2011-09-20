/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.common.math;

import org.jetbrains.annotations.Nullable;
import org.solovyev.common.utils.Formatter;

/**
 * User: serso
 * Date: 9/20/11
 * Time: 10:52 PM
 */
public class ValueOfFormatter<T> implements Formatter<T>{

	@Override
	public String formatValue(@Nullable T t) throws IllegalArgumentException {
		return String.valueOf(t);
	}
}
