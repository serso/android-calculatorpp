/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import jscl.math.operator.Operator;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.math.MathRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * User: serso
 * Date: 11/19/11
 * Time: 1:48 PM
 */
public class AndroidPostfixFunctionsRegistry extends AndroidMathRegistryImpl<Operator> {

	@NotNull
	private static final Map<String, String> substitutes = new HashMap<String, String>();
	static {
		substitutes.put("%", "percent");
		substitutes.put("!", "factorial");
		substitutes.put("!!", "double_factorial");
		substitutes.put("°", "degree");
	}

	@NotNull
	private static final String POSTFIX_FUNCTION_DESCRIPTION_PREFIX = "c_pf_description_";

	protected AndroidPostfixFunctionsRegistry(@NotNull MathRegistry<Operator> functionsRegistry) {
		super(functionsRegistry, POSTFIX_FUNCTION_DESCRIPTION_PREFIX);
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
}
