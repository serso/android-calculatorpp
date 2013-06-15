/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.common.JBuilder;
import org.solovyev.common.math.MathEntity;

/**
 * User: serso
 * Date: 12/22/11
 * Time: 9:21 PM
 */
public interface MathEntityBuilder<T extends MathEntity> extends JBuilder<T> {

	@Nonnull
	public MathEntityBuilder<T> setName(@Nonnull String name);

	@Nonnull
	public MathEntityBuilder<T> setDescription(@Nullable String description);

	@Nonnull
	public MathEntityBuilder<T> setValue(@Nullable String value);
}
