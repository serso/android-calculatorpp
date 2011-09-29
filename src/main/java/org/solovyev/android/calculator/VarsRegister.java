/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.solovyev.android.calculator.math.MathEntityType;
import org.solovyev.common.utils.CollectionsUtils;
import org.solovyev.common.utils.Finder;

import java.io.StringWriter;
import java.util.*;

/**
 * User: serso
 * Date: 9/29/11
 * Time: 4:57 PM
 */
public class VarsRegister {

	@NotNull
	private final List<Var> vars = new ArrayList<Var>();

	@NotNull
	private final List<Var> systemVars = new ArrayList<Var>();

	@NotNull
	public List<Var> getVars() {
		return Collections.unmodifiableList(vars);
	}

	@NotNull
	public List<Var> getSystemVars() {
		return Collections.unmodifiableList(systemVars);
	}

	@Nullable
	public Var getVar(@NotNull final String name) {
		return CollectionsUtils.get(vars, new Finder<Var>() {
			@Override
			public boolean isFound(@Nullable Var var) {
				return var != null && name.equals(var.getName());
			}
		});
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

	public synchronized void load(@Nullable Context context) {

		this.vars.clear();
		this.systemVars.clear();

		Var t = new Var("T", 0d, false);
		t.setDescription("Some text description!");
		this.vars.add(t);

		Var t2 = new Var("T2", 2d, false);
		this.vars.add(t2);

		Var t1 = new Var("T1", 1d, false);
		t1.setDescription("Не так давно в рунете появились упоминания англоязычного проекта DearPhotograph.com. Сайт нас растрогал, зарядил идеей и вдохновением.");
		this.vars.add(t1);

		if (context != null) {
			final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

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


		for (Var systemVar : MathEntityType.constants) {

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

		editor.putString(context.getString(R.string.p_calc_vars),sw.toString());

		editor.commit();
	}
}
