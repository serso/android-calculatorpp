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

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.solovyev.android.calculator.AbstractCalculatorTest;
import org.solovyev.android.calculator.CalculatorEvalException;
import org.solovyev.android.calculator.CalculatorTestUtils;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.calculator.variables.OldVar;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import jscl.AngleUnit;
import jscl.JsclMathEngine;
import jscl.MathEngine;
import jscl.NumeralBase;
import jscl.math.Expression;
import jscl.math.function.CustomFunction;
import jscl.text.ParseException;

import static org.junit.Assert.fail;


/**
 * User: serso
 * Date: 9/17/11
 * Time: 9:47 PM
 */

@SuppressWarnings("deprecation")
public class AndroidEngineTest extends AbstractCalculatorTest {

    @BeforeClass
    public static void staticSetUp() throws Exception {
        CalculatorTestUtils.staticSetUp();
        Locator.getInstance().getEngine().getMathEngine().setPrecision(3);
    }


    @Test
    public void testDegrees() throws Exception {
        final MathEngine cm = Locator.getInstance().getEngine().getMathEngine();

        final AngleUnit defaultAngleUnit = cm.getAngleUnits();
        try {
            cm.setAngleUnits(AngleUnit.rad);
            cm.setPrecision(3);
            CalculatorTestUtils.assertError("°");
            CalculatorTestUtils.assertEval("0.017", "1°");
            CalculatorTestUtils.assertEval("0.349", "20.0°");
            CalculatorTestUtils.assertEval("0.5", "sin(30°)");
            CalculatorTestUtils.assertEval("0.524", "asin(sin(30°))");
            CalculatorTestUtils.assertEval("∂(cos(t), t, t, 1°)", "∂(cos(t),t,t,1°)");

            CalculatorTestUtils.assertEval("∂(cos(t), t, t, 1°)", "∂(cos(t),t,t,1°)", JsclOperation.simplify);
        } finally {
            cm.setAngleUnits(defaultAngleUnit);
        }
    }

    @Test
    public void testFormatting() throws Exception {
        final MathEngine ce = Locator.getInstance().getEngine().getMathEngine();

        CalculatorTestUtils.assertEval("12 345", ce.simplify("12345"));

    }

