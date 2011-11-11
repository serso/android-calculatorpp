/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * User: serso
 * Date: 9/26/11
 * Time: 12:13 PM
 */
public class ToJsclTextProcessorTest {

	@BeforeClass
	public static void setUp() throws Exception {
		CalculatorEngine.instance.init(null, null);
	}

	@Test
	public void testSpecialCases() throws ParseException {
		final ToJsclTextProcessor preprocessor = new ToJsclTextProcessor();
		Assert.assertEquals( "3^10^10", preprocessor.process("3^10^10").toString());
	}

	@Test
	public void testProcess() throws Exception {
		final ToJsclTextProcessor preprocessor = new ToJsclTextProcessor();

		Assert.assertEquals( "", preprocessor.process("").toString());
		Assert.assertEquals( "()", preprocessor.process("[]").toString());
		Assert.assertEquals( "()*()", preprocessor.process("[][]").toString());
		Assert.assertEquals( "()*(1)", preprocessor.process("[][1]").toString());
		Assert.assertEquals( "(0)*(1)", preprocessor.process("[0][1]").toString());
		Assert.assertEquals( "(0)*(1*10^)", preprocessor.process("[0][1E]").toString());
		Assert.assertEquals( "(0)*(1*10^1)", preprocessor.process("[0][1E1]").toString());
		Assert.assertEquals( "(0)*(1*10^-1)", preprocessor.process("[0][1E-1]").toString());
		Assert.assertEquals( "(0)*(1.*10^-1)", preprocessor.process("[0][1.E-1]").toString());
		Assert.assertEquals( "(0)*(2*10^-1)", preprocessor.process("[0][2*E-1]").toString());
		Assert.assertEquals( "(0)*ln(1)*(2*10^-1)", preprocessor.process("[0]ln(1)[2*E-1]").toString());
		Assert.assertEquals( "sin(4)*asin(0.5)*√(2)", preprocessor.process("sin(4)asin(0.5)√(2)").toString());
		Assert.assertEquals( "sin(4)*cos(5)", preprocessor.process("sin(4)cos(5)").toString());
		Assert.assertEquals( "π*sin(4)*π*cos(√(5))", preprocessor.process("πsin(4)πcos(√(5))").toString());
		Assert.assertEquals( "π*sin(4)+π*cos(√(5))", preprocessor.process("πsin(4)+πcos(√(5))").toString());
		Assert.assertEquals( "π*sin(4)+π*cos(√(5+(√(-1))))", preprocessor.process("πsin(4)+πcos(√(5+i))").toString());
		Assert.assertEquals( "π*sin(4.01)+π*cos(√(5+(√(-1))))", preprocessor.process("πsin(4.01)+πcos(√(5+i))").toString());
		Assert.assertEquals( "e^π*sin(4.01)+π*cos(√(5+(√(-1))))", preprocessor.process("e^πsin(4.01)+πcos(√(5+i))").toString());
		Assert.assertEquals( "e^π*sin(4.01)+π*cos(√(5+(√(-1))))*10^2", preprocessor.process("e^πsin(4.01)+πcos(√(5+i))E2").toString());
		Assert.assertEquals( "e^π*sin(4.01)+π*cos(√(5+(√(-1))))*10^-2", preprocessor.process("e^πsin(4.01)+πcos(√(5+i))E-2").toString());
		Assert.assertEquals( "10^2", preprocessor.process("E2").toString());
		Assert.assertEquals( "10^-2", preprocessor.process("E-2").toString());
		Assert.assertEquals( "10^-1/2", preprocessor.process("E-1/2").toString());
		Assert.assertEquals( "10^-1.2", preprocessor.process("E-1.2").toString());
		Assert.assertEquals( "10^(-1.2)", preprocessor.process("E(-1.2)").toString());
		Assert.assertEquals( "10^10^", preprocessor.process("EE").toString());
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
	public void testDegrees() throws Exception {
		final ToJsclTextProcessor preprocessor = new ToJsclTextProcessor();

		Assert.assertEquals( "", preprocessor.process("").toString());
	/*	try {
			Assert.assertEquals( "π/180", preprocessor.process("°").toString());
		} catch (ParseException e) {
			if ( !e.getMessage().startsWith("Could not find start of prefix") ){
				junit.framework.Assert.fail();
			}
		}
		Assert.assertEquals( "1*π/180", preprocessor.process("1°").toString());
		Assert.assertEquals( "20.0*π/180", preprocessor.process("20.0°").toString());
		Assert.assertEquals( "sin(30*π/180)", preprocessor.process("sin(30°)").toString());
		Assert.assertEquals( "asin(sin(π/6))*π/180", preprocessor.process("asin(sin(π/6))°").toString());
		Assert.assertEquals( "1*π/180*sin(1)", preprocessor.process("1°sin(1)").toString());
		try {
			Assert.assertEquals( "1*π/180^sin(1)", preprocessor.process("1°^sin(1)").toString());
			junit.framework.Assert.fail();
		} catch (ParseException e) {
			if ( !e.getMessage().equals("Power operation after postfix function is currently unsupported!") ) {
				junit.framework.Assert.fail();
			}
		}*/

	}

	@Test
	public void testPostfixFunction() throws Exception {
	}
}
