/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import jscl.math.operator.Operator;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.JBuilder;
import org.solovyev.common.math.MathRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * User: serso
 * Date: 11/19/11
 * Time: 1:48 PM
 */
public class CalculatorPostfixFunctionsRegistry extends AbstractCalculatorMathRegistry<Operator, MathPersistenceEntity> {

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

	public CalculatorPostfixFunctionsRegistry(@NotNull MathRegistry<Operator> functionsRegistry,
											  @NotNull MathEntityDao<MathPersistenceEntity> mathEntityDao) {
		super(functionsRegistry, POSTFIX_FUNCTION_DESCRIPTION_PREFIX, mathEntityDao);
	}


	@NotNull
	@Override
	protected Map<String, String> getSubstitutes() {
		return substitutes;
	}

	@Override
	public String getCategory(@NotNull Operator operator) {
		for (OperatorCategory category : OperatorCategory.values()) {
			if (category.isInCategory(operator)) {
				return category.name();
			}
		}
		return null;
	}

	@Override
	public void load() {
		// not supported yet
	}

	@NotNull
	@Override
	protected JBuilder<? extends Operator> createBuilder(@NotNull MathPersistenceEntity entity) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void save() {
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
