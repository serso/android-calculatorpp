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
		CalculatorModel.instance.init(null, null);
	}

	@Test
	public void testProcess() throws Exception {
		final ToJsclTextProcessor preprocessor = new ToJsclTextProcessor();

		Assert.assertEquals( "sin(4)*cos(5)", preprocessor.process("sin(4)cos(5)"));
		Assert.assertEquals( "3.141592653589793*sin(4)*3.141592653589793*cos(sqrt(5))", preprocessor.process("πsin(4)πcos(√(5))"));
		Assert.assertEquals( "3.141592653589793*sin(4)+3.141592653589793*cos(sqrt(5))", preprocessor.process("πsin(4)+πcos(√(5))"));
		Assert.assertEquals( "3.141592653589793*sin(4)+3.141592653589793*cos(sqrt(5+sqrt(-1)))", preprocessor.process("πsin(4)+πcos(√(5+i))"));
		Assert.assertEquals( "3.141592653589793*sin(4.01)+3.141592653589793*cos(sqrt(5+sqrt(-1)))", preprocessor.process("πsin(4.01)+πcos(√(5+i))"));
		Assert.assertEquals( "2.718281828459045^3.141592653589793*sin(4.01)+3.141592653589793*cos(sqrt(5+sqrt(-1)))", preprocessor.process("e^πsin(4.01)+πcos(√(5+i))"));
		Assert.assertEquals( "2.718281828459045^3.141592653589793*sin(4.01)+3.141592653589793*cos(sqrt(5+sqrt(-1)))*10^2", preprocessor.process("e^πsin(4.01)+πcos(√(5+i))E2"));
		Assert.assertEquals( "2.718281828459045^3.141592653589793*sin(4.01)+3.141592653589793*cos(sqrt(5+sqrt(-1)))*10^-2", preprocessor.process("e^πsin(4.01)+πcos(√(5+i))E-2"));
		Assert.assertEquals( "log(foo)", preprocessor.process("ln()"));
		Assert.assertEquals( "log(foo)*log(foo)", preprocessor.process("ln()ln()"));
		Assert.assertEquals( "2.718281828459045*log(foo)*2.718281828459045*log(foo)*log(foo)*log(foo)*log(foo)*2.718281828459045", preprocessor.process("eln()eln()ln()ln()ln()e"));
		Assert.assertEquals( "log(log(log(log(log(log(log(log(log(log(log(log(log(log(log(foo)))))))))))))))", preprocessor.process("ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln(ln()))))))))))))))"));

	}

	@Test
	public void testPostfixFunctionsProcessing() throws Exception {
		final ToJsclTextProcessor preprocessor = new ToJsclTextProcessor();

		Assert.assertEquals(-1, preprocessor.getPostfixFunctionStart("5!", 0));
		Assert.assertEquals(0, preprocessor.getPostfixFunctionStart("!", 0));
		Assert.assertEquals(-1, preprocessor.getPostfixFunctionStart("5.4434234!", 8));
		Assert.assertEquals(1, preprocessor.getPostfixFunctionStart("2+5!", 2));
		Assert.assertEquals(4, preprocessor.getPostfixFunctionStart("2.23+5.4434234!", 13));
		Assert.assertEquals(14, preprocessor.getPostfixFunctionStart("2.23+5.4434234*5!", 15));
		Assert.assertEquals(14, preprocessor.getPostfixFunctionStart("2.23+5.4434234*5.1!", 17));
		Assert.assertEquals(4, preprocessor.getPostfixFunctionStart("2.23+(5.4434234*5.1)!", 19));
		Assert.assertEquals(4, preprocessor.getPostfixFunctionStart("2.23+(5.4434234*(5.1+1))!", 23));
		Assert.assertEquals(4, preprocessor.getPostfixFunctionStart("2.23+(5.4434234*sin(5.1+1))!", 26));
		Assert.assertEquals(0, preprocessor.getPostfixFunctionStart("sin(5)!", 5));
		Assert.assertEquals(0, preprocessor.getPostfixFunctionStart("sin(5sin(5sin(5)))!", 17));
		Assert.assertEquals(2, preprocessor.getPostfixFunctionStart("2+sin(5sin(5sin(5)))!", 19));
		Assert.assertEquals(5, preprocessor.getPostfixFunctionStart("2.23+sin(5.4434234*sin(5.1+1))!", 29));
	}
}
