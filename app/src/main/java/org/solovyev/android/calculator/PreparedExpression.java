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

import java.util.List;

import javax.annotation.Nonnull;

import jscl.math.function.IConstant;

/**
 * User: serso
 * Date: 10/18/11
 * Time: 10:07 PM
 */
public class PreparedExpression implements CharSequence {

    @Nonnull
    private String expression;

    @Nonnull
    private List<IConstant> undefinedVars;

    public PreparedExpression(@Nonnull String expression, @Nonnull List<IConstant> undefinedVars) {
        this.expression = expression;
        this.undefinedVars = undefinedVars;
    }

    @Nonnull
    public String getExpression() {
        return expression;
    }

    public boolean isExistsUndefinedVar() {
        return !this.undefinedVars.isEmpty();
    }

    @Nonnull
    public List<IConstant> getUndefinedVars() {
        return undefinedVars;
    }

    @Override
    public int length() {
        return expression.length();
    }

    @Override
    public char charAt(int i) {
        return expression.charAt(i);
    }

    @Override
    public CharSequence subSequence(int i, int i1) {
        return expression.subSequence(i, i1);
    }

    @Override
    public String toString() {
        return this.expression;
    }
}
