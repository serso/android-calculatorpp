package org.solovyev.android.calculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.solovyev.android.calculator.math.MathEntityType;
import org.solovyev.android.view.widgets.SimpleOnDragListener;
import org.solovyev.common.utils.CollectionsUtils;
import org.solovyev.common.utils.Finder;

import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: serso
 * Date: 9/29/11
 * Time: 4:57 PM
 */
public class VarsRegister {

	@NotNull
	private final Set<Var> vars = new HashSet<Var>();

	@NotNull
	private final Set<Var> systemVars = new HashSet<Var>();

	@NotNull
	public Set<Var> getVars() {
		return Collections.unmodifiableSet(vars);
	}

	@NotNull
	public Set<Var> getSystemVars() {
		return Collections.unmodifiableSet(systemVars);
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

	public synchronized void load(@NotNull Context context) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

		this.vars.clear();
		this.systemVars.clear();

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


		for (Var systemVar : MathEntityType.constants) {

			systemVars.add(systemVar);
			if (!vars.contains(systemVar)) {
				vars.add(systemVar);
			}
		}
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
