/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import jscl.math.operator.*;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.JBuilder;
import org.solovyev.common.math.MathRegistry;

import java.util.*;

/**
 * User: serso
 * Date: 11/17/11
 * Time: 11:29 PM
 */
public class CalculatorOperatorsMathRegistry extends AbstractCalculatorMathRegistry<Operator, MathPersistenceEntity> {

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

	public CalculatorOperatorsMathRegistry(@NotNull MathRegistry<Operator> functionsRegistry,
                                           @NotNull MathEntityDao<MathPersistenceEntity> mathEntityDao) {
		super(functionsRegistry, OPERATOR_DESCRIPTION_PREFIX, mathEntityDao);
	}

	@NotNull
	@Override
	protected Map<String, String> getSubstitutes() {
		return substitutes;
	}

    @Override
    public String getCategory(@NotNull Operator operator) {
        for (OperatorCategory category : OperatorCategory.values()) {
            if ( category.isInCategory(operator) ) {
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

    /*
    **********************************************************************
    *
    *                           STATIC
    *
    **********************************************************************
    */

}