    @Test
    public void testI() throws ParseException, CalculatorEvalException {
        final MathEngine cm = Locator.getInstance().getEngine().getMathEngine();

        CalculatorTestUtils.assertEval("-i", cm.evaluate("i^3"));
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
                cm.evaluate(sb.toString());
            } catch (Throwable e) {
                fail(sb.toString());
            }
        }
    }

    @Test
    public void testEmptyFunction() throws Exception {
        final MathEngine cm = Locator.getInstance().getEngine().getMathEngine();
        try {
            cm.evaluate("cos(cos(cos(cos(acos(acos(acos(acos(acos(acos(acos(acos(cos(cos(cos(cos(cosh(acos(cos(cos(cos(cos(cos(acos(acos(acos(acos(acos(acos(acos(acos(cos(cos(cos(cos(cosh(acos(cos())))))))))))))))))))))))))))))))))))))");
            Assert.fail();
        } catch (ParseException e) {
        }
        CalculatorTestUtils.assertEval("0.34+1.382i", "ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(100)))))))))))))))");
        try {
            cm.evaluate("cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos())))))))))))))))))))))))))))))))))))");
            Assert.fail();
        } catch (ParseException e) {
        }

        final AngleUnit defaultAngleUnit = cm.getAngleUnits();
        try {
            cm.setAngleUnits(AngleUnit.rad);
            CalculatorTestUtils.assertEval("0.739", cm.evaluate("cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(1))))))))))))))))))))))))))))))))))))"));
        } finally {
            cm.setAngleUnits(defaultAngleUnit);
        }

        Locator.getInstance().getEngine().getVariablesRegistry().add(new OldVar.Builder("si", 5d));
        CalculatorTestUtils.assertEval("5", cm.evaluate("si"));

        CalculatorTestUtils.assertError("sin");
    }

    @Test
    public void testRounding() throws Exception {
        final MathEngine cm = Locator.getInstance().getEngine().getMathEngine();

        try {
            DecimalFormatSymbols decimalGroupSymbols = new DecimalFormatSymbols(Locale.getDefault());
            decimalGroupSymbols.setDecimalSeparator('.');
            decimalGroupSymbols.setGroupingSeparator('\'');
            cm.setDecimalGroupSymbols(decimalGroupSymbols);
            cm.setPrecision(2);
            CalculatorTestUtils.assertEval("12'345'678.9", cm.evaluate("1.23456789E7"));
            cm.setPrecision(10);
            CalculatorTestUtils.assertEval("12'345'678.9", cm.evaluate("1.23456789E7"));
            CalculatorTestUtils.assertEval("123'456'789", cm.evaluate("1.234567890E8"));
            CalculatorTestUtils.assertEval("1'234'567'890.1", cm.evaluate("1.2345678901E9"));
        } finally {
            cm.setPrecision(3);
            DecimalFormatSymbols decimalGroupSymbols = new DecimalFormatSymbols(Locale.getDefault());
            decimalGroupSymbols.setDecimalSeparator('.');
            decimalGroupSymbols.setGroupingSeparator(JsclMathEngine.GROUPING_SEPARATOR_DEFAULT.charAt(0));
            cm.setDecimalGroupSymbols(decimalGroupSymbols);
        }
    }

    @Test
    public void testNumeralSystems() throws Exception {
        final MathEngine cm = Locator.getInstance().getEngine().getMathEngine();

        CalculatorTestUtils.assertEval("11 259 375", "0x:ABCDEF");
        CalculatorTestUtils.assertEval("30 606 154.462", "0x:ABCDEF*e");
        CalculatorTestUtils.assertEval("30 606 154.462", "e*0x:ABCDEF");
        CalculatorTestUtils.assertEval("e", "e*0x:ABCDEF/0x:ABCDEF");
        CalculatorTestUtils.assertEval("30 606 154.462", "0x:ABCDEF*e*0x:ABCDEF/0x:ABCDEF");
        CalculatorTestUtils.assertEval("30 606 154.462", "c+0x:ABCDEF*e*0x:ABCDEF/0x:ABCDEF-c+0x:C-0x:C");
        CalculatorTestUtils.assertEval("1 446 257 064 651.832", "28*28 * sin(28) - 0b:1101 + √(28) + exp ( 28) ");
        CalculatorTestUtils.assertEval("13", "0b:1101");

        CalculatorTestUtils.assertError("0b:π");

        final NumeralBase defaultNumeralBase = cm.getNumeralBase();
        try {
            cm.setNumeralBase(NumeralBase.bin);
            CalculatorTestUtils.assertEval("101", "10+11");
            CalculatorTestUtils.assertEval("0.101", "10/11");

            cm.setNumeralBase(NumeralBase.hex);
            CalculatorTestUtils.assertEval("63 7B", "56CE+CAD");
            CalculatorTestUtils.assertEval("E", "E");
        } finally {
            cm.setNumeralBase(defaultNumeralBase);
        }
    }

    @Test
    public void testLog() throws Exception {
        final MathEngine cm = Locator.getInstance().getEngine().getMathEngine();

        CalculatorTestUtils.assertEval("∞", Expression.valueOf("1/0").numeric().toString());
        CalculatorTestUtils.assertEval("∞", Expression.valueOf("ln(10)/ln(1)").numeric().toString());

        // logarithm
        CalculatorTestUtils.assertEval("ln(x)/ln(base)", ((CustomFunction) cm.getFunctionsRegistry().get("log")).getContent());
        CalculatorTestUtils.assertEval("∞", "log(1, 10)");
        CalculatorTestUtils.assertEval("3.322", "log(2, 10)");
        CalculatorTestUtils.assertEval("1.431", "log(5, 10)");
        CalculatorTestUtils.assertEval("0.96", "log(11, 10)");
        CalculatorTestUtils.assertEval("1/(bln(a))", "∂(log(a, b), b)", JsclOperation.simplify);
        CalculatorTestUtils.assertEval("-ln(b)/(aln(a)^2)", "∂(log(a, b), a)", JsclOperation.simplify);

    }
}
