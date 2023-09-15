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

package org.solovyev.android.calculator.variables;

import androidx.annotation.StringRes;
import jscl.math.function.IConstant;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.entities.Category;

import javax.annotation.Nonnull;

public enum VariableCategory implements Category<IConstant> {

    my(R.string.c_var_my) {
        @Override
        public boolean isInCategory(@Nonnull IConstant variable) {
            return !variable.isSystem();
        }
    },

    system(R.string.c_var_system) {
        @Override
        public boolean isInCategory(@Nonnull IConstant variable) {
            return variable.isSystem();
        }
    };

    @StringRes
    private final int title;

    VariableCategory(@StringRes int title) {
        this.title = title;
    }

    @Override
    public int title() {
        return title;
    }
}
