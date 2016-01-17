/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import jscl.CustomFunctionCalculationException;
import jscl.math.function.CustomFunction;
import jscl.math.function.Function;
import jscl.math.function.IFunction;
import org.solovyev.android.calculator.function.FunctionBuilderAdapter;
import org.solovyev.android.calculator.model.AFunction;
import org.solovyev.android.calculator.model.Functions;
import org.solovyev.android.calculator.model.MathEntityBuilder;
import org.solovyev.common.JBuilder;
import org.solovyev.common.math.MathRegistry;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FunctionsRegistry extends BaseEntitiesRegistry<Function, AFunction> {

    @Nonnull
    private static final Map<String, String> substitutes = new HashMap<String, String>();
    @Nonnull
    private static final String FUNCTION_DESCRIPTION_PREFIX = "c_fun_description_";

    static {
        substitutes.put("√", "sqrt");
    }

    public FunctionsRegistry(@Nonnull MathRegistry<Function> functionsRegistry,
                             @Nonnull MathEntityDao<AFunction> mathEntityDao) {
        super(functionsRegistry, FUNCTION_DESCRIPTION_PREFIX, mathEntityDao);
    }

    public static void saveFunction(@Nonnull EntitiesRegistry<Function> registry,
                                    @Nonnull MathEntityBuilder<? extends Function> builder,
                                    @Nullable IFunction editedInstance,
                                    @Nonnull Object source, boolean save) throws CustomFunctionCalculationException, AFunction.Builder.CreationException {
        final Function addedFunction = registry.add(builder);

        if (save) {
            registry.save();
        }

        if (editedInstance == null) {
            Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.function_added, addedFunction, source);
        } else {
            Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.function_changed, ChangeImpl.newInstance(editedInstance, addedFunction), source);
        }
    }

    @Override
    public void load() {
        add(new CustomFunction.Builder(true, "log", Arrays.asList("base", "x"), "ln(x)/ln(base)"));
        add(new CustomFunction.Builder(true, "√3", Arrays.asList("x"), "x^(1/3)"));
        add(new CustomFunction.Builder(true, "√4", Arrays.asList("x"), "x^(1/4)"));
        add(new CustomFunction.Builder(true, "√n", Arrays.asList("x", "n"), "x^(1/n)"));
        add(new CustomFunction.Builder(true, "re", Arrays.asList("x"), "(x+conjugate(x))/2"));
        add(new CustomFunction.Builder(true, "im", Arrays.asList("x"), "(x-conjugate(x))/(2*i)"));

        super.load();
    }

    @Nonnull
    @Override
    protected Map<String, String> getSubstitutes() {
        return substitutes;
    }

    @Override
    public String getCategory(@Nonnull Function function) {
        for (FunctionCategory category : FunctionCategory.values()) {
            if (category.isInCategory(function)) {
                return category.name();
            }
        }

        return null;
    }

    @Nullable
    @Override
    public String getDescription(@Nonnull String name) {
        final Function function = get(name);

        String result = null;
        if (function instanceof CustomFunction) {
            result = ((CustomFunction) function).getDescription();
        }

        if (Strings.isEmpty(result)) {
            result = super.getDescription(name);
        }

        return result;

    }

    @Nonnull
    @Override
    protected JBuilder<? extends Function> createBuilder(@Nonnull AFunction function) {
        return new FunctionBuilderAdapter(new AFunction.Builder(function));
    }

    @Override
    protected AFunction transform(@Nonnull Function function) {
        if (function instanceof CustomFunction) {
            return AFunction.fromIFunction((CustomFunction) function);
        } else {
            return null;
        }
    }

    @Nonnull
    @Override
    protected MathEntityPersistenceContainer<AFunction> createPersistenceContainer() {
        return new Functions();
    }
}
