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
import jscl.math.function.IConstant;

import javax.annotation.Nonnull;
import java.util.List;

public class PreparedExpression implements CharSequence {

    @Nonnull
    public final String value;

    @Nonnull
    public final List<IConstant> undefinedVariables;

    public PreparedExpression(@Nonnull String value, @Nonnull List<IConstant> undefinedVariables) {
        this.value = value;
        this.undefinedVariables = undefinedVariables;
    }

    @Nonnull
    public String getValue() {
        return value;
    }

    public boolean hasUndefinedVariables() {
        return !undefinedVariables.isEmpty();
    }

    @Override
    public int length() {
        return value.length();
    }

    @Override
    public char charAt(int i) {
        return value.charAt(i);
    }

    @Override
    public CharSequence subSequence(int i, int i1) {
        return value.subSequence(i, i1);
    }

    @NonNull
    @Override
    public String toString() {
        return this.value;
    }
}
