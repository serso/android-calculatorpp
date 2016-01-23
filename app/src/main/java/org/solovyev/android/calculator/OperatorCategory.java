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

import android.support.annotation.StringRes;
import jscl.math.operator.*;
import org.solovyev.common.collections.Collections;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;

public enum OperatorCategory implements Category {

    derivatives(100, R.string.derivatives) {
        @Override
        public boolean isInCategory(@Nonnull Operator operator) {
            return operator instanceof Derivative || operator instanceof Integral || operator instanceof IndefiniteIntegral;
        }
    },

    other(200, R.string.other) {
        @Override
        public boolean isInCategory(@Nonnull Operator operator) {
            return operator instanceof Sum || operator instanceof Product;
        }
    },

    my(0, R.string.c_fun_category_my) {
        @Override
        public boolean isInCategory(@Nonnull Operator operator) {
            return !operator.isSystem();
        }
    },

    common(50, R.string.c_fun_category_common) {
        @Override
        public boolean isInCategory(@Nonnull Operator operator) {
            for (OperatorCategory category : values()) {
                if (category != this) {
                    if (category.isInCategory(operator)) {
                        return false;
                    }
                }
            }

            return true;
        }
    };

    private final int tabOrder;
    @StringRes
    private final int title;

    OperatorCategory(int tabOrder, @StringRes int title) {
        this.tabOrder = tabOrder;
        this.title = title;
    }

    @Nonnull
    public static List<OperatorCategory> getCategoriesByTabOrder() {
        final List<OperatorCategory> result = Collections.asList(OperatorCategory.values());

        java.util.Collections.sort(result, new Comparator<OperatorCategory>() {
            @Override
            public int compare(OperatorCategory category, OperatorCategory category1) {
                return category.tabOrder - category1.tabOrder;
            }
        });

        // todo serso: current solution (as creating operators is not implemented yet)
        result.remove(my);

        return result;
    }



    public abstract boolean isInCategory(@Nonnull Operator operator);

    @Override
    public int title() {
        return title;
    }
}
