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

import android.support.annotation.NonNull;

import org.solovyev.android.calculator.entities.BaseEntitiesRegistry;
import org.solovyev.android.calculator.entities.Category;
import org.solovyev.android.calculator.entities.Entities;
import org.solovyev.android.calculator.json.Jsonable;
import org.solovyev.android.calculator.operators.OperatorCategory;
import org.solovyev.common.math.MathRegistry;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import jscl.math.operator.Operator;

public class OperatorsRegistry extends BaseEntitiesRegistry<Operator> {

    @Nonnull
    private static final Map<String, String> substitutes = new HashMap<String, String>();

    static {
        substitutes.put("Σ", "sum");
        substitutes.put("∏", "product");
        substitutes.put("∂", "derivative");
        substitutes.put("∫ab", "integral_ab");
        substitutes.put("∫", "integral");
        substitutes.put("Σ", "sum");
    }

    public OperatorsRegistry(@Nonnull MathRegistry<Operator> functionsRegistry) {
        super(functionsRegistry, "c_op_description_");
    }

    @Nonnull
    @Override
    protected Map<String, String> getSubstitutes() {
        return substitutes;
    }

    @Nullable
    @Override
    protected Jsonable toJsonable(@NonNull Operator entity) {
        return null;
    }

    @Nullable
    @Override
    protected File getEntitiesFile() {
        return null;
    }

    @Override
    public Category getCategory(@Nonnull Operator operator) {
        return Entities.getCategory(operator, OperatorCategory.values());
    }
}
