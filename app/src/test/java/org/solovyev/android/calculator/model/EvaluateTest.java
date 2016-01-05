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

import org.junit.BeforeClass;
import org.junit.Test;
import org.solovyev.android.calculator.AbstractCalculatorTest;
import org.solovyev.android.calculator.CalculatorTestUtils;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.jscl.JsclOperation;

import jscl.AngleUnit;
import jscl.MathEngine;
import jscl.NumeralBase;
import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.function.Constant;


/**
 * User: serso
 * Date: 9/17/11
 * Time: 9:47 PM
 */

@SuppressWarnings("deprecation")
public class EvaluateTest extends AbstractCalculatorTest {

    @BeforeClass
    public static void staticSetUp() throws Exception {
        CalculatorTestUtils.staticSetUp();
        Locator.getInstance().getEngine().setPrecision(3);
    }

    @Test
    public void testEvaluate() throws Exception {
        final MathEngine cm = Locator.getInstance().getEngine().getMathEngine0();

        final AngleUnit defaultAngleUnit = cm.getAngleUnits();

        CalculatorTestUtils.assertEval("cos(t)+10%", "cos(t)+10%", JsclOperation.simplify);

        final Generic expression = cm.simplifyGeneric("cos(t)+10%");
        expression.substitute(new Constant("t"), Expression.valueOf(100d));

        CalculatorTestUtils.assertEval("it", "it", JsclOperation.simplify);
        CalculatorTestUtils.assertEval("10%", "10%", JsclOperation.simplify);
        CalculatorTestUtils.assertEval("0", "eq(0, 1)");
        CalculatorTestUtils.assertEval("1", "eq(1, 1)");
        CalculatorTestUtils.assertEval("1", "eq(  1,   1)");
        CalculatorTestUtils.assertEval("1", "eq(  1,   1)", JsclOperation.simplify);
        CalculatorTestUtils.assertEval("1", "lg(10)");
        CalculatorTestUtils.assertEval("4", "2+2");
        try {
            cm.setAngleUnits(AngleUnit.rad);
            CalculatorTestUtils.assertEval("-0.757", "sin(4)");
            CalculatorTestUtils.assertEval("0.524", "asin(0.5)");
            CalculatorTestUtils.assertEval("-0.396", "sin(4)asin(0.5)");
            CalculatorTestUtils.assertEval("-0.56", "sin(4)asin(0.5)√(2)");
            CalculatorTestUtils.assertEval("-0.56", "sin(4)asin(0.5)√(2)");
        } finally {
            cm.setAngleUnits(defaultAngleUnit);
        }
        CalculatorTestUtils.assertEval("7.389", "e^2");
        CalculatorTestUtils.assertEval("7.389", "exp(1)^2");
        CalculatorTestUtils.assertEval("7.389", "exp(2)");
        CalculatorTestUtils.assertEval("2+i", "2*1+√(-1)");
        try {
            cm.setAngleUnits(AngleUnit.rad);
            CalculatorTestUtils.assertEval("0.921+Πi", "ln(5cosh(38π√(2cos(2))))");
            CalculatorTestUtils.assertEval("-3.41+3.41i", "(5tan(2i)+2i)/(1-i)");
        } finally {
            cm.setAngleUnits(defaultAngleUnit);
        }
        CalculatorTestUtils.assertEval("7.389i", "iexp(2)");
        CalculatorTestUtils.assertEval("2+7.389i", "2+iexp(2)");
        CalculatorTestUtils.assertEval("2+7.389i", "2+√(-1)exp(2)");
        CalculatorTestUtils.assertEval("2-2.5i", "2-2.5i");
        CalculatorTestUtils.assertEval("-2-2.5i", "-2-2.5i");
        CalculatorTestUtils.assertEval("-2+2.5i", "-2+2.5i");
        CalculatorTestUtils.assertEval("-2+2.1i", "-2+2.1i");
        CalculatorTestUtils.assertEval("-0.1-0.2i", "(1-i)/(2+6i)");

        CalculatorTestUtils.assertEval("24", "4!");
        CalculatorTestUtils.assertEval("24", "(2+2)!");
        CalculatorTestUtils.assertEval("120", "(2+2+1)!");
        CalculatorTestUtils.assertEval("24", "(2.0+2.0)!");
        CalculatorTestUtils.assertEval("24", "4.0!");
        CalculatorTestUtils.assertEval("720", "(3!)!");
        CalculatorTestUtils.assertEval("36", Expression.valueOf("3!^2").numeric().toString());
        CalculatorTestUtils.assertEval("3", Expression.valueOf("cubic(27)").numeric().toString());
        CalculatorTestUtils.assertError("i!");

        CalculatorTestUtils.assertEval("1", cm.evaluate("(π/π)!"));

        CalculatorTestUtils.assertError("(-1)i!");
        CalculatorTestUtils.assertEval("24i", "4!i");

        Locator.getInstance().getEngine().getVarsRegistry().add(new Var.Builder("si", 5d));

        try {
            cm.setAngleUnits(AngleUnit.rad);
            CalculatorTestUtils.assertEval("0.451", "acos(0.8999999999999811)");
            CalculatorTestUtils.assertEval("-0.959", "sin(5)");
            CalculatorTestUtils.assertEval("-4.795", "sin(5)si");
            CalculatorTestUtils.assertEval("-23.973", "sisin(5)si");
            CalculatorTestUtils.assertEval("-23.973", "si*sin(5)si");
            CalculatorTestUtils.assertEval("-3.309", "sisin(5si)si");
        } finally {
            cm.setAngleUnits(defaultAngleUnit);
        }

        Locator.getInstance().getEngine().getVarsRegistry().add(new Var.Builder("s", 1d));
        CalculatorTestUtils.assertEval("5", cm.evaluate("si"));

        Locator.getInstance().getEngine().getVarsRegistry().add(new Var.Builder("k", 3.5d));
        Locator.getInstance().getEngine().getVarsRegistry().add(new Var.Builder("k1", 4d));
        CalculatorTestUtils.assertEval("4", "k11");

        Locator.getInstance().getEngine().getVarsRegistry().add(new Var.Builder("t", (String) null));
        CalculatorTestUtils.assertEval("11t", "t11");
        CalculatorTestUtils.assertEval("11et", "t11e");
        CalculatorTestUtils.assertEval("∞", "∞");
        CalculatorTestUtils.assertEval("∞", "Infinity");
        CalculatorTestUtils.assertEval("11∞t", "t11∞");
        CalculatorTestUtils.assertEval("-t+t^3", "t(t-1)(t+1)");

        CalculatorTestUtils.assertEval("100", "0.1E3");
        CalculatorTestUtils.assertEval("3.957", "ln(8)lg(8)+ln(8)");

        CalculatorTestUtils.assertEval("0.933", "0x:E/0x:F");

        try {
            cm.setNumeralBase(NumeralBase.hex);
            CalculatorTestUtils.assertEval("0.EE E", "0x:E/0x:F");
            CalculatorTestUtils.assertEval("0.EE E", cm.simplify("0x:E/0x:F"));
            CalculatorTestUtils.assertEval("0.EE E", "E/F");
            CalculatorTestUtils.assertEval("0.EE E", cm.simplify("E/F"));
        } finally {
            cm.setNumeralBase(NumeralBase.dec);
        }

        CalculatorTestUtils.assertEval("0", "((((((0))))))");
        CalculatorTestUtils.assertEval("0", "((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((0))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))");


		/*	CalculatorTestUtils.assertEval("0.524", cm.evaluate( "30°").getResult());
        CalculatorTestUtils.assertEval("0.524", cm.evaluate( "(10+20)°").getResult());
		CalculatorTestUtils.assertEval("1.047", cm.evaluate( "(10+20)°*2").getResult());
		try {
			CalculatorTestUtils.assertEval("0.278", cm.evaluate( "30°^2").getResult());
			fail();
		} catch (ParseException e) {
			if ( !e.getMessage().equals("Power operation after postfix function is currently unsupported!") ) {
				fail();
			}
		}*//*

*//*		try {
			cm.setTimeout(5000);
			CalculatorTestUtils.assertEval("2", cm.evaluate( "2!").getResult());
		} finally {
			cm.setTimeout(3000);
		}*/

        Locator.getInstance().getEngine().getVarsRegistry().add(new Var.Builder("t", (String) null));
        CalculatorTestUtils.assertEval("2t", "∂(t^2,t)", JsclOperation.simplify);
        CalculatorTestUtils.assertEval("2t", "∂(t^2,t)");
        Locator.getInstance().getEngine().getVarsRegistry().add(new Var.Builder("t", "2"));
        CalculatorTestUtils.assertEval("2t", "∂(t^2,t)", JsclOperation.simplify);
        CalculatorTestUtils.assertEval("4", "∂(t^2,t)");

        CalculatorTestUtils.assertEval("-x+xln(x)", "∫(ln(x), x)", JsclOperation.simplify);
        CalculatorTestUtils.assertEval("-(x-xln(x))/(ln(2)+ln(5))", "∫(log(10, x), x)", JsclOperation.simplify);

        CalculatorTestUtils.assertEval("∫((ln(2)+ln(5))/ln(x), x)", "∫(ln(10)/ln(x), x)", JsclOperation.simplify);
        //CalculatorTestUtils.assertEval("∫(ln(10)/ln(x), x)", Expression.valueOf("∫(log(x, 10), x)").expand().toString());
        CalculatorTestUtils.assertEval("∫((ln(2)+ln(5))/ln(x), x)", "∫(log(x, 10), x)");
        CalculatorTestUtils.assertEval("∫((ln(2)+ln(5))/ln(x), x)", "∫(log(x, 10), x)", JsclOperation.simplify);
    }
}
