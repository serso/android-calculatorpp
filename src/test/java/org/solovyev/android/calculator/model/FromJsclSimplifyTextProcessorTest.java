package org.solovyev.android.calculator.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.solovyev.android.calculator.jscl.JsclOperation;

/**
 * User: serso
 * Date: 10/20/11
 * Time: 3:43 PM
 */
public class FromJsclSimplifyTextProcessorTest {

	@BeforeClass
	public static void setUp() throws Exception {
		CalculatorEngine.instance.init(null, null);
	}

	@Test
	public void testProcess() throws Exception {
		FromJsclSimplifyTextProcessor tp = new FromJsclSimplifyTextProcessor();
		Assert.assertEquals("t11×e", tp.process("t11*2.718281828459045"));
		Assert.assertEquals("e", tp.process("2.718281828459045"));
		Assert.assertEquals("te×e", tp.process("t2.718281828459045*2.718281828459045"));

		CalculatorEngine.instance.getVarsRegister().addVar(null, new Var.Builder("t2.718281828459045", "2"));
		Assert.assertEquals("t2.718281828459045×e", tp.process("t2.718281828459045*2.718281828459045"));

	}
}
