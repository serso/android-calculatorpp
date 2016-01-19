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

import jscl.math.operator.Operator;
import org.solovyev.common.JBuilder;
import org.solovyev.common.math.MathRegistry;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class PostfixFunctionsRegistry extends BaseEntitiesRegistry<Operator, MathPersistenceEntity> {

    @Nonnull
    private static final Map<String, String> substitutes = new HashMap<String, String>();
    @Nonnull
    private static final String POSTFIX_FUNCTION_DESCRIPTION_PREFIX = "c_pf_description_";

    static {
        substitutes.put("%", "percent");
        substitutes.put("!", "factorial");
        substitutes.put("!!", "double_factorial");
        substitutes.put("°", "degree");
    }

    public PostfixFunctionsRegistry(@Nonnull MathRegistry<Operator> functionsRegistry,
                                    @Nonnull MathEntityDao<MathPersistenceEntity> mathEntityDao) {
        super(functionsRegistry, POSTFIX_FUNCTION_DESCRIPTION_PREFIX, mathEntityDao);
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
    protected JBuilder<? extends Operator> createBuilder(@Nonnull MathPersistenceEntity entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save() {
        // not supported yet
    }

    @Override
    protected MathPersistenceEntity transform(@Nonnull Operator entity) {
        return null;
    }

    @Nonnull
    @Override
    protected MathEntityPersistenceContainer<MathPersistenceEntity> createPersistenceContainer() {
        throw new UnsupportedOperationException();
    }
}
