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

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import jscl.math.Generic;
import jscl.math.function.Constant;
import jscl.math.function.IConstant;

/**
 * User: serso
 * Date: 9/22/12
 * Time: 7:13 PM
 */
public final class CalculatorUtils {

    static final long FIRST_ID = 0;

    private CalculatorUtils() {
        throw new AssertionError();
    }

    @Nonnull
    public static CalculatorEventData createFirstEventDataId() {
        return CalculatorEventDataImpl.newInstance(FIRST_ID, FIRST_ID);
    }

    @Nonnull
    public static Set<Constant> getNotSystemConstants(@Nonnull Generic expression) {
        final Set<Constant> notSystemConstants = new HashSet<Constant>();

        for (Constant constant : expression.getConstants()) {
            IConstant var = Locator.getInstance().getEngine().getVarsRegistry().get(constant.getName());
            if (var != null && !var.isSystem() && !var.isDefined()) {
                notSystemConstants.add(constant);
            }
        }

        return notSystemConstants;
    }

}
