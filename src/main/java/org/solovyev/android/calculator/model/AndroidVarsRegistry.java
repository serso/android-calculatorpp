/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import android.content.Context;
import android.content.SharedPreferences;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.math.MathRegistry;

/**
 * User: serso
 * Date: 10/6/11
 * Time: 9:31 PM
 */
public interface AndroidVarsRegistry extends MathRegistry<Var>{

	void init(@Nullable Context context, @Nullable SharedPreferences preferences);

	void save(@NotNull Context context);
}
