/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import android.content.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * User: serso
 * Date: 10/6/11
 * Time: 9:31 PM
 */
public interface VarsRegister {

	@NotNull
	List<Var> getVars();

	@NotNull
	List<Var> getSystemVars();

	Var addVar(@Nullable String name, @NotNull Var.Builder builder);

	void remove(@NotNull Var var);

	@NotNull
	List<String> getVarNames();

	@Nullable
	Var getVar(@NotNull String name);

	boolean contains(@NotNull String name);

	void save(@NotNull Context context);
}
