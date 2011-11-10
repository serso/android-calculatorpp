/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import jscl.math.function.IConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.common.definitions.IBuilder;
import org.solovyev.common.math.MathRegistry;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 9/29/11
 * Time: 4:57 PM
 */
class AndroidVarsRegistryImpl implements AndroidVarsRegistry {

	@NotNull
	private final MathRegistry<IConstant> mathRegistry;

	protected AndroidVarsRegistryImpl(@NotNull MathRegistry<IConstant> mathRegistry) {
		this.mathRegistry = mathRegistry;
	}

	private boolean initialized = false;

	public synchronized void init(@Nullable Context context, @Nullable SharedPreferences preferences) {

		if (!initialized) {

			if (context != null && preferences != null) {
				final String value = preferences.getString(context.getString(R.string.p_calc_vars), null);
				if (value != null) {
					final Serializer serializer = new Persister();
					try {
						final Vars vars = serializer.read(Vars.class, value);
						for (Var var : vars.getVars()) {
							if (!contains(var.getName())) {
								add(null, new Var.Builder(var));
							}
						}
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
				} else if (systemVarName.equals(MathType.C)) {
					builder = new Var.Builder(systemVarName, MathType.C_VALUE);
					varDescription = R.string.c_c_description;
				} else if (systemVarName.equals(MathType.G)) {
					builder = new Var.Builder(systemVarName, MathType.G_VALUE);
					varDescription = R.string.c_g_description;
					/*			} else if (systemVarName.equals(MathType.H)) {
										builder = new Var.Builder(systemVarName, MathType.H_VALUE);
										varDescription = R.string.c_h_description;*/
				} else if (systemVarName.equals(MathType.H_REDUCED)) {
					builder = new Var.Builder(systemVarName, MathType.H_REDUCED_VALUE);
					varDescription = R.string.c_h_reduced_description;
				} else if (systemVarName.equals(MathType.IMAGINARY_NUMBER)) {
					builder = new Var.Builder(systemVarName, MathType.IMAGINARY_NUMBER_JSCL);
					varDescription = R.string.c_i_description;
				} else if (systemVarName.equals(MathType.NAN)) {
					builder = new Var.Builder(systemVarName, MathType.NAN);
					varDescription = R.string.c_nan_description;
				} else if (systemVarName.equals(MathType.INFINITY)) {
					builder = new Var.Builder(systemVarName, MathType.INFINITY_JSCL);
					varDescription = R.string.c_infinity_description;
				} else {
					throw new IllegalArgumentException(systemVarName + " is not supported yet!");
				}

				builder.setSystem(true);

				if (context != null) {
					builder.setDescription(context.getString(varDescription));
				}

				if (!contains(systemVarName)) {
					add(null, builder);
				}
			}
		}

		initialized = true;

		/*Log.d(AndroidVarsRegistry.class.getName(), vars.size() + " variables registered!");
		for (Var var : vars) {
			Log.d(AndroidVarsRegistry.class.getName(), var.toString());
		}*/
	}

	@Override
	public synchronized void save(@NotNull Context context) {
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		final SharedPreferences.Editor editor = settings.edit();

		final Vars vars = new Vars();
		for (Var var : this.getEntities()) {
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

	@NotNull
	@Override
	public List<Var> getEntities() {
		final List<Var> result = new ArrayList<Var>();
		for (IConstant iConstant : mathRegistry.getEntities()) {
			result.add(transform(iConstant));
		}
		return result;
	}

	@NotNull
	@Override
	public List<Var> getSystemEntities() {
		final List<Var> result = new ArrayList<Var>();
		for (IConstant iConstant : mathRegistry.getSystemEntities()) {
			result.add(transform(iConstant));
		}
		return result;
	}

	@Override
	public Var add(@Nullable String name, @NotNull IBuilder<? extends Var> IBuilder) {
		IConstant result = mathRegistry.add(name, IBuilder);
		if (result instanceof Var) {
			return (Var) result;
		} else if (result != null) {
			return transform(result);
		} else {
			return null;
		}
	}

	@NotNull
	private Var transform(@NotNull IConstant result) {
		return new Var.Builder(result).create();
	}

	@Override
	public void remove(@NotNull Var var) {
		mathRegistry.remove(var);
	}

	@NotNull
	@Override
	public List<String> getNames() {
		return mathRegistry.getNames();
	}

	@Override
	public boolean contains(@NotNull String name) {
		return mathRegistry.contains(name);
	}

	@Override
	public Var get(@NotNull String name) {
		IConstant result = mathRegistry.get(name);
		if (result instanceof Var) {
			return (Var) result;
		} else if (result != null) {
			return transform(result);
		} else {
			return null;
		}
	}

	@Override
	public Var getById(@NotNull Integer id) {
		final IConstant result = mathRegistry.getById(id);
		if (result instanceof Var) {
			return (Var) result;
		} else if (result != null) {
			return transform(result);
		} else {
			return null;
		}
	}
}
