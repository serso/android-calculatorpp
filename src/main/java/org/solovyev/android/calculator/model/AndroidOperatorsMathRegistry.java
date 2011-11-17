/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import org.jetbrains.annotations.NotNull;
import org.solovyev.common.math.MathEntity;
import org.solovyev.common.math.MathRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * User: serso
 * Date: 11/17/11
 * Time: 11:29 PM
 */
public class AndroidOperatorsMathRegistry<T extends MathEntity> extends AndroidMathRegistryImpl<T> {

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

	protected AndroidOperatorsMathRegistry(@NotNull MathRegistry<T> functionsRegistry) {
		super(functionsRegistry, OPERATOR_DESCRIPTION_PREFIX);
	}

	@NotNull
	@Override
	protected Map<String, String> getSubstitutes() {
		return substitutes;
	}
}
