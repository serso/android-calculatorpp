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
import org.solovyev.common.utils.Parser;

/**
 * User: serso
 * Date: 9/21/11
 * Time: 12:06 AM
 */
public class FloatIntervalMapper extends AbstractIntervalMapper<Float> {
	@NotNull
	@Override
	protected Formatter<Float> getFormatter() {
		return new ValueOfFormatter<Float>();
	}

	@NotNull
	@Override
	protected Parser<Float> getParser() {
		return new Parser<Float>() {
			@Override
			public Float parseValue(@Nullable String s) throws IllegalArgumentException {
				return Float.valueOf(s);
			}
		};
	}
}
