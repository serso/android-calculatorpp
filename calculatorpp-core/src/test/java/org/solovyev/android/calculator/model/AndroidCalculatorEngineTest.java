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
import org.solovyev.android.calculator.CalculatorEvalException;
import org.solovyev.android.calculator.CalculatorLocatorImpl;
import org.solovyev.android.calculator.CalculatorTestUtils;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static junit.framework.Assert.fail;

/**
 * User: serso
 * Date: 9/17/11
 * Time: 9:47 PM
 */

@SuppressWarnings("deprecation")
public class AndroidCalculatorEngineTest {

	@BeforeClass
	public static void setUp() throws Exception {
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
			try {
				Assert.assertEquals("0.017", cm.evaluate("°"));
                fail();
			} catch (ParseException e) {

			}

			Assert.assertEquals("0.017", cm.evaluate( "1°"));
			Assert.assertEquals("0.349", cm.evaluate( "20.0°"));
			Assert.assertEquals("0.5", cm.evaluate( "sin(30°)"));
			Assert.assertEquals("0.524", cm.evaluate( "asin(sin(30°))"));
			Assert.assertEquals("∂(cos(t), t, t, 1°)", cm.evaluate( "∂(cos(t),t,t,1°)"));

			Assert.assertEquals("∂(cos(t), t, t, 1°)", cm.simplify("∂(cos(t),t,t,1°)"));
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

		Assert.assertEquals("cos(t)+10%", cm.simplify( "cos(t)+10%"));

		final Generic expression = cm.simplifyGeneric("cos(t)+10%");
		expression.substitute(new Constant("t"), Expression.valueOf(100d));

		Assert.assertEquals("it", cm.simplify( "it"));
		Assert.assertEquals("10%", cm.simplify( "10%"));
		Assert.assertEquals("0", cm.evaluate( "eq(0, 1)"));
		Assert.assertEquals("1", cm.evaluate( "eq(1, 1)"));
		Assert.assertEquals("1", cm.evaluate( "eq(  1,   1)"));
		Assert.assertEquals("1", cm.simplify( "eq(  1,   1)"));
		Assert.assertEquals("1", cm.evaluate( "lg(10)"));
		Assert.assertEquals("4", cm.evaluate( "2+2"));
		final AngleUnit defaultAngleUnit = cm.getAngleUnits();
		try {
			cm.setAngleUnits(AngleUnit.rad);
			Assert.assertEquals("-0.757", cm.evaluate( "sin(4)"));
			Assert.assertEquals("0.524", cm.evaluate( "asin(0.5)"));
			Assert.assertEquals("-0.396", cm.evaluate( "sin(4)asin(0.5)"));
			Assert.assertEquals("-0.56", cm.evaluate( "sin(4)asin(0.5)√(2)"));
			Assert.assertEquals("-0.56", cm.evaluate( "sin(4)asin(0.5)√(2)"));
		} finally {
			cm.setAngleUnits(defaultAngleUnit);
		}
		Assert.assertEquals("7.389", cm.evaluate( "e^2"));
		Assert.assertEquals("7.389", cm.evaluate( "exp(1)^2"));
		Assert.assertEquals("7.389", cm.evaluate( "exp(2)"));
		Assert.assertEquals("2+i", cm.evaluate( "2*1+√(-1)"));
		try {
			cm.setAngleUnits(AngleUnit.rad);
			Assert.assertEquals("0.921+Πi", cm.evaluate( "ln(5cosh(38π√(2cos(2))))"));
			Assert.assertEquals("-3.41+3.41i", cm.evaluate( "(5tan(2i)+2i)/(1-i)"));
		} finally {
			cm.setAngleUnits(defaultAngleUnit);
		}
		Assert.assertEquals("7.389i", cm.evaluate( "iexp(2)"));
		Assert.assertEquals("2+7.389i", cm.evaluate( "2+iexp(2)"));
		Assert.assertEquals("2+7.389i", cm.evaluate( "2+√(-1)exp(2)"));
		Assert.assertEquals("2-2.5i", cm.evaluate( "2-2.5i"));
		Assert.assertEquals("-2-2.5i", cm.evaluate( "-2-2.5i"));
		Assert.assertEquals("-2+2.5i", cm.evaluate( "-2+2.5i"));
		Assert.assertEquals("-2+2.1i", cm.evaluate( "-2+2.1i"));
		Assert.assertEquals("-0.1-0.2i", cm.evaluate( "(1-i)/(2+6i)"));

		junit.framework.Assert.assertEquals("24", cm.evaluate( "4!"));
		junit.framework.Assert.assertEquals("24", cm.evaluate( "(2+2)!"));
		junit.framework.Assert.assertEquals("120", cm.evaluate( "(2+2+1)!"));
		junit.framework.Assert.assertEquals("24", cm.evaluate( "(2.0+2.0)!"));
		junit.framework.Assert.assertEquals("24", cm.evaluate( "4.0!"));
		junit.framework.Assert.assertEquals("720", cm.evaluate( "(3!)!"));
		junit.framework.Assert.assertEquals("36", Expression.valueOf("3!^2").numeric().toString());
		junit.framework.Assert.assertEquals("3", Expression.valueOf("cubic(27)").numeric().toString());
		try {
			junit.framework.Assert.assertEquals("√(-1)!", cm.evaluate( "i!"));
			fail();
		} catch (ParseException e) {
		}

		junit.framework.Assert.assertEquals("1", cm.evaluate( "(π/π)!"));

		try {
			junit.framework.Assert.assertEquals("i", cm.evaluate( "(-1)i!"));
			fail();
		} catch (ParseException e) {

		}
		junit.framework.Assert.assertEquals("24i", cm.evaluate( "4!i"));

		CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry().add(new Var.Builder("si", 5d));

		try {
			cm.setAngleUnits(AngleUnit.rad);
			Assert.assertEquals("0.451", cm.evaluate( "acos(0.8999999999999811)"));
			Assert.assertEquals("-0.959", cm.evaluate( "sin(5)"));
			Assert.assertEquals("-4.795", cm.evaluate( "sin(5)si"));
			Assert.assertEquals("-23.973", cm.evaluate( "sisin(5)si"));
			Assert.assertEquals("-23.973", cm.evaluate( "si*sin(5)si"));
			Assert.assertEquals("-3.309", cm.evaluate( "sisin(5si)si"));
		} finally {
			cm.setAngleUnits(defaultAngleUnit);
		}

		CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry().add(new Var.Builder("s", 1d));
		Assert.assertEquals("5", cm.evaluate( "si"));

		CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry().add(new Var.Builder("k", 3.5d));
		CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry().add(new Var.Builder("k1", 4d));
		Assert.assertEquals("4", cm.evaluate( "k11"));

		CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry().add(new Var.Builder("t", (String) null));
		Assert.assertEquals("11t", cm.evaluate( "t11"));
		Assert.assertEquals("11et", cm.evaluate( "t11e"));
		Assert.assertEquals("∞", cm.evaluate( "∞"));
		Assert.assertEquals("∞", cm.evaluate( "Infinity"));
		Assert.assertEquals("11∞t", cm.evaluate( "t11∞"));
		Assert.assertEquals("-t+t^3", cm.evaluate( "t(t-1)(t+1)"));

		Assert.assertEquals("100", cm.evaluate( "0.1E3"));
		Assert.assertEquals("3.957", cm.evaluate( "ln(8)lg(8)+ln(8)"));

		Assert.assertEquals("0.933", cm.evaluate( "0x:E/0x:F"));

		try {
		 	cm.setNumeralBase(NumeralBase.hex);
			Assert.assertEquals("E/F", cm.evaluate( "0x:E/0x:F"));
			Assert.assertEquals("E/F", cm.simplify( "0x:E/0x:F"));
			Assert.assertEquals("E/F", cm.evaluate( "E/F"));
			Assert.assertEquals("E/F", cm.simplify( "E/F"));
		} finally {
			cm.setNumeralBase(NumeralBase.dec);
		}

		Assert.assertEquals("0", cm.evaluate( "((((((0))))))"));
		Assert.assertEquals("0", cm.evaluate( "((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((0))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))"));


		/*	Assert.assertEquals("0.524", cm.evaluate( "30°").getResult());
		Assert.assertEquals("0.524", cm.evaluate( "(10+20)°").getResult());
		Assert.assertEquals("1.047", cm.evaluate( "(10+20)°*2").getResult());
		try {
			Assert.assertEquals("0.278", cm.evaluate( "30°^2").getResult());
			junit.framework.Assert.fail();
		} catch (ParseException e) {
			if ( !e.getMessage().equals("Power operation after postfix function is currently unsupported!") ) {
				junit.framework.Assert.fail();
			}
		}*//*

*//*		try {
			cm.setTimeout(5000);
			Assert.assertEquals("2", cm.evaluate( "2!").getResult());
		} finally {
			cm.setTimeout(3000);
		}*/

		CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry().add(new Var.Builder("t", (String) null));
		Assert.assertEquals("2t", cm.simplify( "∂(t^2,t)"));
		Assert.assertEquals("2t", cm.evaluate( "∂(t^2,t)"));
		CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry().add(new Var.Builder("t", "2"));
		Assert.assertEquals("2t", cm.simplify( "∂(t^2,t)"));
		Assert.assertEquals("4", cm.evaluate( "∂(t^2,t)"));

		Assert.assertEquals("-x+x*ln(x)", cm.simplify("∫(ln(x), x)"));
		Assert.assertEquals("-(x-x*ln(x))/(ln(2)+ln(5))", cm.simplify("∫(log(10, x), x)"));

		Assert.assertEquals("∫((ln(2)+ln(5))/ln(x), x)", cm.simplify("∫(ln(10)/ln(x), x)"));
		Assert.assertEquals("∫(ln(10)/ln(x), x)", Expression.valueOf("∫(log(x, 10), x)").expand().toString());
		Assert.assertEquals("∫((ln(2)+ln(5))/ln(x), x)", cm.simplify("∫(log(x, 10), x)"));
	}

