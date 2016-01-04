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

package org.solovyev.android.calculator.function;

import org.solovyev.android.calculator.model.AFunction;
import org.solovyev.android.calculator.model.MathEntityBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import jscl.CustomFunctionCalculationException;
import jscl.math.function.CustomFunction;
import jscl.math.function.Function;

/**
 * User: serso
 * Date: 1/20/13
 * Time: 12:21 PM
 */
public final class FunctionBuilderAdapter implements MathEntityBuilder<Function> {

    @Nonnull
    private final AFunction.Builder nestedBuilder;

    public FunctionBuilderAdapter(@Nonnull AFunction.Builder nestedBuilder) {
        this.nestedBuilder = nestedBuilder;
    }

    @Nonnull
    @Override
    public MathEntityBuilder<Function> setName(@Nonnull String name) {
        nestedBuilder.setName(name);
        return this;
    }

    @Nonnull
    @Override
    public MathEntityBuilder<Function> setDescription(@Nullable String description) {
        nestedBuilder.setDescription(description);
        return this;
    }

    @Nonnull
    @Override
    public MathEntityBuilder<Function> setValue(@Nullable String value) {
        nestedBuilder.setValue(value);
        return this;
    }

    @Nonnull
    @Override
    public Function create() throws CustomFunctionCalculationException, AFunction.Builder.CreationException {
        final AFunction function = nestedBuilder.create();
        return new CustomFunction.Builder(function).create();
    }
}
