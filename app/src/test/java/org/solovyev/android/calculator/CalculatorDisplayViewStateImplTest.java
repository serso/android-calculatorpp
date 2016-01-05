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

import org.junit.Test;
import org.solovyev.android.calculator.jscl.JsclOperation;

import jscl.math.Expression;

/**
 * User: serso
 * Date: 10/20/12
 * Time: 12:24 PM
 */
public class CalculatorDisplayViewStateImplTest {

    @Test
    public void testSerializable() throws Exception {
        CalculatorTestUtils.testSerialization(CalculatorDisplayViewStateImpl.newValidState(JsclOperation.numeric, null, "test", 3));
        CalculatorTestUtils.testSerialization(CalculatorDisplayViewStateImpl.newValidState(JsclOperation.numeric, Expression.valueOf("3"), "test", 3));
        CalculatorTestUtils.testSerialization(CalculatorDisplayViewStateImpl.newValidState(JsclOperation.simplify, Expression.valueOf("3+3"), "test", 3));
        CalculatorTestUtils.testSerialization(CalculatorDisplayViewStateImpl.newDefaultInstance());
        CalculatorTestUtils.testSerialization(CalculatorDisplayViewStateImpl.newErrorState(JsclOperation.numeric, "ertert"));
    }

}