	@Test
	public void testFormatting() throws Exception {
		final MathEngine ce = CalculatorLocatorImpl.getInstance().getEngine().getMathEngine0();

		Assert.assertEquals("12 345", ce.simplify( "12345"));

	}

	@Test
	public void testI() throws ParseException, CalculatorEvalException {
		final MathEngine cm = CalculatorLocatorImpl.getInstance().getEngine().getMathEngine0();

		Assert.assertEquals("-i", cm.evaluate( "i^3"));
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
		Assert.assertEquals("0.34+1.382i", cm.evaluate( "ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(100)))))))))))))))"));
		try {
			cm.evaluate( "cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos())))))))))))))))))))))))))))))))))))");
			Assert.fail();
		} catch (ParseException e) {
		}

		final AngleUnit defaultAngleUnit = cm.getAngleUnits();
		try {
			cm.setAngleUnits(AngleUnit.rad);
			Assert.assertEquals("0.739", cm.evaluate( "cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(cos(1))))))))))))))))))))))))))))))))))))"));
		} finally {
			cm.setAngleUnits(defaultAngleUnit);
		}

		CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry().add(new Var.Builder("si", 5d));
		Assert.assertEquals("5", cm.evaluate( "si"));

		try {
			cm.evaluate( "sin");
			Assert.fail();
		} catch (ParseException e) {
		}
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
			Assert.assertEquals("12'345'678.9", cm.evaluate( "1.23456789E7"));
			cm.setPrecision(10);
			Assert.assertEquals("12'345'678.9", cm.evaluate( "1.23456789E7"));
			Assert.assertEquals("123'456'789", cm.evaluate( "1.234567890E8"));
			Assert.assertEquals("1'234'567'890.1", cm.evaluate( "1.2345678901E9"));
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

		Assert.assertEquals("0", cm.evaluate( "eq(0, 1)"));
		Assert.assertEquals("1", cm.evaluate( "eq(1, 1)"));
		Assert.assertEquals("1", cm.evaluate( "eq(1, 1.0)"));
		Assert.assertEquals("0", cm.evaluate( "eq(1, 1.000000000000001)"));
		Assert.assertEquals("0", cm.evaluate( "eq(1, 0)"));

		Assert.assertEquals("1", cm.evaluate( "lt(0, 1)"));
		Assert.assertEquals("0", cm.evaluate( "lt(1, 1)"));
		Assert.assertEquals("0", cm.evaluate( "lt(1, 0)"));

		Assert.assertEquals("0", cm.evaluate( "gt(0, 1)"));
		Assert.assertEquals("0", cm.evaluate( "gt(1, 1)"));
		Assert.assertEquals("1", cm.evaluate( "gt(1, 0)"));

		Assert.assertEquals("1", cm.evaluate( "ne(0, 1)"));
		Assert.assertEquals("0", cm.evaluate( "ne(1, 1)"));
		Assert.assertEquals("1", cm.evaluate( "ne(1, 0)"));

		Assert.assertEquals("1", cm.evaluate( "le(0, 1)"));
		Assert.assertEquals("1", cm.evaluate( "le(1, 1)"));
		Assert.assertEquals("0", cm.evaluate( "le(1, 0)"));

		Assert.assertEquals("0", cm.evaluate( "ge(0, 1)"));
		Assert.assertEquals("1", cm.evaluate( "ge(1, 1)"));
		Assert.assertEquals("1", cm.evaluate( "ge(1, 0)"));

		Assert.assertEquals("0", cm.evaluate( "ap(0, 1)"));
		Assert.assertEquals("1", cm.evaluate( "ap(1, 1)"));
		//Assert.assertEquals("1", cm.evaluate( "ap(1, 1.000000000000001)").getResult());
		Assert.assertEquals("0", cm.evaluate( "ap(1, 0)"));

	}


