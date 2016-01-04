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

import org.solovyev.android.calculator.jscl.JsclOperation;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 7:26 PM
 */
public class CalculatorInputImpl implements CalculatorInput {

    @Nonnull
    private String expression;

    @Nonnull
    private JsclOperation operation;

    public CalculatorInputImpl(@Nonnull String expression, @Nonnull JsclOperation operation) {
        this.expression = expression;
        this.operation = operation;
    }

    @Override
    @Nonnull
    public String getExpression() {
        return expression;
    }

    @Override
    @Nonnull
    public JsclOperation getOperation() {
        return operation;
    }
}
