/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.math.ValueOfFormatter;
import org.solovyev.common.utils.*;

/**
* User: serso
* Date: 9/20/11
* Time: 11:56 PM
*/
public class IntegerIntervalMapper extends AbstractIntervalMapper<Integer> {

	@NotNull
	protected Formatter<Integer> getFormatter() {
		return new ValueOfFormatter<Integer>();
	}

	@NotNull
	protected Parser<Integer> getParser() {
		return new Parser<Integer>() {
			@Override
			public Integer parseValue(@Nullable String s) throws IllegalArgumentException {
				return Integer.valueOf(s);
			}
		};
	}
}
