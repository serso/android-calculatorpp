/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import android.content.Context;
import jscl.math.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.RClassUtils;
import org.solovyev.common.definitions.IBuilder;
import org.solovyev.common.math.MathRegistry;

import java.util.List;
import java.util.Map;

/**
 * User: serso
 * Date: 10/30/11
 * Time: 1:03 AM
 */
public class AndroidFunctionsRegistryImpl implements AndroidFunctionsRegistry {

	@NotNull
	private static final String FUNCTION_DESCRIPTION_PREFIX = "c_fun_description_";

	@NotNull
	private final MathRegistry<Function> functionsRegistry;

	public AndroidFunctionsRegistryImpl(@NotNull MathRegistry<Function> functionsRegistry) {
		this.functionsRegistry = functionsRegistry;
	}

	@Nullable
	@Override
	public String getDescription(@NotNull Context context, @NotNull String functionName) {
		final String result;

		final Map<String, Integer> stringsCache = RClassUtils.getCache(R.string.class);

		final Integer stringId;
		if (!functionName.equals("√")) {
			stringId = stringsCache.get(FUNCTION_DESCRIPTION_PREFIX + functionName);
		} else {
			// todo serso: think
			stringId = stringsCache.get(FUNCTION_DESCRIPTION_PREFIX + "sqrt");
		}
		if (stringId != null) {
			result = context.getString(stringId);
		} else {
			result = null;
		}

		return result;
	}

	@NotNull
	@Override
	public List<Function> getEntities() {
		return functionsRegistry.getEntities();
	}

	@NotNull
	@Override
	public List<Function> getSystemEntities() {
		return functionsRegistry.getSystemEntities();
	}

	@Override
	public Function add(@NotNull IBuilder<? extends Function> IBuilder) {
		return functionsRegistry.add(IBuilder);
	}

	@Override
	public void remove(@NotNull Function var) {
		functionsRegistry.remove(var);
	}

	@NotNull
	@Override
	public List<String> getNames() {
		return functionsRegistry.getNames();
	}

	@Override
	public boolean contains(@NotNull String name) {
		return functionsRegistry.contains(name);
	}

	@Override
	public Function get(@NotNull String name) {
		return functionsRegistry.get(name);
	}

	@Override
	public Function getById(@NotNull Integer id) {
		return functionsRegistry.getById(id);
	}
}
