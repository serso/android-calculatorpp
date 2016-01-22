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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.solovyev.android.calculator.AbstractCalculatorTest;
import org.solovyev.android.calculator.ParseException;
import org.solovyev.android.calculator.CalculatorTestUtils;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.PreparedExpression;
import org.solovyev.android.calculator.ToJsclTextProcessor;
import org.solovyev.android.calculator.text.TextProcessor;

import jscl.JsclMathEngine;
import jscl.NumeralBase;

/**
 * User: serso
 * Date: 9/26/11
 * Time: 12:13 PM
 */
public class ToJsclTextProcessorTest extends AbstractCalculatorTest {

    @BeforeClass
    public static void staticSetUp() throws Exception {
        CalculatorTestUtils.staticSetUp();
    }

    @Test
    public void testSpecialCases() throws ParseException {
        final TextProcessor<PreparedExpression, String> preprocessor = ToJsclTextProcessor.getInstance();
        Assert.assertEquals("3^E10", preprocessor.process("3^E10").toString());
    }

    @Test
    public void testProcess() throws Exception {
        final TextProcessor<PreparedExpression, String> preprocessor = ToJsclTextProcessor.getInstance();

        Assert.assertEquals("", preprocessor.process("").toString());
        Assert.assertEquals("()", preprocessor.process("[]").toString());
        Assert.assertEquals("()*()", preprocessor.process("[][]").toString());
        Assert.assertEquals("()*(1)", preprocessor.process("[][1]").toString());
        Assert.assertEquals("(0)*(1)", preprocessor.process("[0][1]").toString());
        Assert.assertEquals("(0)*(1E)", preprocessor.process("[0][1E]").toString());
        Assert.assertEquals("(0)*(1E1)", preprocessor.process("[0][1E1]").toString());
        Assert.assertEquals("(0)*(1E-1)", preprocessor.process("[0][1E-1]").toString());
        Assert.assertEquals("(0)*(1.E-1)", preprocessor.process("[0][1.E-1]").toString());
        Assert.assertEquals("(0)*(2*E-1)", preprocessor.process("[0][2*E-1]").toString());
        Assert.assertEquals("(0)*ln(1)*(2*E-1)", preprocessor.process("[0]ln(1)[2*E-1]").toString());
        Assert.assertEquals("sin(4)*asin(0.5)*√(2)", preprocessor.process("sin(4)asin(0.5)√(2)").toString());
        Assert.assertEquals("sin(4)*cos(5)", preprocessor.process("sin(4)cos(5)").toString());
        Assert.assertEquals("π*sin(4)*π*cos(√(5))", preprocessor.process("πsin(4)πcos(√(5))").toString());
        Assert.assertEquals("π*sin(4)+π*cos(√(5))", preprocessor.process("πsin(4)+πcos(√(5))").toString());
        Assert.assertEquals("π*sin(4)+π*cos(√(5+(√(-1))))", preprocessor.process("πsin(4)+πcos(√(5+i))").toString());
        Assert.assertEquals("π*sin(4.01)+π*cos(√(5+(√(-1))))", preprocessor.process("πsin(4.01)+πcos(√(5+i))").toString());
        Assert.assertEquals("e^π*sin(4.01)+π*cos(√(5+(√(-1))))", preprocessor.process("e^πsin(4.01)+πcos(√(5+i))").toString());
        Assert.assertEquals("e^π*sin(4.01)+π*cos(√(5+(√(-1))))E2", preprocessor.process("e^πsin(4.01)+πcos(√(5+i))E2").toString());
        Assert.assertEquals("e^π*sin(4.01)+π*cos(√(5+(√(-1))))E-2", preprocessor.process("e^πsin(4.01)+πcos(√(5+i))E-2").toString());
        Assert.assertEquals("E2", preprocessor.process("E2").toString());
        Assert.assertEquals("E-2", preprocessor.process("E-2").toString());
        Assert.assertEquals("E-1/2", preprocessor.process("E-1/2").toString());
        Assert.assertEquals("E-1.2", preprocessor.process("E-1.2").toString());
        Assert.assertEquals("E+1.2", preprocessor.process("E+1.2").toString());
        Assert.assertEquals("E(-1.2)", preprocessor.process("E(-1.2)").toString());
        Assert.assertEquals("EE", preprocessor.process("EE").toString());

        try {
            Locator.getInstance().getEngine().getMathEngine().setNumeralBase(NumeralBase.hex);
            Assert.assertEquals("22F*exp(F)", preprocessor.process("22Fexp(F)").toString());
        } finally {
            Locator.getInstance().getEngine().getMathEngine().setNumeralBase(NumeralBase.dec);
        }
        Assert.assertEquals("0x:ABCDEF", preprocessor.process("0x:ABCDEF").toString());
        Assert.assertEquals("0x:ABCDEF", preprocessor.process("0x:A BC DEF").toString());
        Assert.assertEquals("0x:ABCDEF", preprocessor.process("0x:A BC                           DEF").toString());
        Assert.assertEquals("0x:ABCDEF*0*x", preprocessor.process("0x:A BC DEF*0x").toString());
        Assert.assertEquals("0x:ABCDEF001*0*x", preprocessor.process("0x:A BC DEF001*0x").toString());
        Assert.assertEquals("0x:ABCDEF001*0*c", preprocessor.process("0x:A BC DEF001*0c").toString());
        Assert.assertEquals("0x:ABCDEF001*c", preprocessor.process("0x:A BC DEF001*c").toString());
        Assert.assertEquals("0b:1101", preprocessor.process("0b:1101").toString());
        Assert.assertEquals("0x:1C", preprocessor.process("0x:1C").toString());
        Assert.assertEquals("0x:1C", preprocessor.process(" 0x:1C").toString());
        Assert.assertEquals("0x:1C*0x:1C*sin(0x:1C)-0b:1101+√(0x:1C)+exp(0x:1C)", preprocessor.process("0x:1C*0x:1C * sin(0x:1C) - 0b:1101 + √(0x:1C) + exp ( 0x:1C)").toString());
        Assert.assertEquals("0x:1C*0x:1C*sin(0x:1C)-0b:1101+√(0x:1C)+exp(0x:1C)", preprocessor.process("0x:1C*0x:1C * sin(0x:1C) - 0b:1101 + √(0x:1C) + exp ( 0x:1C)").toString());

        try {
            preprocessor.process("ln()");
            Assert.fail();
        } catch (ParseException e) {
        }
        try {
            preprocessor.process("ln()ln()");
            Assert.fail();
        } catch (ParseException e) {
        }

        try {
            preprocessor.process("eln()eln()ln()ln()ln()e");
            Assert.fail();
        } catch (ParseException e) {
        }

        try {
            preprocessor.process("ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln()))))))))))))))");
            Assert.fail();
        } catch (ParseException e) {
        }

        try {
            preprocessor.process("cos(cos(cos(cos(acos(acos(acos(acos(acos(acos(acos(acos(cos(cos(cos(cos(cosh(acos(cos(cos(cos(cos(cos(acos(acos(acos(acos(acos(acos(acos(acos(cos(cos(cos(cos(cosh(acos(cos())))))))))))))))))))))))))))))))))))))");
            Assert.fail();
        } catch (ParseException e) {
        }
    }

    @Test
    public void testPostfixFunction() throws Exception {
    }

    @Test
    public void testNumeralBases() throws Exception {
        final TextProcessor<PreparedExpression, String> processor = ToJsclTextProcessor.getInstance();

        final NumeralBase defaultNumeralBase = JsclMathEngine.getInstance().getNumeralBase();
        try {
            JsclMathEngine.getInstance().setNumeralBase(NumeralBase.bin);
            Assert.assertEquals("101", JsclMathEngine.getInstance().evaluate("10+11"));

            JsclMathEngine.getInstance().setNumeralBase(NumeralBase.hex);
            Assert.assertEquals("56CE+CAD", processor.process("56CE+CAD").getExpression());
        } finally {
            JsclMathEngine.getInstance().setNumeralBase(defaultNumeralBase);
        }
    }
}
