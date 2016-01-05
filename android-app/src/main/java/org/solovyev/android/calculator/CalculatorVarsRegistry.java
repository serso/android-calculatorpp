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

import org.solovyev.android.calculator.model.MathEntityBuilder;
import org.solovyev.android.calculator.model.Var;
import org.solovyev.android.calculator.model.Vars;
import org.solovyev.common.JBuilder;
import org.solovyev.common.math.MathEntity;
import org.solovyev.common.math.MathRegistry;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import jscl.math.function.IConstant;

/**
 * User: serso
 * Date: 9/29/11
 * Time: 4:57 PM
 */
public class CalculatorVarsRegistry extends AbstractCalculatorMathRegistry<IConstant, Var> {

    @Nonnull
    public static final String ANS = "ans";

    @Nonnull
    private static final Map<String, String> substitutes = new HashMap<String, String>();

    static {
        substitutes.put("π", "pi");
        substitutes.put("Π", "PI");
        substitutes.put("∞", "inf");
        substitutes.put("h", "h_reduced");
        substitutes.put("NaN", "nan");
    }

    public CalculatorVarsRegistry(@Nonnull MathRegistry<IConstant> mathRegistry,
                                  @Nonnull MathEntityDao<Var> mathEntityDao) {
        super(mathRegistry, "c_var_description_", mathEntityDao);
    }

    public static <T extends MathEntity> void saveVariable(@Nonnull CalculatorMathRegistry<T> registry,
                                                           @Nonnull MathEntityBuilder<? extends T> builder,
                                                           @Nullable T editedInstance,
                                                           @Nonnull Object source, boolean save) {
        final T addedVar = registry.add(builder);

        if (save) {
            registry.save();
        }

        if (editedInstance == null) {
            Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.constant_added, addedVar, source);
        } else {
            Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.constant_changed, ChangeImpl.newInstance(editedInstance, addedVar), source);
        }
    }

    @Nonnull
    @Override
    protected Map<String, String> getSubstitutes() {
        return substitutes;
    }

    public synchronized void load() {
        super.load();

        tryToAddAuxVar("x");
        tryToAddAuxVar("y");
        tryToAddAuxVar("t");
        tryToAddAuxVar("j");


		/*Log.d(AndroidVarsRegistry.class.getName(), vars.size() + " variables registered!");
        for (Var var : vars) {
			Log.d(AndroidVarsRegistry.class.getName(), var.toString());
		}*/
    }


    @Nonnull
    @Override
    protected JBuilder<? extends IConstant> createBuilder(@Nonnull Var entity) {
        return new Var.Builder(entity);
    }

    @Nonnull
    @Override
    protected MathEntityPersistenceContainer<Var> createPersistenceContainer() {
        return new Vars();
    }

    private void tryToAddAuxVar(@Nonnull String name) {
        if (!contains(name)) {
            add(new Var.Builder(name, (String) null));
        }
    }

    @Nonnull
    @Override
    protected Var transform(@Nonnull IConstant entity) {
        if (entity instanceof Var) {
            return (Var) entity;
        } else {
            return new Var.Builder(entity).create();
        }
    }

    @Override
    public String getDescription(@Nonnull String mathEntityName) {
        final IConstant var = get(mathEntityName);
        if (var != null && !var.isSystem()) {
            return var.getDescription();
        } else {
            return super.getDescription(mathEntityName);
        }
    }

    @Override
    public String getCategory(@Nonnull IConstant var) {
        for (VarCategory category : VarCategory.values()) {
            if (category.isInCategory(var)) {
                return category.name();
            }
        }

        return null;
    }
}
