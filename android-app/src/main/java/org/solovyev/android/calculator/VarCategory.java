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

import org.solovyev.common.collections.Collections;

import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;

import jscl.math.function.IConstant;

/**
 * User: serso
 * Date: 12/22/11
 * Time: 4:25 PM
 */
public enum VarCategory {

    system(100) {
        @Override
        public boolean isInCategory(@Nonnull IConstant var) {
            return var.isSystem();
        }
    },

    my(0) {
        @Override
        public boolean isInCategory(@Nonnull IConstant var) {
            return !var.isSystem();
        }
    };

    private final int tabOrder;

    VarCategory(int tabOrder) {
        this.tabOrder = tabOrder;
    }

    @Nonnull
    public static List<VarCategory> getCategoriesByTabOrder() {
        final List<VarCategory> result = Collections.asList(VarCategory.values());

        java.util.Collections.sort(result, new Comparator<VarCategory>() {
            @Override
            public int compare(VarCategory category, VarCategory category1) {
                return category.tabOrder - category1.tabOrder;
            }
        });

        return result;
    }

    public abstract boolean isInCategory(@Nonnull IConstant var);
}
