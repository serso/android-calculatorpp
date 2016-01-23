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
import jscl.math.function.ArcTrigonometric;
import jscl.math.function.Comparison;
import jscl.math.function.Function;
import jscl.math.function.Trigonometric;
import org.solovyev.common.collections.Collections;

import javax.annotation.Nonnull;
import java.util.*;

public enum FunctionCategory implements Category {

    trigonometric(100, R.string.c_fun_category_trig) {
        @Override
        public boolean isInCategory(@Nonnull Function function) {
            return (function instanceof Trigonometric || function instanceof ArcTrigonometric) && !hyperbolic_trigonometric.isInCategory(function);
        }
    },

    hyperbolic_trigonometric(300, R.string.c_fun_category_hyper_trig) {

        private final Set<String> names = new HashSet<>(Arrays.asList("sinh", "cosh", "tanh", "coth", "asinh", "acosh", "atanh", "acoth"));

        @Override
        public boolean isInCategory(@Nonnull Function function) {
            return names.contains(function.getName());
        }
    },

    comparison(200, R.string.c_fun_category_comparison) {
        @Override
        public boolean isInCategory(@Nonnull Function function) {
            return function instanceof Comparison;
        }
    },

    my(0, R.string.c_fun_category_my) {
        @Override
        public boolean isInCategory(@Nonnull Function function) {
            return !function.isSystem();
        }
    },

    common(50, R.string.c_fun_category_common) {
        @Override
        public boolean isInCategory(@Nonnull Function function) {
            for (FunctionCategory category : values()) {
                if (category != this) {
                    if (category.isInCategory(function)) {
                        return false;
                    }
                }
            }

            return true;
        }
    };

    public final int tabOrder;
    public final int title;

    FunctionCategory(int tabOrder, @StringRes int title) {
        this.tabOrder = tabOrder;
        this.title = title;
    }

    @Nonnull
    public static List<FunctionCategory> getCategoriesByTabOrder() {
        final List<FunctionCategory> result = Collections.asList(FunctionCategory.values());

        java.util.Collections.sort(result, new Comparator<FunctionCategory>() {
            @Override
            public int compare(FunctionCategory category, FunctionCategory category1) {
                return category.tabOrder - category1.tabOrder;
            }
        });

        return result;
    }

    @Override
    public int title() {
        return title;
    }

    public abstract boolean isInCategory(@Nonnull Function function);
}
