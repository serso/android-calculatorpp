/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.math.MathEntityComparator;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.common.utils.CollectionsUtils;
import org.solovyev.common.utils.Finder;

import java.io.StringWriter;
import java.util.*;

/**
 * User: serso
 * Date: 9/29/11
 * Time: 4:57 PM
 */
class VarsRegisterImpl implements VarsRegister {

	@NotNull
	private final List<Var> vars = new ArrayList<Var>();

	@NotNull
	private final List<Var> systemVars = new ArrayList<Var>();

	protected VarsRegisterImpl() {
	}

	@Override
	@NotNull
	public List<Var> getVars() {
		return Collections.unmodifiableList(vars);
	}

	@Override
	@NotNull
	public List<Var> getSystemVars() {
		return Collections.unmodifiableList(systemVars);
	}

	@Override
	public Var addVar(@Nullable String name, @NotNull Var.Builder builder) {
		final Var var = builder.create();

		Var varFromRegister = getVar(name == null ? var.getName() : name);
		if (varFromRegister == null) {
			varFromRegister = var;
			vars.add(var);
		} else {
			varFromRegister.copy(var);
		}

		return varFromRegister;
	}

	@Override
	public void remove(@NotNull Var var) {
		this.vars.remove(var);
	}

	@Override
	@NotNull
	public List<String> getVarNames() {
		final List<String> result = new ArrayList<String>();

		for (Var var : vars) {
			result.add(var.getName());
		}

		Collections.sort(result, new MathEntityComparator());

		return result;
	}

	@Override
	@Nullable
	public Var getVar(@NotNull final String name) {
		return CollectionsUtils.get(vars, new Finder<Var>() {
			@Override
			public boolean isFound(@Nullable Var var) {
				return var != null && name.equals(var.getName());
			}
		});
	}

	@Override
	public boolean contains(@NotNull final String name) {
		return CollectionsUtils.get(vars, new Finder<Var>() {
			@Override
			public boolean isFound(@Nullable Var var) {
				return var != null && name.equals(var.getName());
			}
		}) != null;
	}

	public void merge(@NotNull final List<Var> varsParam) {
		final Set<Var> result = new HashSet<Var>(varsParam);

		for (Var systemVar : systemVars) {
			if (!result.contains(systemVar)) {
				result.add(systemVar);
			}
		}

		vars.clear();
		vars.addAll(result);
	}

	synchronized void init(@Nullable Context context, @Nullable SharedPreferences preferences) {

		this.vars.clear();
		this.systemVars.clear();

		if (context != null && preferences != null) {
			final String value = preferences.getString(context.getString(R.string.p_calc_vars), null);
			if (value != null) {
				final Serializer serializer = new Persister();
				try {
					final Vars vars = serializer.read(Vars.class, value);
					this.vars.addAll(vars.getVars());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}


		for (String systemVarName : MathType.constants) {

			final Var.Builder builder;
			final Integer varDescription;

			if (systemVarName.equals(MathType.E)) {
				builder = new Var.Builder(systemVarName, Math.E);
				varDescription = R.string.c_e_description;
			} else if (systemVarName.equals(MathType.PI)) {
				builder = new Var.Builder(systemVarName, Math.PI);
				varDescription = R.string.c_pi_description;
			} else if (systemVarName.equals(MathType.IMAGINARY_NUMBER)) {
				builder = new Var.Builder(systemVarName, MathType.IMAGINARY_NUMBER_DEF);
				varDescription = R.string.c_i_description;
			} else if (systemVarName.equals(MathType.NAN)) {
				builder = new Var.Builder(systemVarName, MathType.NAN);
				varDescription = R.string.c_nan_description;
			} else if (systemVarName.equals(MathType.INFINITY)) {
				builder = new Var.Builder(systemVarName, MathType.INFINITY_DEF);
				varDescription = R.string.c_infinity_description;
			} else {
				throw new IllegalArgumentException(systemVarName + " is not supported yet!");
			}

			builder.setSystem(true);

			if (context != null) {
				builder.setDescription(context.getString(varDescription));
			}

			final Var systemVar = builder.create();

			systemVars.add(systemVar);
			if (!vars.contains(systemVar)) {
				vars.add(systemVar);
			}
		}

		/*Log.d(VarsRegister.class.getName(), vars.size() + " variables registered!");
		for (Var var : vars) {
			Log.d(VarsRegister.class.getName(), var.toString());
		}*/
	}

	@Override
	public synchronized void save(@NotNull Context context) {
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		final SharedPreferences.Editor editor = settings.edit();

		final Vars vars = new Vars();
		for (Var var : this.vars) {
			if (!var.isSystem()) {
				vars.getVars().add(var);
			}
		}

		final StringWriter sw = new StringWriter();
		final Serializer serializer = new Persister();
		try {
			serializer.write(vars, sw);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		editor.putString(context.getString(R.string.p_calc_vars), sw.toString());

		editor.commit();
	}
}
