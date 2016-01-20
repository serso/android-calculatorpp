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

import org.solovyev.android.calculator.model.EntityDao;
import org.solovyev.common.JBuilder;
import org.solovyev.common.math.MathRegistry;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import jscl.math.operator.Operator;

public class OperatorsRegistry extends BaseEntitiesRegistry<Operator, PersistedEntity> {

    @Nonnull
    private static final Map<String, String> substitutes = new HashMap<String, String>();
    @Nonnull
    private static final String OPERATOR_DESCRIPTION_PREFIX = "c_op_description_";

    static {
        substitutes.put("Σ", "sum");
        substitutes.put("∏", "product");
        substitutes.put("∂", "derivative");
        substitutes.put("∫ab", "integral_ab");
        substitutes.put("∫", "integral");
        substitutes.put("Σ", "sum");
    }

    public OperatorsRegistry(@Nonnull MathRegistry<Operator> functionsRegistry,
                             @Nonnull EntityDao<PersistedEntity> entityDao) {
        super(functionsRegistry, OPERATOR_DESCRIPTION_PREFIX, entityDao);
    }

    @Nonnull
    @Override
    protected Map<String, String> getSubstitutes() {
        return substitutes;
    }

    @Override
    public String getCategory(@Nonnull Operator operator) {
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

    @Nonnull
    @Override
    protected JBuilder<? extends Operator> createBuilder(@Nonnull PersistedEntity entity) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void save() {
        // not supported yet
    }

    @Override
    protected PersistedEntity transform(@Nonnull Operator entity) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nonnull
    @Override
    protected PersistedEntitiesContainer<PersistedEntity> createPersistenceContainer() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
