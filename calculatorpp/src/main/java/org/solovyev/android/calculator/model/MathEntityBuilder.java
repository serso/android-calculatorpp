/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.JBuilder;
import org.solovyev.common.math.MathEntity;

/**
* User: serso
* Date: 12/22/11
* Time: 9:21 PM
*/
public interface MathEntityBuilder<T extends MathEntity> extends JBuilder<T> {

	@NotNull
	public MathEntityBuilder<T> setName(@NotNull String name);

	@NotNull
	public MathEntityBuilder<T> setDescription(@Nullable String description);

	@NotNull
	public MathEntityBuilder<T> setValue(@Nullable String value);
}
