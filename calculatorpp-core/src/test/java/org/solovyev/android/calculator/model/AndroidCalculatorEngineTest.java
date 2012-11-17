/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator.model;

import jscl.AngleUnit;
import jscl.JsclMathEngine;
import jscl.MathEngine;
import jscl.NumeralBase;
import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.function.Constant;
import jscl.math.function.CustomFunction;
import jscl.text.ParseException;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.solovyev.android.calculator.AbstractCalculatorTest;
import org.solovyev.android.calculator.CalculatorEvalException;
import org.solovyev.android.calculator.CalculatorLocatorImpl;
import org.solovyev.android.calculator.CalculatorTestUtils;
import org.solovyev.android.calculator.jscl.JsclOperation;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static junit.framework.Assert.fail;

/**
 * User: serso
 * Date: 9/17/11
 * Time: 9:47 PM
 */

@SuppressWarnings("deprecation")
public class AndroidCalculatorEngineTest extends AbstractCalculatorTest {

	@BeforeClass
	public static void staticSetUp() throws Exception {
        CalculatorTestUtils.staticSetUp();
        CalculatorLocatorImpl.getInstance().getEngine().setPrecision(3);
	}
    

	@Test
	public void testDegrees() throws Exception {
		final MathEngine cm = CalculatorLocatorImpl.getInstance().getEngine().getMathEngine0();

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

/*	@Test
	public void testLongExecution() throws Exception {
		final MathEngine cm = CalculatorLocatorImpl.getInstance().getEngine().getMathEngine0();

		try {
			cm.evaluate( "3^10^10^10");
			fail();
		} catch (ParseException e) {
			if (e.getMessageCode().equals(Messages.msg_3)) {

			} else {
				System.out.print(e.getCause().getMessage());
				fail();
			}
		}

		try {
			cm.evaluate("9999999!");
			fail();
		} catch (ParseException e) {
			if (e.getMessageCode().equals(Messages.msg_3)) {

			} else {
				System.out.print(e.getCause().getMessage());
				fail();
			}
		}

		final long start = System.currentTimeMillis();
		try {
			cm.evaluate( "3^10^10^10");
			fail();
		} catch (ParseException e) {
			if (e.getMessage().startsWith("Too long calculation")) {
				final long end = System.currentTimeMillis();
				Assert.assertTrue(end - start < 1000);
			} else {
				fail();
			}
		}

	}*/

	@Test
	public void testEvaluate() throws Exception {
		final MathEngine cm = CalculatorLocatorImpl.getInstance().getEngine().getMathEngine0();

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
		final AngleUnit defaultAngleUnit = cm.getAngleUnits();
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
		CalculatorTestUtils.assertEval("-2+2.1i",  "-2+2.1i");
		CalculatorTestUtils.assertEval("-0.1-0.2i", "(1-i)/(2+6i)");

        CalculatorTestUtils.assertEval("24", "4!");
        CalculatorTestUtils.assertEval("24",  "(2+2)!");
        CalculatorTestUtils.assertEval("120", "(2+2+1)!");
        CalculatorTestUtils.assertEval("24", "(2.0+2.0)!");
        CalculatorTestUtils.assertEval("24",  "4.0!");
        CalculatorTestUtils.assertEval("720", "(3!)!");
        CalculatorTestUtils.assertEval("36", Expression.valueOf("3!^2").numeric().toString());
        CalculatorTestUtils.assertEval("3", Expression.valueOf("cubic(27)").numeric().toString());
        CalculatorTestUtils.assertError("i!");

        CalculatorTestUtils.assertEval("1", cm.evaluate( "(π/π)!"));

        CalculatorTestUtils.assertError("(-1)i!");
        CalculatorTestUtils.assertEval("24i", "4!i");

		CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry().add(new Var.Builder("si", 5d));

		try {
			cm.setAngleUnits(AngleUnit.rad);
			CalculatorTestUtils.assertEval("0.451", "acos(0.8999999999999811)");
			CalculatorTestUtils.assertEval("-0.959", "sin(5)");
			CalculatorTestUtils.assertEval("-4.795", "sin(5)si");
			CalculatorTestUtils.assertEval("-23.973",  "sisin(5)si");
			CalculatorTestUtils.assertEval("-23.973", "si*sin(5)si");
			CalculatorTestUtils.assertEval("-3.309", "sisin(5si)si");
		} finally {
			cm.setAngleUnits(defaultAngleUnit);
		}

		CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry().add(new Var.Builder("s", 1d));
		CalculatorTestUtils.assertEval("5", cm.evaluate( "si"));

		CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry().add(new Var.Builder("k", 3.5d));
		CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry().add(new Var.Builder("k1", 4d));
		CalculatorTestUtils.assertEval("4", "k11");

		CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry().add(new Var.Builder("t", (String) null));
		CalculatorTestUtils.assertEval("11t", "t11");
		CalculatorTestUtils.assertEval("11et", "t11e");
		CalculatorTestUtils.assertEval("∞", "∞");
		CalculatorTestUtils.assertEval("∞", "Infinity");
		CalculatorTestUtils.assertEval("11∞t", "t11∞");
		CalculatorTestUtils.assertEval("-t+t^3", "t(t-1)(t+1)");

		CalculatorTestUtils.assertEval("100", "0.1E3");
		CalculatorTestUtils.assertEval("3.957", "ln(8)lg(8)+ln(8)");

		CalculatorTestUtils.assertEval("0.933",  "0x:E/0x:F");

		try {
		 	cm.setNumeralBase(NumeralBase.hex);
			CalculatorTestUtils.assertEval("0.EE E", "0x:E/0x:F");
			CalculatorTestUtils.assertEval("0.EE E", cm.simplify( "0x:E/0x:F"));
			CalculatorTestUtils.assertEval("0.EE E", "E/F");
			CalculatorTestUtils.assertEval("0.EE E", cm.simplify( "E/F"));
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

		CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry().add(new Var.Builder("t", (String) null));
		CalculatorTestUtils.assertEval("2t", "∂(t^2,t)", JsclOperation.simplify);
		CalculatorTestUtils.assertEval("2t", "∂(t^2,t)");
		CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry().add(new Var.Builder("t", "2"));
		CalculatorTestUtils.assertEval("2t", "∂(t^2,t)", JsclOperation.simplify);
		CalculatorTestUtils.assertEval("4", "∂(t^2,t)");

		CalculatorTestUtils.assertEval("-x+xln(x)", "∫(ln(x), x)", JsclOperation.simplify);
		CalculatorTestUtils.assertEval("-(x-xln(x))/(ln(2)+ln(5))", "∫(log(10, x), x)", JsclOperation.simplify);

		CalculatorTestUtils.assertEval("∫((ln(2)+ln(5))/ln(x), x)", "∫(ln(10)/ln(x), x)", JsclOperation.simplify);
		//CalculatorTestUtils.assertEval("∫(ln(10)/ln(x), x)", Expression.valueOf("∫(log(x, 10), x)").expand().toString());
        CalculatorTestUtils.assertEval("∫((ln(2)+ln(5))/ln(x), x)", "∫(log(x, 10), x)");
		CalculatorTestUtils.assertEval("∫((ln(2)+ln(5))/ln(x), x)", "∫(log(x, 10), x)", JsclOperation.simplify);
	}

	@Test
	public void testFormatting() throws Exception {
		final MathEngine ce = CalculatorLocatorImpl.getInstance().getEngine().getMathEngine0();

		CalculatorTestUtils.assertEval("12 345", ce.simplify( "12345"));

	}

	@Test
	public void testI() throws ParseException, CalculatorEvalException {
		final MathEngine cm = CalculatorLocatorImpl.getInstance().getEngine().getMathEngine0();

		CalculatorTestUtils.assertEval("-i", cm.evaluate( "i^3"));
		for (int i = 0; i < 1000; i++) {
		 	double real = (Math.random()-0.5) * 1000;
		 	double imag = (Math.random()-0.5) * 1000;
		 	int exp = (int)(Math.random() * 10);

			final StringBuilder sb = new StringBuilder();
			sb.append(real);
			if ( imag > 0 ) {
				sb.append("+");
			}
			sb.append(imag);
			sb.append("^").append(exp);
			try {
				cm.evaluate( sb.toString());
			} catch (Throwable e) {
				fail(sb.toString());
			}
		}
	}

	@Test
	public void testEmptyFunction() throws Exception {
		final MathEngine cm = CalculatorLocatorImpl.getInstance().getEngine().getMathEngine0();
		try {
			cm.evaluate( "cos(cos(cos(cos(acos(acos(acos(acos(acos(acos(acos(acos(cos(cos(cos(cos(cosh(acos(cos(cos(cos(cos(cos(acos(acos(acos(acos(acos(acos(acos(acos(cos(cos(cos(cos(cosh(acos(cos())))))))))))))))))))))))))))))))))))))");
			Assert.fail();
		} catch (ParseException e) {
		}
		CalculatorTestUtils.assertEval("0.34+1.382i", "ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(100)))))))))))))))");
		try {
			cm.evaluate( "cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos())))))))))))))))))))))))))))))))))))");
			Assert.fail();
		} catch (ParseException e) {
		}

		final AngleUnit defaultAngleUnit = cm.getAngleUnits();
		try {
			cm.setAngleUnits(AngleUnit.rad);
			CalculatorTestUtils.assertEval("0.739", cm.evaluate( "cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(1))))))))))))))))))))))))))))))))))))"));
		} finally {
			cm.setAngleUnits(defaultAngleUnit);
		}

		CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry().add(new Var.Builder("si", 5d));
		CalculatorTestUtils.assertEval("5", cm.evaluate( "si"));

        CalculatorTestUtils.assertError("sin");
	}

	@Test
	public void testRounding() throws Exception {
		final MathEngine cm = CalculatorLocatorImpl.getInstance().getEngine().getMathEngine0();

		try {
			DecimalFormatSymbols decimalGroupSymbols = new DecimalFormatSymbols(Locale.getDefault());
			decimalGroupSymbols.setDecimalSeparator('.');
			decimalGroupSymbols.setGroupingSeparator('\'');
			cm.setDecimalGroupSymbols(decimalGroupSymbols);
			cm.setPrecision(2);
			CalculatorTestUtils.assertEval("12'345'678.9", cm.evaluate( "1.23456789E7"));
			cm.setPrecision(10);
			CalculatorTestUtils.assertEval("12'345'678.9", cm.evaluate( "1.23456789E7"));
			CalculatorTestUtils.assertEval("123'456'789", cm.evaluate( "1.234567890E8"));
			CalculatorTestUtils.assertEval("1'234'567'890.1", cm.evaluate( "1.2345678901E9"));
		} finally {
			cm.setPrecision(3);
			DecimalFormatSymbols decimalGroupSymbols = new DecimalFormatSymbols(Locale.getDefault());
			decimalGroupSymbols.setDecimalSeparator('.');
			decimalGroupSymbols.setGroupingSeparator(JsclMathEngine.GROUPING_SEPARATOR_DEFAULT.charAt(0));
			cm.setDecimalGroupSymbols(decimalGroupSymbols);
		}
	}

	@Test
	public void testComparisonFunction() throws Exception {
		final MathEngine cm = CalculatorLocatorImpl.getInstance().getEngine().getMathEngine0();

		CalculatorTestUtils.assertEval("0",  "eq(0, 1)");
		CalculatorTestUtils.assertEval("1",  "eq(1, 1)");
		CalculatorTestUtils.assertEval("1",  "eq(1, 1.0)");
		CalculatorTestUtils.assertEval("0",  "eq(1, 1.000000000000001)");
		CalculatorTestUtils.assertEval("0",  "eq(1, 0)");

		CalculatorTestUtils.assertEval("1",  "lt(0, 1)");
		CalculatorTestUtils.assertEval("0",  "lt(1, 1)");
		CalculatorTestUtils.assertEval("0",  "lt(1, 0)");

		CalculatorTestUtils.assertEval("0",  "gt(0, 1)");
		CalculatorTestUtils.assertEval("0",  "gt(1, 1)");
		CalculatorTestUtils.assertEval("1",  "gt(1, 0)");

		CalculatorTestUtils.assertEval("1",  "ne(0, 1)");
		CalculatorTestUtils.assertEval("0",  "ne(1, 1)");
		CalculatorTestUtils.assertEval("1",  "ne(1, 0)");

		CalculatorTestUtils.assertEval("1",  "le(0, 1)");
		CalculatorTestUtils.assertEval("1",  "le(1, 1)");
		CalculatorTestUtils.assertEval("0",  "le(1, 0)");

		CalculatorTestUtils.assertEval("0",  "ge(0, 1)");
		CalculatorTestUtils.assertEval("1",  "ge(1, 1)");
		CalculatorTestUtils.assertEval("1",  "ge(1, 0)");

		CalculatorTestUtils.assertEval("0",  "ap(0, 1)");
		CalculatorTestUtils.assertEval("1",  "ap(1, 1)");
		CalculatorTestUtils.assertEval("0",  "ap(1, 0)");

	}


	@Test
	public void testNumeralSystems() throws Exception {
		final MathEngine cm = CalculatorLocatorImpl.getInstance().getEngine().getMathEngine0();

		CalculatorTestUtils.assertEval("11 259 375",  "0x:ABCDEF");
		CalculatorTestUtils.assertEval("30 606 154.462",  "0x:ABCDEF*e");
		CalculatorTestUtils.assertEval("30 606 154.462",  "e*0x:ABCDEF");
		CalculatorTestUtils.assertEval("e",  "e*0x:ABCDEF/0x:ABCDEF");
		CalculatorTestUtils.assertEval("30 606 154.462",  "0x:ABCDEF*e*0x:ABCDEF/0x:ABCDEF");
		CalculatorTestUtils.assertEval("30 606 154.462",  "c+0x:ABCDEF*e*0x:ABCDEF/0x:ABCDEF-c+0x:C-0x:C");
		CalculatorTestUtils.assertEval("1 446 257 064 651.832",  "28*28 * sin(28) - 0b:1101 + √(28) + exp ( 28) ");
		CalculatorTestUtils.assertEval("13",  "0b:1101");

        CalculatorTestUtils.assertError("0b:π");

		final NumeralBase defaultNumeralBase = cm.getNumeralBase();
		try{
			cm.setNumeralBase(NumeralBase.bin);
			CalculatorTestUtils.assertEval("101", "10+11");
            CalculatorTestUtils.assertEval("0.101", "10/11");

			cm.setNumeralBase(NumeralBase.hex);
            CalculatorTestUtils.assertEval("63 7B", "56CE+CAD");
            CalculatorTestUtils.assertEval("E",  "E");
		} finally {
			cm.setNumeralBase(defaultNumeralBase);
		}
	}

	@Test
	public void testLog() throws Exception {
		final MathEngine cm = CalculatorLocatorImpl.getInstance().getEngine().getMathEngine0();

		CalculatorTestUtils.assertEval("∞", Expression.valueOf("1/0").numeric().toString());
		CalculatorTestUtils.assertEval("∞", Expression.valueOf("ln(10)/ln(1)").numeric().toString());

		// logarithm
		CalculatorTestUtils.assertEval("ln(x)/ln(base)", ((CustomFunction) cm.getFunctionsRegistry().get("log")).getContent());
		CalculatorTestUtils.assertEval("∞", "log(1, 10)");
        CalculatorTestUtils.assertEval("3.322", "log(2, 10)");
        CalculatorTestUtils.assertEval("1.431", "log(5, 10)");
        CalculatorTestUtils.assertEval("0.96",  "log(11, 10)");
        CalculatorTestUtils.assertEval("1/(bln(a))", "∂(log(a, b), b)", JsclOperation.simplify);
        CalculatorTestUtils.assertEval("-ln(b)/(aln(a)^2)", "∂(log(a, b), a)", JsclOperation.simplify);

	}
}
