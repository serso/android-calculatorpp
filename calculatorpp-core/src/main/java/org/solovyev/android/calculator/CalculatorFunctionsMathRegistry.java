/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import jscl.CustomFunctionCalculationException;
import jscl.math.function.CustomFunction;
import jscl.math.function.Function;
import jscl.math.function.IFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.model.AFunction;
import org.solovyev.android.calculator.model.Functions;
import org.solovyev.android.calculator.model.MathEntityBuilder;
import org.solovyev.common.JBuilder;
import org.solovyev.common.math.MathRegistry;
import org.solovyev.common.text.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * User: serso
 * Date: 11/17/11
 * Time: 11:28 PM
 */
public class CalculatorFunctionsMathRegistry extends AbstractCalculatorMathRegistry<Function, AFunction> {

    @NotNull
	private static final Map<String, String> substitutes = new HashMap<String, String>();
	static {
		substitutes.put("√", "sqrt");
	}

	@NotNull
	private static final String FUNCTION_DESCRIPTION_PREFIX = "c_fun_description_";

	public CalculatorFunctionsMathRegistry(@NotNull MathRegistry<Function> functionsRegistry,
                                           @NotNull MathEntityDao<AFunction> mathEntityDao) {
		super(functionsRegistry, FUNCTION_DESCRIPTION_PREFIX, mathEntityDao);
	}

	@Override
	public void load() {
		super.load();

		add(new CustomFunction.Builder(true, "log", Arrays.asList("base", "x"), "ln(x)/ln(base)"));
		add(new CustomFunction.Builder(true, "√3", Arrays.asList("x"), "x^(1/3)"));
		add(new CustomFunction.Builder(true, "√4", Arrays.asList("x"), "x^(1/4)"));
		add(new CustomFunction.Builder(true, "√n", Arrays.asList("x", "n"), "x^(1/n)"));
        add(new CustomFunction.Builder(true, "re", Arrays.asList("x"), "(x+conjugate(x))/2"));
        add(new CustomFunction.Builder(true, "im", Arrays.asList("x"), "(x-conjugate(x))/(2*i)"));
    }

	public static void saveFunction(@NotNull CalculatorMathRegistry<Function> registry,
									@NotNull MathEntityBuilder<? extends Function> builder,
									@Nullable IFunction editedInstance,
									@NotNull Object source, boolean save) throws CustomFunctionCalculationException {
		final Function addedFunction = registry.add(builder);

		if (save) {
			registry.save();
		}

		if (editedInstance == null) {
			CalculatorLocatorImpl.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.function_added, addedFunction, source);
		} else {
			CalculatorLocatorImpl.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.function_changed, ChangeImpl.newInstance(editedInstance, addedFunction), source);
		}
	}

	@NotNull
	@Override
	protected Map<String, String> getSubstitutes() {
		return substitutes;
	}

    @Override
    public String getCategory(@NotNull Function function) {
        for (FunctionCategory category : FunctionCategory.values()) {
            if ( category.isInCategory(function) ) {
                return category.name();
            }
        }
        
        return null;
    }

	@Nullable
	@Override
	public String getDescription(@NotNull String functionName) {
		final Function function = get(functionName);

		String result = null;
		if ( function instanceof CustomFunction ) {
			result = ((CustomFunction) function).getDescription();
		}

		if (StringUtils.isEmpty(result) ) {
			result = super.getDescription(functionName);
		}

		return result;

	}

	@NotNull
	@Override
	protected JBuilder<? extends Function> createBuilder(@NotNull AFunction function) {
		return new CustomFunction.Builder(function);
	}

    @Override
	protected AFunction transform(@NotNull Function function) {
		if (function instanceof CustomFunction) {
			return AFunction.fromIFunction((CustomFunction) function);
		} else {
			return null;
		}
	}

	@NotNull
	@Override
	protected MathEntityPersistenceContainer<AFunction> createPersistenceContainer() {
		return new Functions();
	}
}
