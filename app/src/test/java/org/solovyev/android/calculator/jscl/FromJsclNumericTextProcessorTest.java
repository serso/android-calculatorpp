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

package org.solovyev.android.calculator.jscl;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.solovyev.android.calculator.AbstractCalculatorTest;
import org.solovyev.android.calculator.CalculatorTestUtils;

import jscl.AngleUnit;
import jscl.JsclMathEngine;
import jscl.math.Expression;
import jscl.math.Generic;

/**
 * User: serso
 * Date: 10/18/11
 * Time: 10:42 PM
 */
public class FromJsclNumericTextProcessorTest extends AbstractCalculatorTest {

    @BeforeClass
    public static void staticSetUp() throws Exception {
        CalculatorTestUtils.staticSetUp();
    }

    @Test
    public void testCreateResultForComplexNumber() throws Exception {
        final FromJsclNumericTextProcessor cm = new FromJsclNumericTextProcessor();

        final JsclMathEngine me = JsclMathEngine.getInstance();
        final AngleUnit defaultAngleUnits = me.getAngleUnits();

        Assert.assertEquals("1.22133+23 123i", cm.process(Expression.valueOf("1.22133232+23123*i").numeric()));
        Assert.assertEquals("1.22133+1.2i", cm.process(Expression.valueOf("1.22133232+1.2*i").numeric()));
        Assert.assertEquals("1.22133+0i", cm.process(Expression.valueOf("1.22133232+0.000000001*i").numeric()));
        try {
            me.setAngleUnits(AngleUnit.rad);
            Assert.assertEquals("1-0i", cm.process(Expression.valueOf("-(e^(i*π))").numeric()));
        } finally {
            me.setAngleUnits(defaultAngleUnits);
        }
        Assert.assertEquals("1.22i", cm.process(Expression.valueOf("1.22*i").numeric()));
        Assert.assertEquals("i", cm.process(Expression.valueOf("i").numeric()));
        Generic numeric = Expression.valueOf("e^(Π*i)+1").numeric();
        junit.framework.Assert.assertEquals("0i", cm.process(numeric));
    }
}
