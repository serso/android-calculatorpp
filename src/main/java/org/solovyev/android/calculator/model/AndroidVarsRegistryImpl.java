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
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.common.math.AbstractMathRegistry;

import java.io.StringWriter;

/**
 * User: serso
 * Date: 9/29/11
 * Time: 4:57 PM
 */
class AndroidVarsRegistryImpl extends AbstractMathRegistry<Var> implements AndroidVarsRegistry {

	protected AndroidVarsRegistryImpl() {
	}

/*	public void merge(@NotNull final List<Var> varsParam) {
		final Set<Var> result = new HashSet<Var>(varsParam);

		for (Var systemVar : systemEntities) {
			if (!result.contains(systemVar)) {
				result.add(systemVar);
			}
		}

		entities.clear();
		entities.addAll(result);
	}*/

	public synchronized void init(@Nullable Context context, @Nullable SharedPreferences preferences) {

		this.entities.clear();
		this.systemEntities.clear();

		if (context != null && preferences != null) {
			final String value = preferences.getString(context.getString(R.string.p_calc_vars), null);
			if (value != null) {
				final Serializer serializer = new Persister();
				try {
					final Vars vars = serializer.read(Vars.class, value);
					this.entities.addAll(vars.getVars());
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

			final Var systemVar = builder.create();

			systemEntities.add(systemVar);
			if (!entities.contains(systemVar)) {
				entities.add(systemVar);
			}
		}

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
		for (Var var : this.entities) {
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