	@Test
	public void testNumeralSystems() throws Exception {
		final MathEngine cm = CalculatorLocatorImpl.getInstance().getEngine().getMathEngine0();

		Assert.assertEquals("11 259 375", cm.evaluate( "0x:ABCDEF"));
		Assert.assertEquals("30 606 154.462", cm.evaluate( "0x:ABCDEF*e"));
		Assert.assertEquals("30 606 154.462", cm.evaluate( "e*0x:ABCDEF"));
		Assert.assertEquals("e", cm.evaluate( "e*0x:ABCDEF/0x:ABCDEF"));
		Assert.assertEquals("30 606 154.462", cm.evaluate( "0x:ABCDEF*e*0x:ABCDEF/0x:ABCDEF"));
		Assert.assertEquals("30 606 154.462", cm.evaluate( "c+0x:ABCDEF*e*0x:ABCDEF/0x:ABCDEF-c+0x:C-0x:C"));
		Assert.assertEquals("1 446 257 064 651.832", cm.evaluate( "28*28 * sin(28) - 0b:1101 + √(28) + exp ( 28) "));
		Assert.assertEquals("13", cm.evaluate( "0b:1101"));

		try {
			cm.evaluate( "0b:π");
			Assert.fail();
		} catch (ParseException e) {
			// ok
		}

		final NumeralBase defaultNumeralBase = cm.getNumeralBase();
		try{
			cm.setNumeralBase(NumeralBase.bin);
			Assert.assertEquals("101", cm.evaluate( "10+11"));
			Assert.assertEquals("10/11", cm.evaluate( "10/11"));

			cm.setNumeralBase(NumeralBase.hex);
			Assert.assertEquals("63 7B", cm.evaluate( "56CE+CAD"));
			Assert.assertEquals("E", cm.evaluate( "E"));
		} finally {
			cm.setNumeralBase(defaultNumeralBase);
		}
	}

	@Test
	public void testLog() throws Exception {
		final MathEngine cm = CalculatorLocatorImpl.getInstance().getEngine().getMathEngine0();

		Assert.assertEquals("∞", Expression.valueOf("1/0").numeric().toString());
		Assert.assertEquals("∞", Expression.valueOf("ln(10)/ln(1)").numeric().toString());

		// logarithm
		Assert.assertEquals("ln(x)/ln(base)", ((CustomFunction) cm.getFunctionsRegistry().get("log")).getContent());
		Assert.assertEquals("∞", cm.evaluate( "log(1, 10)"));
		Assert.assertEquals("3.322", cm.evaluate( "log(2, 10)"));
		Assert.assertEquals("1.431", cm.evaluate( "log(5, 10)"));
		Assert.assertEquals("0.96", cm.evaluate( "log(11, 10)"));
		Assert.assertEquals("1/(bln(a))", cm.simplify( "∂(log(a, b), b)"));
		Assert.assertEquals("-ln(b)/(aln(a)^2)", cm.simplify( "∂(log(a, b), a)"));

	}
}
