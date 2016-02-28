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

package org.solovyev.android.calculator.model;

import jscl.AngleUnit;
import jscl.JsclMathEngine;
import jscl.MathEngine;
import jscl.NumeralBase;
import jscl.math.Expression;
import jscl.math.function.CustomFunction;
import jscl.text.ParseException;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.solovyev.android.calculator.BaseCalculatorTest;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.calculator.variables.CppVariable;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static org.junit.Assert.fail;

public class AndroidEngineTest extends BaseCalculatorTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        engine.getMathEngine().setPrecision(3);
    }

    @Test
    public void testDegrees() throws Exception {
        final MathEngine me = engine.getMathEngine();
        final AngleUnit defaultAngleUnit = me.getAngleUnits();
        try {
            me.setAngleUnits(AngleUnit.rad);
            me.setPrecision(3);
            assertError("°");
            assertEval("0.017", "1°");
            assertEval("0.349", "20.0°");
            assertEval("0.5", "sin(30°)");
            assertEval("0.524", "asin(sin(30°))");
            assertEval("∂(cos(t), t, t, 1°)", "∂(cos(t),t,t,1°)");

            assertEval("∂(cos(t), t, t, 1°)", "∂(cos(t),t,t,1°)", JsclOperation.simplify);
        } finally {
            me.setAngleUnits(defaultAngleUnit);
        }
    }

    @Test
    public void testFormatting() throws Exception {
        final MathEngine me = engine.getMathEngine();
        assertEval("12 345", me.simplify("12345"));
    }

    @Test
    public void testI() throws ParseException {
        final MathEngine me = engine.getMathEngine();

        assertEval("-i", me.evaluate("i^3"));
        for (int i = 0; i < 1000; i++) {
            double real = (Math.random() - 0.5) * 1000;
            double imag = (Math.random() - 0.5) * 1000;
            int exp = (int) (Math.random() * 10);

            final StringBuilder sb = new StringBuilder();
            sb.append(real);
            if (imag > 0) {
                sb.append("+");
            }
            sb.append(imag);
            sb.append("^").append(exp);
            try {
                me.evaluate(sb.toString());
            } catch (Throwable e) {
                fail(sb.toString());
            }
        }
    }

    @Test
    public void testEmptyFunction() throws Exception {
        final MathEngine me = engine.getMathEngine();
        try {
            me.evaluate("cos(cos(cos(cos(acos(acos(acos(acos(acos(acos(acos(acos(cos(cos(cos(cos(cosh(acos(cos(cos(cos(cos(cos(acos(acos(acos(acos(acos(acos(acos(acos(cos(cos(cos(cos(cosh(acos(cos())))))))))))))))))))))))))))))))))))))");
            Assert.fail();
        } catch (ParseException ignored) {
        }
        assertEval("0.34+1.382i", "ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(100)))))))))))))))");
        try {
            me.evaluate("cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos())))))))))))))))))))))))))))))))))))");
            Assert.fail();
        } catch (ParseException ignored) {
        }

        final AngleUnit defaultAngleUnit = me.getAngleUnits();
        try {
            me.setAngleUnits(AngleUnit.rad);
            assertEval("0.739", me.evaluate("cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(1))))))))))))))))))))))))))))))))))))"));
        } finally {
            me.setAngleUnits(defaultAngleUnit);
        }

        engine.getVariablesRegistry().add(CppVariable.builder("si").withValue(5d).build().toJsclBuilder());
        assertEval("5", me.evaluate("si"));

        assertError("sin");
    }

    @Test
    public void testRounding() throws Exception {
        final MathEngine me = engine.getMathEngine();

        try {
            DecimalFormatSymbols decimalGroupSymbols = new DecimalFormatSymbols(Locale.getDefault());
            decimalGroupSymbols.setDecimalSeparator('.');
            decimalGroupSymbols.setGroupingSeparator('\'');
            me.setDecimalGroupSymbols(decimalGroupSymbols);
            me.setPrecision(2);
            assertEval("12'345'678.9", me.evaluate("1.23456789E7"));
            me.setPrecision(10);
            assertEval("12'345'678.9", me.evaluate("1.23456789E7"));
            assertEval("123'456'789", me.evaluate("1.234567890E8"));
            assertEval("1'234'567'890.1", me.evaluate("1.2345678901E9"));
        } finally {
            me.setPrecision(3);
            DecimalFormatSymbols decimalGroupSymbols = new DecimalFormatSymbols(Locale.getDefault());
            decimalGroupSymbols.setDecimalSeparator('.');
            decimalGroupSymbols.setGroupingSeparator(JsclMathEngine.GROUPING_SEPARATOR_DEFAULT.charAt(0));
            me.setDecimalGroupSymbols(decimalGroupSymbols);
        }
    }

    @Test
    public void testNumeralSystems() throws Exception {
        final MathEngine me = engine.getMathEngine();

        assertEval("11 259 375", "0x:ABCDEF");
        assertEval("30 606 154.462", "0x:ABCDEF*e");
        assertEval("30 606 154.462", "e*0x:ABCDEF");
        assertEval("e", "e*0x:ABCDEF/0x:ABCDEF");
        assertEval("30 606 154.462", "0x:ABCDEF*e*0x:ABCDEF/0x:ABCDEF");
        assertEval("30 606 154.462", "c+0x:ABCDEF*e*0x:ABCDEF/0x:ABCDEF-c+0x:C-0x:C");
        assertEval("1 446 257 064 651.832", "28*28 * sin(28) - 0b:1101 + √(28) + exp ( 28) ");
        assertEval("13", "0b:1101");

        assertError("0b:π");

        final NumeralBase defaultNumeralBase = me.getNumeralBase();
        try {
            me.setNumeralBase(NumeralBase.bin);
            assertEval("101", "10+11");
            assertEval("0.101", "10/11");

            me.setNumeralBase(NumeralBase.hex);
            assertEval("63 7B", "56CE+CAD");
            assertEval("E", "E");
        } finally {
            me.setNumeralBase(defaultNumeralBase);
        }
    }

    @Test
    public void testLog() throws Exception {
        final MathEngine me = engine.getMathEngine();

        assertEval("∞", Expression.valueOf("1/0").numeric().toString());
        assertEval("∞", Expression.valueOf("ln(10)/ln(1)").numeric().toString());

        // logarithm
        assertEval("ln(x)/ln(base)", ((CustomFunction) me.getFunctionsRegistry().get("log")).getContent());
        assertEval("∞", "log(1, 10)");
        assertEval("3.322", "log(2, 10)");
        assertEval("1.431", "log(5, 10)");
        assertEval("0.96", "log(11, 10)");
        assertEval("1/(bln(a))", "∂(log(a, b), b)", JsclOperation.simplify);
        assertEval("-ln(b)/(aln(a)^2)", "∂(log(a, b), a)", JsclOperation.simplify);

    }
}
