/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.common.math.MathEntity;
import org.solovyev.common.math.MathRegistry;

/**
 * User: serso
 * Date: 10/30/11
 * Time: 1:02 AM
 */
public interface CalculatorMathRegistry<T extends MathEntity> extends MathRegistry<T> {

	@Nullable
	String getDescription(@Nonnull String mathEntityName);

	@Nullable
	String getCategory(@Nonnull T mathEntity);

	void load();

	void save();
}
