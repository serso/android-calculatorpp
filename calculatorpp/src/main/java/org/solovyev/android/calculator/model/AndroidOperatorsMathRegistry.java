/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import android.content.Context;
import android.content.SharedPreferences;
import jscl.math.operator.Operator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.common.definitions.IBuilder;
import org.solovyev.common.math.MathRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * User: serso
 * Date: 11/17/11
 * Time: 11:29 PM
 */
public class AndroidOperatorsMathRegistry extends AbstractAndroidMathRegistry<Operator, MathPersistenceEntity> {

	@NotNull
	private static final Map<String, String> substitutes = new HashMap<String, String>();
	static {
		substitutes.put("Σ", "sum");
		substitutes.put("∏", "product");
		substitutes.put("∂", "derivative");
		substitutes.put("∫ab", "integral_ab");
		substitutes.put("∫", "integral");
		substitutes.put("Σ", "sum");
	}

	@NotNull
	private static final String OPERATOR_DESCRIPTION_PREFIX = "c_op_description_";

	protected AndroidOperatorsMathRegistry(@NotNull MathRegistry<Operator> functionsRegistry) {
		super(functionsRegistry, OPERATOR_DESCRIPTION_PREFIX);
	}

	@NotNull
	@Override
	protected Map<String, String> getSubstitutes() {
		return substitutes;
	}

    @Override
    public String getCategory(@NotNull Operator mathEntity) {
        return null;
    }

	@Override
	public void load(@Nullable Context context, @Nullable SharedPreferences preferences) {
		// not supported yet
	}

	@NotNull
	@Override
	protected IBuilder<? extends Operator> createBuilder(@NotNull MathPersistenceEntity entity) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@NotNull
	@Override
	protected Class<? extends MathEntityPersistenceContainer<MathPersistenceEntity>> getPersistenceContainerClass() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	protected Integer getPreferenceStringId() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void save(@NotNull Context context) {
		// not supported yet
	}

	@Override
	protected MathPersistenceEntity transform(@NotNull Operator entity) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@NotNull
	@Override
	protected MathEntityPersistenceContainer<MathPersistenceEntity> createPersistenceContainer() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
