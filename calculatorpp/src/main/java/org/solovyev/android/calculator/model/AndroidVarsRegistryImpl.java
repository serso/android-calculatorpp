/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import android.content.Context;
import android.content.SharedPreferences;
import jscl.math.function.IConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.R;
import org.solovyev.common.JBuilder;
import org.solovyev.common.math.MathRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * User: serso
 * Date: 9/29/11
 * Time: 4:57 PM
 */
class AndroidVarsRegistryImpl extends AbstractAndroidMathRegistry<IConstant, Var> {

	@NotNull
	private static final Map<String, String> substitutes = new HashMap<String, String>();
	static {
		substitutes.put("π", "pi");
		substitutes.put("Π", "PI");
		substitutes.put("∞", "inf");
		substitutes.put("h", "h_reduced");
		substitutes.put("NaN", "nan");
	}

	protected AndroidVarsRegistryImpl(@NotNull MathRegistry<IConstant> mathRegistry) {
		super(mathRegistry, "c_var_description_");
	}

	@NotNull
	@Override
	protected Map<String, String> getSubstitutes() {
		return substitutes;
	}

	public synchronized void load(@Nullable Context context, @Nullable SharedPreferences preferences) {
		super.load(context, preferences);

		tryToAddAuxVar("x");
		tryToAddAuxVar("y");
		tryToAddAuxVar("t");
		tryToAddAuxVar("j");


		/*Log.d(AndroidVarsRegistry.class.getName(), vars.size() + " variables registered!");
		for (Var var : vars) {
			Log.d(AndroidVarsRegistry.class.getName(), var.toString());
		}*/
	}


	@NotNull
	@Override
	protected JBuilder<? extends IConstant> createBuilder(@NotNull Var entity) {
		return new Var.Builder(entity);
	}

	@NotNull
	@Override
	protected Class<? extends MathEntityPersistenceContainer<Var>> getPersistenceContainerClass() {
		return Vars.class;
	}

	@NotNull
	@Override
	protected MathEntityPersistenceContainer<Var> createPersistenceContainer() {
		return new Vars();
	}

	@NotNull
	protected Integer getPreferenceStringId() {
		return R.string.p_calc_vars;
	}

	private void tryToAddAuxVar(@NotNull String name) {
		if ( !contains(name) ) {
			add(new Var.Builder(name, (String)null));
		}
	}

	@NotNull
	@Override
	protected Var transform(@NotNull IConstant entity) {
		if (entity instanceof Var) {
			return (Var) entity;
		} else {
			return new Var.Builder(entity).create();
		}
	}

	@Override
    public String getDescription(@NotNull Context context, @NotNull String mathEntityName) {
        final IConstant var = get(mathEntityName);
        if (var != null && !var.isSystem()) {
            return var.getDescription();
        } else {
            return super.getDescription(context, mathEntityName);
        }
    }

    @Override
    public String getCategory(@NotNull IConstant var) {
        for (VarCategory category : VarCategory.values()) {
            if ( category.isInCategory(var) ) {
                return category.name();
            }
        }

        return null;
    }
}
