package org.solovyev.android.calculator.model;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

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
		Assert.assertEquals("(e)", tp.process("(2.718281828459045)"));
		Assert.assertEquals("123 456 789e", tp.process("123456789*2.718281828459045"));
		Assert.assertEquals("123 456 789e", tp.process("123 456 789 * 2.718281828459045"));
		Assert.assertEquals("t11e", tp.process("t11*2.718281828459045"));
		Assert.assertEquals("e", tp.process("2.718281828459045"));
		Assert.assertEquals("tee", tp.process("t2.718281828459045*2.718281828459045"));

		CalculatorEngine.instance.getVarsRegister().addVar(null, new Var.Builder("t2.718281828459045", "2"));
		Assert.assertEquals("t2.718281828459045e", tp.process("t2.718281828459045*2.718281828459045"));
		Assert.assertEquals("t×", tp.process("t*"));
		Assert.assertEquals("×t", tp.process("*t"));
		Assert.assertEquals("t×2", tp.process("t*2"));
		Assert.assertEquals("2×t", tp.process("2*t"));
		CalculatorEngine.instance.getVarsRegister().addVar(null, new Var.Builder("t", (String)null));
		Assert.assertEquals("t×", tp.process("t*"));
		Assert.assertEquals("×t", tp.process("*t"));

		Assert.assertEquals("t2", tp.process("t*2"));
		Assert.assertEquals("2t", tp.process("2*t"));

		Assert.assertEquals("t^2×2", tp.process("t^2*2"));
		Assert.assertEquals("2t^2", tp.process("2*t^2"));

		Assert.assertEquals("t^[2×2t]", tp.process("t^[2*2*t]"));
		Assert.assertEquals("2t^2[2t]", tp.process("2*t^2[2*t]"));

		CalculatorEngine.instance.getVarsRegister().addVar(null, new Var.Builder("k", (String)null));
		Assert.assertEquals("(t+2k)[k+2t]", tp.process("(t+2*k)*[k+2*t]"));
		Assert.assertEquals("(te+2k)e[k+2te]", tp.process("(t*e+2*k)*e*[k+2*t*e]"));


		Assert.assertEquals("tln(3)", tp.process("t*log(3)"));
		Assert.assertEquals("t√(3)", tp.process("t*sqrt(3)"));

	}
}
